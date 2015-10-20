package org.azafiu.hibernatetest.dto;

import java.io.Serializable;

import org.azafiu.hibernatetest.entities.ProductDetailsEntity;

/**
 * DTO for a Product Details entity.
 *
 * @author andrei.zafiu
 *
 */
public class ProductDetailsDTO implements Serializable {

	private static final long serialVersionUID = 6702415199818514435L;

	/** changes done to a {@link ProductDetailsEntity} */
	private ProductDetailsEntity changes;

	/** initial values of the {@link ProductDetailsEntity} */
	private ProductDetailsEntity initial;
	/**
	 * the operation done to get from initial to changes. Possible values are
	 * 'ADD', 'MOD' and 'REM'
	 */
	private String operation;

	/**
	 * @return the changes
	 */
	public ProductDetailsEntity getChanges() {
		return this.changes;
	}

	/**
	 * @return the initial
	 */
	public ProductDetailsEntity getInitial() {
		return this.initial;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return this.operation;
	}

	/**
	 * @param changes
	 *            the changes to set
	 */
	public void setChanges(final ProductDetailsEntity changes) {
		this.changes = changes;
	}

	/**
	 * @param initial
	 *            the initial to set
	 */
	public void setInitial(final ProductDetailsEntity initial) {
		this.initial = initial;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final String operation) {
		this.operation = operation;
	}

}
