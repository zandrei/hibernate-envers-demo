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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for *DAO classes. It will return an {@link AuditReader} from the
 * current persistence context.
 * 
 * @author andrei.zafiu
 * 
 */
@Component
public class CommonDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Get an {@link AuditReader} from the {@link EntityManager} through the
     * {@link AuditReaderFactory}
     * 
     * @return an {@link AuditReader}
     */
    protected AuditReader getAuditReader() {
        return AuditReaderFactory.get(this.entityManager);
    }

    @Transactional
    public Object[] getLatestChangeForItemWithID(final Long id,
                                                 final Class<?> itemClass) {
        final AuditQuery query = this.getAuditReader()
                                     .createQuery()
                                     .forRevisionsOfEntity(itemClass,
                                                           false,
                                                           true)
                                     .addOrder(AuditEntity.property("modified")
                                                          .desc())
                                     .add(AuditEntity.id().eq(id))
                                     .setMaxResults(1);

        final List<Object[]> resultList = query.getResultList();

        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }

}
