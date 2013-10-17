/*
 * Copyright @2007-2008 by Infowarelab Webapps.
 */
package com.sanbo.common;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Power
 */
public class Result<T extends Object> implements Serializable {

	private static final long serialVersionUID = -7801500180007393760L;

	private Page page;

	private Collection<T> content;

	/**
	 * The default constructor
	 */
	public Result() {
		super();
	}

	/**
	 * The constructor using fields
	 * 
	 * @param page
	 *            page object
	 * @param content
	 *            result list
	 */
	public Result(Page page, Collection<T> content) {
		this.page = page;
		this.content = content;
	}

	/**
	 * @return Returns the content.
	 */
	public Collection<T> getContent() {
		return content;
	}

	/**
	 * @return Returns the page.
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * @param content
	 *            The content to set.
	 */
	public void setContent(Collection<T> content) {
		this.content = content;
	}

	/**
	 * @param page
	 *            The page to set.
	 */
	public void setPage(Page page) {
		this.page = page;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	public String toString() {
		final String tab = "\r\n\t";
		String retValue = "";
		retValue = "Result ( " + super.toString() + tab + "page = " + this.page
				+ tab + "content = " + this.content + tab + " )";

		return retValue;
	}
}