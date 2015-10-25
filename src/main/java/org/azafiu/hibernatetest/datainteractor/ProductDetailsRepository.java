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
