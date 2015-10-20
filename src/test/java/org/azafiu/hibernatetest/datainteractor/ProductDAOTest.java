package org.azafiu.hibernatetest.datainteractor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.azafiu.hibernatetest.Application;
import org.azafiu.hibernatetest.datainteractor.ProductDAO;
import org.azafiu.hibernatetest.datainteractor.ProductRepository;
import org.azafiu.hibernatetest.entities.ProductEntity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductDAOTest {

	@Autowired
	private ProductDAO productDao;

	@Autowired
	private ProductRepository productRepo;

	@Test
	public void getAllProductsWaitingForApproval_WhenNoProductAdded_ThenReturnsEmptyList() {
		final List<Object[]> results = this.productDao.getAllProductsWaitingForApproval();

		Assert.assertNotNull(results);
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void getAllProductsWaitingForApproval_WhenOneCheckedProductAdded_ThenReturnsEmptyList() {
		ProductEntity pe = new ProductEntity();

		pe.setActive(true);
		pe.setName("Product 1");
		pe.setPrice(Double.valueOf(2.4));
		pe.setWasChecked(Boolean.TRUE);

		this.productRepo.save(pe);

		final List<Object[]> results = this.productDao.getAllProductsWaitingForApproval();

		Assert.assertNotNull(results);
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void getAllProductsWaitingForApproval_WhenOneUncheckedProductAddedAndThenUpdatedToChecked_ThenReturnsEmptyList() {
		ProductEntity pe = new ProductEntity();

		pe.setActive(true);
		pe.setName("Product 1");
		pe.setPrice(Double.valueOf(2.4));
		pe.setWasChecked(Boolean.FALSE);

		this.productRepo.save(pe);

		pe.setWasChecked(Boolean.TRUE);

		this.productRepo.save(pe);

		final List<Object[]> results = this.productDao.getAllProductsWaitingForApproval();

		Assert.assertNotNull(results);
		Assert.assertTrue(results.isEmpty());
	}

	@Test
	public void getAllProductsWaitingForApproval_WhenOneUncheckedProductPreviouslyAdded_ThenReturnsProduct() {
		ProductEntity pe = new ProductEntity();

		pe.setActive(true);
		pe.setName("Product 1");
		pe.setPrice(Double.valueOf(2.4));
		pe.setWasChecked(Boolean.FALSE);

		this.productRepo.save(pe);

		final List<Object[]> results = this.productDao.getAllProductsWaitingForApproval();

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		final Object[] auditedRevision = results.get(0);
		Assert.assertNotNull(auditedRevision[0]);
		assertThat(auditedRevision[0], instanceOf(ProductEntity.class));

		pe = (ProductEntity) auditedRevision[0];
		Assert.assertEquals("Product 1", pe.getName());
		Assert.assertEquals(Boolean.TRUE, pe.getActive());
		Assert.assertEquals(Boolean.FALSE, pe.getWasChecked());
		Assert.assertEquals(Double.valueOf(2.4), pe.getPrice());
	}

	@Test
	public void getPreviousStateForProduct_WhenProductAddedAndThenModified_ThenReturnsTheInitialStateOfTheProduct() {

		ProductEntity pe = new ProductEntity();

		pe.setActive(true);
		pe.setName("Product 1");
		pe.setPrice(Double.valueOf(2.4));
		pe.setWasChecked(Boolean.TRUE);

		pe = this.productRepo.save(pe);

		pe.setActive(true);
		pe.setName("Product 2");
		pe.setPrice(Double.valueOf(2.4));
		pe.setWasChecked(Boolean.FALSE);

		this.productRepo.save(pe);

		final Object[] productRevision = this.productDao.getLatestChangeForItemWithID(pe.getId(), ProductEntity.class);
		Assert.assertEquals(3, productRevision.length);
		Assert.assertTrue(DefaultRevisionEntity.class.isInstance(productRevision[1]));

		final DefaultRevisionEntity revEnt = (DefaultRevisionEntity) productRevision[1];

		final ProductEntity prodEnt = this.productDao.getPreviousStateForProduct(pe.getId(), revEnt.getId());

		Assert.assertEquals("Product 1", prodEnt.getName());
		Assert.assertEquals(Boolean.TRUE, prodEnt.getActive());
		Assert.assertEquals(Boolean.TRUE, prodEnt.getWasChecked());
		Assert.assertEquals(Double.valueOf(2.4), prodEnt.getPrice());
	}

}
