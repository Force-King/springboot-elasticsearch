package com.elasticsearch.api.service.es;

import org.elasticsearch.search.sort.SortOrder;

/**
 * @author CleverApe
 * @Classname ESIndexOrder
 * @Description TODO
 * @Date 2019-07-19 11:07
 * @Version V1.0
 */
public class ESIndexOrder {
    private String fieldName;
    private SortOrder order;

    public ESIndexOrder(String fieldName, SortOrder order) {
        this.fieldName = fieldName;
        this.order = order;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

}
