package org.azafiu.hibernatetest.datainteractor;

import java.util.Date;
import java.util.List;

import org.azafiu.hibernatetest.Application;
import org.azafiu.hibernatetest.entities.ProductDetailsEntity;
import org.azafiu.hibernatetest.entities.ProductEntity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductDetailsDAOTest {

	final ProductDetailsEntity detail1 = new ProductDetailsEntity();

	final ProductDetailsEntity detail2 = new ProductDetailsEntity();

	private Date insertDate;

	ProductEntity pe;

	@Autowired
	private ProductDAO productDao;

	@Autowired
	private ProductDetailsDAO productDetailsDao;

	@Autowired
	private ProductDetailsRepository productDetailsRepo;

	@Autowired
	private ProductRepository productRepo;

	private void assertProperObjectsReturned(final Object[] revisionObject, final ProductDetailsEntity pde) {

		Assert.assertEquals(3, revisionObject.length);
		Assert.assertTrue(ProductDetailsEntity.class.isInstance(revisionObject[0]));
		Assert.assertTrue(DefaultRevisionEntity.class.isInstance(revisionObject[1]));
		Assert.assertTrue(RevisionType.class.isInstance(revisionObject[2]));

		final ProductDetailsEntity pd = (ProductDetailsEntity) revisionObject[0];
		final RevisionType rt = (RevisionType) revisionObject[2];

		Assert.assertEquals(RevisionType.ADD, rt);

		Assert.assertEquals(pde.getStoreName(), pd.getStoreName());
		Assert.assertEquals(pde.getExpirationDate(), pd.getExpirationDate());
		Assert.assertEquals(pde.getActive(), pd.getActive());
		Assert.assertEquals(pde.getWasChecked(), pd.getWasChecked());
	}

	@Test
	public void getAllProductDetailsForProductAtRevision_WhenOneProductAndTwoProdDetailsAdded_ThenReturnsTheTwoProductDetails() {
		this.initialize();

		final List<Object[]> results = this.productDetailsDao.getAllProductDetailsForProductAtRevision(this.pe.getId(),
				this.insertDate);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		final Object[] firstProdDetailsRev = results.get(0);
		final Object[] secondProdDetailsRev = results.get(1);

		this.assertProperObjectsReturned(firstProdDetailsRev, this.detail1);
		this.assertProperObjectsReturned(secondProdDetailsRev, this.detail2);
	}

	@Test
	public void getPreviousStateForProductDetails_WhenProductDetailsAddedAndThenModified_ThenReturnsTheInitialStateOfTheProductDetails() {

		ProductDetailsEntity pe = new ProductDetailsEntity();

		pe.setActive(true);
		pe.setStoreName("Product 1");
		pe.setExpirationDate(new Date());
		pe.setWasChecked(Boolean.TRUE);

		pe = this.productDetailsRepo.save(pe);

		pe.setActive(true);
		pe.setStoreName("Product 2");
		pe.setWasChecked(Boolean.FALSE);

		this.productDetailsRepo.save(pe);

		final Object[] latestProductDetails = this.productDetailsDao.getLatestChangeForItemWithID(pe.getId(),
				ProductDetailsEntity.class);
		Assert.assertNotNull(latestProductDetails);

		Assert.assertEquals(3, latestProductDetails.length);
		Assert.assertTrue(DefaultRevisionEntity.class.isInstance(latestProductDetails[1]));

		final DefaultRevisionEntity revEnt = (DefaultRevisionEntity) latestProductDetails[1];

		final ProductDetailsEntity prodEnt = this.productDetailsDao.getPreviousStateForProductDetails(pe.getId(),
				revEnt.getId());

		Assert.assertNotNull(prodEnt);
		Assert.assertEquals("Product 1", prodEnt.getStoreName());
		Assert.assertEquals(Boolean.TRUE, prodEnt.getActive());
		Assert.assertEquals(Boolean.TRUE, prodEnt.getWasChecked());
	}

	@Test
	public void getPreviousStateForProductDetails_WhenProductDetailsAdded_ThenReturnNULL() {

		ProductDetailsEntity pe = new ProductDetailsEntity();

		pe.setActive(true);
		pe.setStoreName("Product 1");
		pe.setExpirationDate(new Date());
		pe.setWasChecked(Boolean.TRUE);

		pe = this.productDetailsRepo.save(pe);

		final Object[] latestProductDetails = this.productDetailsDao.getLatestChangeForItemWithID(pe.getId(),
				ProductDetailsEntity.class);
		Assert.assertNotNull(latestProductDetails);

		Assert.assertEquals(3, latestProductDetails.length);
		Assert.assertTrue(DefaultRevisionEntity.class.isInstance(latestProductDetails[1]));

		final DefaultRevisionEntity revEnt = (DefaultRevisionEntity) latestProductDetails[1];

		final ProductDetailsEntity prodEnt = this.productDetailsDao.getPreviousStateForProductDetails(pe.getId(),
				revEnt.getId());

		Assert.assertNull(prodEnt);
	}

	@Transactional
	public void initialize() {

		this.pe = new ProductEntity();

		this.pe.setActive(true);
		this.pe.setName("Product 1");
		this.pe.setPrice(Double.valueOf(2.4));
		this.pe.setWasChecked(Boolean.FALSE);

		this.detail1.setStoreName("STORE_NAME");
		this.detail1.setExpirationDate(new Date());
		this.detail1.setWasChecked(Boolean.FALSE);
		this.detail1.setActive(true);

		this.detail2.setStoreName("STORE_NAME2");
		this.detail2.setExpirationDate(new Date());
		this.detail2.setWasChecked(Boolean.FALSE);
		this.detail2.setActive(true);

		this.pe = this.productRepo.save(this.pe);

		this.detail1.setProduct(this.pe);
		this.detail2.setProduct(this.pe);

		this.productDetailsRepo.save(this.detail1);
		this.productDetailsRepo.save(this.detail2);

		final List<Object[]> products = this.productDao.getAllProductsWaitingForApproval();
		Assert.assertNotNull(products);
		Assert.assertEquals(1, products.size());

		final Object[] productRevision = products.get(0);
		Assert.assertEquals(3, productRevision.length);
		Assert.assertTrue(DefaultRevisionEntity.class.isInstance(productRevision[1]));

		final DefaultRevisionEntity revEnt = (DefaultRevisionEntity) productRevision[1];
		this.insertDate = revEnt.getRevisionDate();
	}

}
