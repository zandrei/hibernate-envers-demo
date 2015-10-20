package org.azafiu.hibernatetest.datainteractor;

import java.util.List;

import org.azafiu.hibernatetest.entities.ProductDetailsEntity;
import org.azafiu.hibernatetest.entities.ProductEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the {@link ProductDetailsEntity} object
 * 
 * @author andrei.zafiu
 * 
 */
public interface ProductDetailsRepository extends
        CrudRepository<ProductDetailsEntity, Long> {

    /**
     * Select all product details which have an active flag set to true and are
     * related to the product with the given id.
     * 
     * @param productId
     *            the id of the {@link ProductEntity}
     * @return a List of {@link ProductDetailsEntity}
     */
    @Query("Select pd from ProductDetailsEntity pd where pd.active = true and pd.product.id = ?1")
            List<ProductDetailsEntity>
            findAllActiveForProduct(final Long productId);

}
