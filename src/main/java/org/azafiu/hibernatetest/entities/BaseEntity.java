package org.azafiu.hibernatetest.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * Base entity for current database mapping. It will contain the common
 * properties of all entities (created, createdBy, modified, modifiedBy, active,
 * wasChecked) and 2 abstract function to get and set the id. It also contains
 * PrePersist and PreUpdate methods to set the created and modified dates.
 *
 * The @Audited annotation will mark the whole entity to be audited. Any change
 * to its properties will insert a new row in the ENTITYNAME_AUD audit table
 * with the corresponding operation. For more information check
 * http://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html/ch15.html
 *
 * The "withModifiedFlag" flag will create a column to track changes at
 * individual property level. For more information check
 * http://docs.jboss.org/hibernate
 * /orm/4.2/devguide/en-US/html/ch15.html#envers-tracking-properties-changes
 *
 * @author andrei.zafiu
 *
 */
@MappedSuperclass
@Audited(withModifiedFlag = true)
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ACTIVE")
	private Boolean active;

	@NotAudited
	@Column(name = "CREATED")
	private Timestamp created;

	@NotAudited
	@Column(name = "CREATED_BY")
	private Long createdBy;

	@Column(name = "MODIFIED")
	private Timestamp modified;

	@Column(name = "MODIFIED_BY")
	private Long modifiedBy;

	@Column(name = "WAS_CHECKED")
	private Boolean wasChecked;

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return this.active;
	}

	/**
	 * @return the created
	 */
	public Timestamp getCreated() {
		return this.created;
	}

	/**
	 * @return the createdBy
	 */
	public Long getCreatedBy() {
		return this.createdBy;
	}

	public abstract Long getId();

	/**
	 * @return the modified
	 */
	public Timestamp getModified() {
		return this.modified;
	}

	/**
	 * @return the modifiedBy
	 */
	public Long getModifiedBy() {
		return this.modifiedBy;
	}

	/**
	 * @return the wasChecked
	 */
	public Boolean getWasChecked() {
		return this.wasChecked;
	}

	/**
	 * Before persisting an entity set the created property to the current date.
	 * In this method we can also set the createdBy property to the logged user
	 * from the LoginFilter.
	 *
	 */
	@PrePersist
	public void prePersist() {
		final Date now = new Date();
		this.created = new Timestamp(now.getTime());
	}

	/**
	 * Before updating an entity set the modified property to the current date.
	 * In this method we can also set the modifiedBy property to the logged user
	 * from the LoginFilter.
	 *
	 */
	@PreUpdate
	public void preUpdate() {
		final Date now = new Date();
		this.modified = new Timestamp(now.getTime());
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(final Boolean active) {
		this.active = active;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(final Timestamp created) {
		this.created = created;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(final Long createdBy) {
		this.createdBy = createdBy;
	}

	public abstract void setId(Long id);

	/**
	 * @param modified
	 *            the modified to set
	 */
	public void setModified(final Timestamp modified) {
		this.modified = modified;
	}

	/**
	 * @param modifiedBy
	 *            the modifiedBy to set
	 */
	public void setModifiedBy(final Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @param wasChecked
	 *            the wasChecked to set
	 */
	public void setWasChecked(final Boolean wasChecked) {
		this.wasChecked = wasChecked;
	}

}
