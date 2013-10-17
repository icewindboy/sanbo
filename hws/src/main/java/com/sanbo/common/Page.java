/*
 * Copyright @2007-2008 by Infowarelab Webapps.
 */
package com.sanbo.common;

import java.io.Serializable;

/**
 * @author Power
 * 
 */
public class Page implements Serializable  {

	private static final long serialVersionUID = 7295055109493685876L;

	/** imply if the page has previous page */
	private boolean hasPrePage;

	/** imply if the page has next page */
	private boolean hasNextPage;

	/** the number of every page */
	private int everyPage;

	/** the total page number */
	private int totalPage;

	/** the number of current page */
	private int currentPage;

	/** the begin index of the records by the current query */
	private int beginIndex;

	/** the total Records of the query */
	private int totalRecords;

	/** The default constructor */
	public Page() {

	}

	/**
	 * construct the page by everyPage
	 * 
	 * @param everyPage
	 *            the number of every page
	 */
	public Page(int everyPage) {
		this.everyPage = everyPage;
	}

	/**
	 * The whole constructor
	 * 
	 * @param hasPrePage
	 *            imply if the page has previous page
	 * @param hasNextPage
	 *            imply if the page has next page
	 * @param everyPage
	 *            the number of every page
	 * @param totalPage
	 *            the total page number
	 * @param currentPage
	 *            the number of current page
	 * @param beginIndex
	 *            the begin index of the records by the current query
	 * @param totalRecords
	 *            the total Records of the query
	 */
	public Page(boolean hasPrePage, boolean hasNextPage, int everyPage,
			int totalPage, int currentPage, int beginIndex, int totalRecords) {
		this.hasPrePage = hasPrePage;
		this.hasNextPage = hasNextPage;
		this.everyPage = everyPage;
		this.totalPage = totalPage;
		this.currentPage = currentPage;
		this.beginIndex = beginIndex;
		this.totalRecords = totalRecords;
	}

	/**
	 * @return Returns the beginIndex.
	 */
	public int getBeginIndex() {
		return beginIndex;
	}

	/**
	 * @param beginIndex
	 *            The beginIndex to set.
	 */
	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	/**
	 * @return Returns the currentPage.
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage
	 *            The currentPage to set.
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return Returns the everyPage.
	 */
	public int getEveryPage() {
		return everyPage;
	}

	/**
	 * @param everyPage
	 *            The everyPage to set.
	 */
	public void setEveryPage(int everyPage) {
		this.everyPage = everyPage;
	}

	/**
	 * @return Returns the hasNextPage.
	 */
	public boolean getHasNextPage() {
		return hasNextPage;
	}

	/**
	 * @param hasNextPage
	 *            The hasNextPage to set.
	 */
	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	/**
	 * @return Returns the hasPrePage.
	 */
	public boolean getHasPrePage() {
		return hasPrePage;
	}

	/**
	 * @param hasPrePage
	 *            The hasPrePage to set.
	 */
	public void setHasPrePage(boolean hasPrePage) {
		this.hasPrePage = hasPrePage;
	}

	/**
	 * @return Returns the totalPage.
	 * 
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * @param totalPage
	 *            The totalPage to set.
	 */
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString() {
	    final String tab = "\r\n\t";
	    
	    String retValue = "";
	    
	    retValue = "Page ( "
	        + super.toString() + tab
	        + "hasPrePage = " + this.hasPrePage + tab
	        + "hasNextPage = " + this.hasNextPage + tab
	        + "everyPage = " + this.everyPage + tab
	        + "totalPage = " + this.totalPage + tab
	        + "currentPage = " + this.currentPage + tab
	        + "beginIndex = " + this.beginIndex + tab
	        + "totalRecords = " + this.totalRecords + tab
	        + " )";
	
	    return retValue;
	}
	
}
