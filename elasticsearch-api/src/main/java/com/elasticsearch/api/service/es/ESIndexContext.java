package com.elasticsearch.api.service.es;

import com.elasticsearch.api.enums.ESIndexTermType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CleverApe
 * @Classname ESIndexContext
 * @Description TODO
 * @Date 2019-07-19 11:06
 * @Version V1.0
 */
public class ESIndexContext {

    private int page;
    private int pageSize;
    private List<ESIndexTerm> terms;
    private List<ESIndexTerm> aggregationGroups;
    private List<ESIndexTerm> aggregationColumns;
    private List<ESIndexTerm> views;
    private List<ESIndexOrder> orders;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<ESIndexTerm> getTerms() {
        return terms;
    }

    public void setTerms(List<ESIndexTerm> terms) {
        this.terms = terms;
    }

    public List<ESIndexTerm> getAggregationGroups() {
        return aggregationGroups;
    }

    public void setAggregationGroups(List<ESIndexTerm> aggregationGroups) {
        this.aggregationGroups = aggregationGroups;
    }

    public List<ESIndexTerm> getAggregationColumns() {
        return aggregationColumns;
    }

    public void setAggregationColumns(List<ESIndexTerm> aggregationColumns) {
        this.aggregationColumns = aggregationColumns;
    }

    public List<ESIndexTerm> getViews() {
        return views;
    }

    public void setViews(List<ESIndexTerm> views) {
        this.views = views;
    }

    public List<ESIndexOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ESIndexOrder> orders) {
        this.orders = orders;
    }

    public boolean addContext(ESIndexTerm indexTerm) {
        if (indexTerm.getType() == null) {
            return false;
        }
        switch (indexTerm.getType()) {
            case ESIndexTermType.TERM_TYPE:
            case ESIndexTermType.RANGE_TYPE:
            case ESIndexTermType.SHOULD_TYPE:
                if(terms == null){
                    terms = new ArrayList<>();
                }
                terms.add(indexTerm);
                break;
            case ESIndexTermType.AGGREGATION_GROUP_TYPE:
                if(aggregationGroups == null){
                    aggregationGroups = new ArrayList<>();
                }
                aggregationGroups.add(indexTerm);
                break;
            case ESIndexTermType.AGGREGATION_COUNT_TYPE:
            case ESIndexTermType.AGGREGATION_DISTINCT_TYPE:
            case ESIndexTermType.AGGREGATION_SUM_TYPE:
            case ESIndexTermType.AGGREGATION_AVG_TYPE:
                if(aggregationColumns == null){
                    aggregationColumns = new ArrayList<>();
                }
                aggregationColumns.add(indexTerm);
                break;
            default:
                if(views == null){
                    views = new ArrayList<>();
                }
                views.add(indexTerm);
                break;
        }
        return true;
    }
}
