/*******************************************************************************
 * Copyright 2005-2015 Open Source Applications Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
