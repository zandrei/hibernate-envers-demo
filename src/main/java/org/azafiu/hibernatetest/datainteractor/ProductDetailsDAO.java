package org.azafiu.hibernatetest.datainteractor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.azafiu.hibernatetest.entities.ProductDetailsEntity;
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
public class ProductDetailsDAO extends CommonDAO {

    /**
     * Query for all {@link ProductDetailsEntity} objects linked to a product at
     * a given revision.
     * 
     * @param productId
     *            the id of the related product
     * @param revisionDate
     *            the date of the product change revision
     * @return a list of Object[]. Each element will be an Object[3] array with
     *         the following items: Object[0] - the {@link ProductDetailsEntity}
     *         at a revision ( greater or equal than the one given as parameter)
     *         Object[1] a {@link DefaultRevisionEntity} Object[2] a
     *         {@link RevisionType} object containing information about the
     *         revision
     */
    @Transactional
    public List<Object[]>
            getAllProductDetailsForProductAtRevision(final Long productId,
                                                     final Date revisionDate) {

        // get the revision number based on the revision date
        final Number revNumber = this.getAuditReader()
                                     .getRevisionNumberForDate(revisionDate);

        /**
         * Query the audit table for {@link ProductDetailsEntity}, order the
         * results descending based on modified property of the
         * {@link ProductDetailsEntity} object, get the list of objects with
         * revision greater or equal than the one given as parameter (the
         * {@link ProductDetailsEntity} may have been added in a different
         * revision(after the product was persisted in the database) than the
         * {@link ProductEntity} and we need to retrieve it) with the foreign
         * key for the product set to the one given as parameter and with the
         * wasChecked property set to false
         */
        final AuditQuery query = this.getAuditReader()
                                     .createQuery()
                                     .forRevisionsOfEntity(ProductDetailsEntity.class,
                                                           false,
                                                           false)
                                     .addOrder(AuditEntity.property("modified")
                                                          .desc())
                                     .add(AuditEntity.revisionNumber()
                                                     .ge(revNumber))
                                     .add(AuditEntity.property("fkProduct")
                                                     .eq(productId))
                                     .add(AuditEntity.property("wasChecked")
                                                     .eq(Boolean.FALSE));

        final List<Object[]> resultList = query.getResultList();

        final List<Object[]> result = new ArrayList<>();

        /**
         * for each "changed" object found in the db we need to check if there
         * is a newer revision of it in which the {@link ProductDetailsEntity}
         * was approved (wasChecked = true) because we do not need to retrieve
         * already checked objects to the checker.
         */
        for (final Object[] change : resultList) {
            final ProductDetailsEntity pe = (ProductDetailsEntity) change[0];
            final AuditQuery queryForWasCheckedTrue = this.getAuditReader()
                                                          .createQuery()
                                                          .forRevisionsOfEntity(ProductDetailsEntity.class,
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
     * Retrieve the previous state of a given {@link ProductDetailsEntity} which
     * was modified earlier than the given modified date.
     * 
     * @param prodDetailsId
     *            the id of the {@link ProductDetailsEntity}
     * @param modifiedDate
     *            the date when the productDetails was modified
     * @return a {@link ProductDetailsEntity} object
     */
    @Transactional
    public ProductDetailsEntity
            getPreviousStateForProductDetails(final Long prodDetailsId,
                                              final int revNumber) {
        /**
         * Get only the most recent {@link ProductDetailsEntity} information
         * from the audit tables where the wasChecked property is true and the
         * modifiedDate is less than the one given as parameter for the given
         * product details object
         */
        final AuditQuery query = this.getAuditReader()
                                     .createQuery()
                                     .forRevisionsOfEntity(ProductDetailsEntity.class,
                                                           true,
                                                           true)
                                     .addOrder(AuditEntity.property("modified")
                                                          .desc())
                                     .add(AuditEntity.id().eq(prodDetailsId))
                                     .add(AuditEntity.property("wasChecked")
                                                     .eq(Boolean.TRUE))
                                     .add(AuditEntity.revisionNumber()
                                                     .lt(Integer.valueOf(revNumber)))
                                     .setMaxResults(1);

        final List<ProductDetailsEntity> resultList = query.getResultList();

        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0);
        }

        return null;
    }

}
