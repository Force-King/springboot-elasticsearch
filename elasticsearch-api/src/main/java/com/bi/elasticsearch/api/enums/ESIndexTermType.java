package com.bi.elasticsearch.api.enums;

/**
 * @author CleverApe
 * @Classname ESIndexTermType
 * @Description ES索引类型
 * @Date 2019-07-19 10:36
 * @Version V1.0
 */
public enum ESIndexTermType {

    MATCH_TYPE,
    TERM_TYPE,
    RANGE_TYPE,
    SHOULD_TYPE,
    AGGREGATION_GROUP_TYPE,
    AGGREGATION_COUNT_TYPE,
    AGGREGATION_SUM_TYPE,
    AGGREGATION_AVG_TYPE,
    AGGREGATION_DISTINCT_TYPE
}
