package org.restcomm.connect.rvd.http;

import java.util.List;

/**
 * A dto class that caries response data and includes REST pagination info
 *
 * @author otsakir@gmail.com - Orestis Tsakiridis
 */
public class PaginatedResults<T> {
    Integer currentPage;
    Integer pageSize;
    String next;
    String previous;
    Integer total;
    List<T> results;

    public PaginatedResults() {
    }

    public PaginatedResults(Integer currentPage, Integer pageSize, String next, String previous, Integer total, List<T> results) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.next = next;
        this.previous = previous;
        this.total = total;
        this.results = results;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
