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
