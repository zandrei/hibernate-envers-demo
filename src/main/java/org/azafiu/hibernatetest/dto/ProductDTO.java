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
package org.azafiu.hibernatetest.dto;

import java.io.Serializable;
import java.util.List;

import org.azafiu.hibernatetest.entities.ProductEntity;

/**
 *
 * @author andrei.zafiu
 *
 */
public class ProductDTO implements Serializable {

	private static final long serialVersionUID = 4216186630025434294L;

	/** changes done to a {@link ProductEntity} */
	private ProductEntity changes;

	/** initial values of the {@link ProductEntity} */
	private ProductEntity initial;

	/**
	 * the operation done to get from initial to changes. Possible values are
	 * 'ADD', 'MOD' and 'REM'
	 */
	private String operation;

	/**
	 * A list of {@link ProductDetailsDTO} objects assigned to this product
	 */
	private List<ProductDetailsDTO> productDetails;

	private Long revision;

	/**
	 * @return the changes
	 */
	public ProductEntity getChanges() {
		return this.changes;
	}

	/**
	 * @return the initial
	 */
	public ProductEntity getInitial() {
		return this.initial;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return this.operation;
	}

	/**
	 * @return the productDetails
	 */
	public List<ProductDetailsDTO> getProductDetails() {
		return this.productDetails;
	}

	/**
	 * @return the revision
	 */
	public Long getRevision() {
		return this.revision;
	}

	/**
	 * @param changes
	 *            the changes to set
	 */
	public void setChanges(final ProductEntity changes) {
		this.changes = changes;
	}

	/**
	 * @param initial
	 *            the initial to set
	 */
	public void setInitial(final ProductEntity initial) {
		this.initial = initial;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final String operation) {
		this.operation = operation;
	}

	/**
	 * @param productDetails
	 *            the productDetails to set
	 */
	public void setProductDetails(final List<ProductDetailsDTO> productDetails) {
		this.productDetails = productDetails;
	}

	/**
	 * @param revision
	 *            the revision to set
	 */
	public void setRevision(final Long revision) {
		this.revision = revision;
	}

}
