package org.azafiu.hibernatetest.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * Entity for Product table.
 * 
 * @author andrei.zafiu
 * 
 */
@Entity
@Audited(withModifiedFlag = true)
@Table(name = "Product")
public class ProductEntity extends BaseEntity {

    private static final long          serialVersionUID = 451000601225951662L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_PRODUCT")
    private Long                       id;

    @Column(name = "name")
    private String                     name;

    @Column(name = "price")
    private Double                     price;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @NotAudited
    private List<ProductDetailsEntity> productDetails;

    public ProductEntity
            addAllProductDetail(final List<ProductDetailsEntity> details) {

        this.productDetails.clear();

        for (final ProductDetailsEntity detail : details) {
            this.addProductDetail(detail);
        }

        return this;
    }

    public ProductEntity addProductDetail(final ProductDetailsEntity detail) {
        detail.setProduct(this);
        this.productDetails.add(detail);
        return this;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return this.price;
    }

    /**
     * @return the productDetails
     */
    public List<ProductDetailsEntity> getProductDetails() {
        if (this.productDetails == null) {
            this.productDetails = new ArrayList<>();
        }
        return this.productDetails;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(final Double price) {
        this.price = price;
    }

    /**
     * @param productDetails
     *            the productDetails to set
     */
    public void
            setProductDetails(final List<ProductDetailsEntity> productDetails) {
        this.productDetails = productDetails;
    }

}
