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
package org.azafiu.hibernatetest.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity for Product_details table
 * 
 * @author andrei.zafiu
 * 
 */
@Entity
@Audited(withModifiedFlag = true)
@Table(name = "PRODUCT_DETAILS")
public class ProductDetailsEntity extends BaseEntity {

    private static final long serialVersionUID = -4391984901146921286L;

    @Column(name = "EXPIRATION_DATE")
    private Date              expirationDate;

    /**
     * the column mapping is needed to be able to query audit entity for
     * FK_PRODUCT column values and changes
     */
    @Column(name = "FK_PRODUCT", insertable = false, updatable = false)
    private Long              fkProduct;

    @Id
    @GeneratedValue
    @Column(name = "ID_PRODUCT_DETAILS")
    private Long              id;

    /**
     * The @JsonIgnore property is needed to avoid cyclic construction of JSON
     * when marshalling a product entity to JSON
     */
    @ManyToOne
    @JoinColumn(name = "FK_PRODUCT")
    @JsonIgnore
    private ProductEntity     product;

    @Column(name = "STORE_NAME")
    private String            storeName;

    /**
     * @return the expirationDate
     */
    public Date getExpirationDate() {
        return this.expirationDate;
    }

    /**
     * @return the fkProduct
     */
    public Long getFkProduct() {
        return this.fkProduct;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * @return the product
     */
    public ProductEntity getProduct() {
        return this.product;
    }

    /**
     * @return the storeName
     */
    public String getStoreName() {
        return this.storeName;
    }

    /**
     * @param expirationDate
     *            the expirationDate to set
     */
    public void setExpirationDate(final Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @param fkProduct
     *            the fkProduct to set
     */
    public void setFkProduct(final Long fkProduct) {
        this.fkProduct = fkProduct;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;

    }

    /**
     * @param product
     *            the product to set
     */
    public void setProduct(final ProductEntity product) {
        this.product = product;
    }

    /**
     * @param storeName
     *            the storeName to set
     */
    public void setStoreName(final String storeName) {
        this.storeName = storeName;
    }

}
