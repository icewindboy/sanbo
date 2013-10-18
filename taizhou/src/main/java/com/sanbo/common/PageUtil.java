/*
 * Copyright @2007-2008 by Infowarelab Webapps.
 */
package com.sanbo.common;

import java.util.Collection;
import java.util.List;



/**
 * @author Power
 * 
 * modify: 1.当前页码大于总页数的时候，当前页吗等于最大页数 2008/10/27 by Denny
 * 
 */
public final class PageUtil {
	private PageUtil() {
	}

	/**
	 * Use the origin page to create a new page
	 * 
	 * @param page
	 *            page object
	 * @param totalRecords
	 *            total records
	 * @return new page object
	 */
	public static Page createPage(Page page, int totalRecords) {
		return createPage(page.getEveryPage(), page.getCurrentPage(), totalRecords);
	}

	/**
	 * the basic page utils not including exception handler
	 * 
	 * @param pageSize
	 *            the size of every page
	 * @param currentPage
	 *            the number of current page
	 * @param totalRecords
	 *            the total Records of the query
	 * @return page page object
	 */
	public static Page createPage(int pageSize, int currentPage, int totalRecords) {
		int everyPageNew = getEveryPage(pageSize);
		int totalPage = getTotalPage(everyPageNew, totalRecords);
		int currentPageNew = getCurrentPage(currentPage, totalPage);
		int beginIndex = getBeginIndex(everyPageNew, currentPageNew);
		boolean hasNextPage = hasNextPage(currentPageNew, totalPage);
		boolean hasPrePage = hasPrePage(currentPageNew);

		return new Page(hasPrePage, hasNextPage, everyPageNew, totalPage, currentPageNew, beginIndex, totalRecords);
	}

	private static int getEveryPage(int everyPage) {
		return everyPage == 0 ? 10 : everyPage;
	}

	/**
	 * 
	 * @author denny.lv
	 * @createtime Oct 27, 2008
	 * @param currentPage
	 * @param totalPage
	 * @return
	 */
	private static int getCurrentPage(int currentPage, int totalPage) {
		int cp = 1;
		if (currentPage > 0 && currentPage <= totalPage) {
			cp = currentPage;
		} else if (currentPage > totalPage) {
			cp = totalPage>0? totalPage:1;
		}
		return cp;
	}

	private static int getBeginIndex(int everyPage, int currentPage) {
		return (currentPage - 1) * everyPage;
	}

	private static int getTotalPage(int everyPage, int totalRecords) {
		int totalPage = 0;

		if (totalRecords % everyPage == 0) {
			totalPage = totalRecords / everyPage;
		} else {
			totalPage = totalRecords / everyPage + 1;
		}

		return totalPage;
	}

	private static boolean hasPrePage(int currentPage) {
		return currentPage == 1 ? false : true;
	}

	private static boolean hasNextPage(int currentPage, int totalPage) {
		return currentPage == totalPage || totalPage == 0 ? false : true;
	}

	/**
	 * 计算实际的startIndex
	 * 
	 * @author denny.lv
	 * @createtime Oct 27, 2008
	 * @param currentPage
	 * @param pageSize
	 * @param totalCount
	 * @return
	 */
	public static int getStartIndex(int currentPage, int pageSize, int totalCount) {
		int everyPageNew = getEveryPage(pageSize);
		int totalPage = getTotalPage(everyPageNew, totalCount);
		int currentPageNew = getCurrentPage(currentPage, totalPage);
		return getBeginIndex(everyPageNew, currentPageNew);
	}
	
	@SuppressWarnings("unchecked")
	public static Result createResult(int pageSize, int currentPage, int totalRecords, Collection records) {

		Page page = createPage(pageSize, currentPage, totalRecords);
		Result result = new Result();
		result.setContent((List) records);
		result.setPage(page);
		return result;
	}


}
