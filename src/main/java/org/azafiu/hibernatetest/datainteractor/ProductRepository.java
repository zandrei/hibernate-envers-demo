package org.azafiu.hibernatetest.datainteractor;

import java.util.List;

import org.azafiu.hibernatetest.entities.ProductEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the {@link ProductEntity} object
 * 
 * @author andrei.zafiu
 * 
 */
public interface ProductRepository extends CrudRepository<ProductEntity, Long> {

    /**
     * Get all {@link ProductEntity} objects with active and wasChecked
     * properties set to the ones given as parameters.
     * 
     * @param active
     *            the value of the active property
     * @param wasChecked
     *            the value for the wasChecked property
     * @return a list of {@link ProductEntity}
     */
    List<ProductEntity> findByActiveAndWasChecked(final Boolean active,
                                                  final Boolean wasChecked);

    /**
     * Get all {@link ProductEntity} objects with wasChecked property set to the
     * one given as parameter.
     * 
     * @param wasChecked
     *            the value of the wasChecked property
     * @return a list of {@link ProductEntity}
     */
    List<ProductEntity> findByWasChecked(final Boolean wasChecked);

}
