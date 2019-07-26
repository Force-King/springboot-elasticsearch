package com.bi.elasticsearch.api.service.es;

import com.bi.elasticsearch.api.enums.ESIndexTermType;
import org.elasticsearch.search.aggregations.BucketOrder;

import java.util.List;

/**
 * @author CleverApe
 * @Classname ESIndexTerm
 * @Description TODO
 * @Date 2019-07-19 11:06
 * @Version V1.0
 */
public class ESIndexTerm {

    private ESIndexTermType type;
    private String field;
    private String fieldName;
    private String fieldType;
    private Object value;
    private String rangeBegin;
    private String rangeEnd;
    private List<BucketOrder> orders;

    public ESIndexTermType getType() {
        return type;
    }

    public void setType(ESIndexTermType type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getRangeBegin() {
        return rangeBegin;
    }

    public void setRangeBegin(String rangeBegin) {
        this.rangeBegin = rangeBegin;
    }

    public String getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(String rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public List<BucketOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<BucketOrder> orders) {
        this.orders = orders;
    }
}
