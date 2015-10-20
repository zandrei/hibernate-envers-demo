package org.azafiu.hibernatetest.datainteractor;

import java.util.List;

import org.azafiu.hibernatetest.Application;
import org.azafiu.hibernatetest.datainteractor.ProductDetailsRepository;
import org.azafiu.hibernatetest.datainteractor.ProductRepository;
import org.azafiu.hibernatetest.entities.ProductEntity;
import org.junit.Assert;
import org.junit.Before;
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
public class ProductRepositoryTest {

    @Autowired
    private ProductDetailsRepository prodDetailsRepository;

    @Autowired
    private ProductRepository        prodRepository;

    @Test
    public void
            findByActiveAndWasChecked_WhenBothParametersAreFalse_ThenReturnEmptySet() {
        final List<ProductEntity> list = this.prodRepository.findByActiveAndWasChecked(false,
                                                                                       false);

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 0);
    }

    @Test
    public void
            findByActiveAndWasChecked_WhenBothParametersAreTrue_ThenReturnOneProduct() {
        final List<ProductEntity> list = this.prodRepository.findByActiveAndWasChecked(true,
                                                                                       true);

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void
            findByActiveAndWasChecked_WhenSearchingForWasActiveFalse_ThenReturnEmptySet() {
        final List<ProductEntity> list = this.prodRepository.findByActiveAndWasChecked(false,
                                                                                       true);

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 0);
    }

    @Test
    public void
            findByActiveAndWasChecked_WhenSearchingForWasCheckedFalse_ThenReturnEmptySet() {
        final List<ProductEntity> list = this.prodRepository.findByActiveAndWasChecked(true,
                                                                                       false);

        Assert.assertNotNull(list);
        Assert.assertTrue(list.size() == 0);
    }

    @Before
    public void setUp() {
        final ProductEntity pe = new ProductEntity();
        pe.setActive(Boolean.TRUE);
        pe.setWasChecked(Boolean.TRUE);

        this.prodRepository.save(pe);
    }
}
