package com.lilium.elasticsearch.search;

import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

public class SearchRequestDTO extends PagedRequestDTO {
    private List<String> fields; // field(s) in Document to search on
    private String searchTerm; // searchTerm to be applied on (could be multiple) fields
    private String sortBy;
    private SortOrder order;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }
}