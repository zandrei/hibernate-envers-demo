package org.azafiu.hibernatetest.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.azafiu.hibernatetest.datainteractor.ProductDAO;
import org.azafiu.hibernatetest.datainteractor.ProductDetailsDAO;
import org.azafiu.hibernatetest.datainteractor.ProductDetailsRepository;
import org.azafiu.hibernatetest.datainteractor.ProductRepository;
import org.azafiu.hibernatetest.dto.ProductDTO;
import org.azafiu.hibernatetest.dto.ProductDetailsDTO;
import org.azafiu.hibernatetest.entities.ProductDetailsEntity;
import org.azafiu.hibernatetest.entities.ProductEntity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Product
 * 
 * @author andrei.zafiu
 * 
 */
@RestController
@RequestMapping("/rest/product")
public class ProductRESTService {

    @Autowired
    private ProductDAO               productDao;

    @Autowired
    private ProductDetailsDAO        productDetailsDao;

    @Autowired
    private ProductDetailsRepository productDetailsRepository;

    @Autowired
    private ProductRepository        productRepository;

    /**
     * Retrieve the active products that need to be checked
     * 
     * @return a list of {@link ProductDTO} objects
     */
    @RequestMapping(value = "/checker", method = RequestMethod.GET,
            produces = "application/json")
    public List<ProductDTO> getAllActiveProductsForChecker() {

        /** Retrieve a list of products which need checker approval */
        final List<Object[]> productChanges = this.productDao.getAllProductsWaitingForApproval();

        final List<ProductDTO> results = new ArrayList<ProductDTO>();

        for (final Object[] revision : productChanges) {

            final ProductDTO dto = new ProductDTO();

            if (revision.length > 2
                && ProductEntity.class.isInstance(revision[0])) {

                /** get the current value for the {@link ProductDetailsEntity} */
                final ProductEntity currentValue = (ProductEntity) revision[0];
                if (RevisionType.class.isInstance(revision[2])) {

                    // get the {@link RevisionType} of the current change
                    final RevisionType rt = (RevisionType) revision[2];
                    dto.setOperation(rt.name());

                    // get the {@link DefaultRevisionEntity} and retrieve the
                    // revision date
                    final DefaultRevisionEntity revEntity = (DefaultRevisionEntity) revision[1];
                    final Date revDate = revEntity.getRevisionDate();
                    dto.setRevision(revDate.getTime());

                    // get all product details associated with the current
                    // product at the given revision
                    dto.setProductDetails(this.getAllProductDetailsForProductAtRevision(currentValue.getId(),
                                                                                        revDate.getTime()));

                    // if the revision type was 'MOD', search for the previous
                    // value of the product to construct the product dto
                    if (rt == RevisionType.MOD) {
                        final ProductEntity previousValue = this.productDao.getPreviousStateForProduct(currentValue.getId(),
                                                                                                       revEntity.getId());

                        dto.setInitial(previousValue);
                        dto.setChanges(currentValue);
                    }
                    else {
                        dto.setChanges(currentValue);
                    }
                }
                results.add(dto);
            }
        }

        return results;
    }

    /**
     * Get all active products to be shown to the "maker"
     * 
     * @return a list of {@link productEntity} objects
     */
    @RequestMapping(value = "/maker", method = RequestMethod.GET,
            produces = "application/json")
    public List<ProductEntity> getAllActiveProductsForMaker() {

        // find all active and checked {@link ProductEntity} objects
        final List<ProductEntity> result = this.productRepository.findByActiveAndWasChecked(Boolean.TRUE,
                                                                                            Boolean.TRUE);

        for (final ProductEntity product : result) {
            // call size method to initialize toMany Collection
            product.getProductDetails().size();
        }
        return result;
    }

    /**
     * Retrieve all {@link ProductDetailsDTO} related to the given product at
     * the given revision
     * 
     * @param prodId
     *            the id of the {@link ProductEntity}
     * @param revisionDateAsLong
     *            the timestamp of the revision as long
     * @return a list of {@link ProductDetailsDTO}
     */
    private List<ProductDetailsDTO>
            getAllProductDetailsForProductAtRevision(final Long prodId,
                                                     final Long revisionDateAsLong) {

        /**
         * retrieve the information about related product details from the
         * {@link ProductDetailsDAO}
         */
        final List<Object[]> changes = this.productDetailsDao.getAllProductDetailsForProductAtRevision(prodId,
                                                                                                       new Date(revisionDateAsLong));

        final List<ProductDetailsDTO> results = new ArrayList<ProductDetailsDTO>();

        for (final Object[] revision : changes) {

            final ProductDetailsDTO dto = new ProductDetailsDTO();
            if (revision.length > 2
                && ProductDetailsEntity.class.isInstance(revision[0])) {

                /** get the current value for the {@link ProductDetailsEntity} */
                final ProductDetailsEntity currentValue = (ProductDetailsEntity) revision[0];
                if (currentValue != null) {
                    currentValue.setFkProduct(prodId);
                }

                if (RevisionType.class.isInstance(revision[2])) {

                    // get the {@link RevisionType} of the current change
                    final RevisionType rt = (RevisionType) revision[2];
                    dto.setOperation(rt.name());

                    // if the revision type was 'MOD' get the previous value of
                    // the entity to be able to construct the DTO
                    if (rt == RevisionType.MOD) {
                        final DefaultRevisionEntity dre = (DefaultRevisionEntity) revision[1];
                        final ProductDetailsEntity previousValue = this.productDetailsDao.getPreviousStateForProductDetails(currentValue.getId(),
                                                                                                                            dre.getId());

                        if (previousValue != null) {
                            previousValue.setFkProduct(prodId);
                        }

                        dto.setInitial(previousValue);
                        dto.setChanges(currentValue);
                    }
                    else {
                        dto.setChanges(currentValue);
                    }
                }
                results.add(dto);
            }
        }

        return results;
    }

    /**
     * Remove a list of products.
     * 
     * @param products
     *            a list of {@link ProductEntity} objects to be deleted
     */
    @RequestMapping(value = "/batch-remove", method = RequestMethod.POST,
            consumes = "application/json")
    public void
            removeProductBatch(@RequestBody final List<ProductEntity> products) {

        this.productRepository.delete(products);

    }

    /**
     * Save a {@link ProductEntity}
     * 
     * @param product
     */
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public void saveProduct(@RequestBody final ProductEntity product) {

        this.productRepository.save(product);

    }

    /**
     * Save a batch of {@link ProductEntity}
     * 
     * @param products
     *            a list of {@link ProductEntity} to be added
     */
    @RequestMapping(value = "/batch", method = RequestMethod.POST,
            consumes = "application/json")
    @Transactional
    public void
            saveProductBatch(@RequestBody final List<ProductEntity> products) {

        // save the products
        this.productRepository.save(products);

        // save the related {@link ProductDetailsEntity} objects
        for (int i = 0; i < products.size(); i++) {
            final ProductEntity product = products.get(i);

            if (product.getProductDetails() != null) {
                for (final ProductDetailsEntity pde : product.getProductDetails()) {
                    pde.setProduct(product);
                    this.productDetailsRepository.save(pde);
                }
            }

        }
    }
}
