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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.azafiu.hibernatetest.entities.ProductEntity;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to query for entities and audited information.
 * 
 * @author andrei.zafiu
 * 
 */
@Component
public class ProductDAO extends CommonDAO {

    /**
     * Get all products that need to be shown to the checker for approval.
     * 
     * @return a list of Object[]. Each element will be an Object[3] array with
     *         the following items: Object[0] - the {@link ProductEntity} at a
     *         revision ( greater or equal than the one given as parameter)
     *         Object[1] a {@link DefaultRevisionEntity} Object[2] a
     *         {@link RevisionType} object containing information about the
     *         revision
     */
    @Transactional
    public List<Object[]> getAllProductsWaitingForApproval() {
        /**
         * Get all distinct {@link ProductEntity} objects where the wasChecked
         * property is false order by modified descending
         */
        final AuditQuery query = this.getAuditReader()
                                     .createQuery()
                                     .forRevisionsOfEntity(ProductEntity.class,
                                                           false,
                                                           true)
                                     .addOrder(AuditEntity.property("modified")
                                                          .desc())
                                     .add(AuditEntity.revisionNumber()
                                                     .maximize()
                                                     .computeAggregationInInstanceContext())
                                     .add(AuditEntity.property("wasChecked")
                                                     .eq(Boolean.FALSE))
                                     .add(AuditEntity.revisionType()
                                                     .ne(RevisionType.DEL));

        final List<Object[]> resultList = query.getResultList();

        final List<Object[]> result = new ArrayList<>();

        /**
         * for each "changed" object found in the db we need to check if there
         * is a newer revision of it in which the {@link ProductEntity} was
         * approved (wasChecked = true) because we do not need to retrieve
         * already checked objects to the checker.
         */
        for (final Object[] change : resultList) {
            final ProductEntity pe = (ProductEntity) change[0];
            final AuditQuery queryForWasCheckedTrue = this.getAuditReader()
                                                          .createQuery()
                                                          .forRevisionsOfEntity(ProductEntity.class,
                                                                                false,
                                                                                true)
                                                          .addOrder(AuditEntity.property("modified")
                                                                               .desc())
                                                          .add(AuditEntity.id()
                                                                          .eq(pe.getId()))
                                                          .add(AuditEntity.property("wasChecked")
                                                                          .eq(Boolean.TRUE));

            if (pe.getModified() != null) {
                queryForWasCheckedTrue.add(AuditEntity.property("modified")
                                                      .gt(pe.getModified()));
            }

            try {
                final Object[] trueWasChecked = (Object[]) queryForWasCheckedTrue.getSingleResult();
            }
            catch (final NoResultException ex) {
                // there is no newer revision where the current product has
                // wasChecked property == true
                result.add(change);
            }

        }

        return result;
    }

    /**
     * Retrieve the previous state of a given {@link ProductEntity} which was
     * modified earlier than the given modified date.
     * 
     * @param prodId
     *            the id of the {@link ProductEntity}
     * @param revNumber
     *            the revision number when the productDetails was modified
     * @return a {@link ProductEntity} object
     */
    @Transactional
    public ProductEntity getPreviousStateForProduct(final Long prodId,
                                                    final int revNumber) {
        /**
         * Get only the most recent {@link ProductEntity} information from the
         * audit tables where the wasChecked property is true and the
         * modifiedDate is less than the one given as parameter for the given
         * product details object
         */
        final AuditQuery query = this.getAuditReader()
                                     .createQuery()
                                     .forRevisionsOfEntity(ProductEntity.class,
                                                           true,
                                                           true)
                                     .addOrder(AuditEntity.property("modified")
                                                          .desc())
                                     .add(AuditEntity.id().eq(prodId))
                                     .add(AuditEntity.property("wasChecked")
                                                     .eq(Boolean.TRUE))
                                     .add(AuditEntity.revisionNumber()
                                                     .lt(Integer.valueOf(revNumber)))
                                     .setMaxResults(1);

        final List<ProductEntity> resultList = query.getResultList();

        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0);
        }

        return null;

    }

}
