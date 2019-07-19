package com.elasticsearch.api.service.es;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.elasticsearch.api.enums.ESIndexTermType;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.*;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CleverApe
 * @Classname ESIndexOperater
 * @Description TODO
 * @Date 2019-07-19 10:39
 * @Version V1.0
 */
@Component
@PropertySource("classpath:index.properties")
public class ESIndexOperater {
    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private JestClient client;

    @Value("${index.name}")
    private String indexname = "";
    @Value("${index.type}")
    private String type = "";
    @Value("${index.shard}")
    private int shard;
    @Value("${index.replicas}")
    private int replicas;
    @Value("${index.interval}")
    private String interval;

    public String getType() {
        return this.type;
    }

    /**
     * 创建文档
     */
    public boolean addDocument(Map<String, Object> obj) throws IOException {
        Bulk.Builder bulk = new Bulk.Builder();
        bulk.addAction(new Index.Builder(obj).index(indexname).type(type).build());
        BulkResult result = client.execute(bulk.build());
        if (!result.isSucceeded()) {
            for (BulkResult.BulkResultItem item : result.getItems()) {
                System.out.println(item.error);
            }
        }
        return result.isSucceeded();
    }

    /**
     * 查询索引
     */
    public SearchResult getDocumentResult(ESIndexContext indexContext) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        if (indexContext.getPage() != 0 && indexContext.getPageSize() != 0) {
            searchSourceBuilder.from((indexContext.getPage() - 1) * indexContext.getPageSize() + 1);
            searchSourceBuilder.size(indexContext.getPageSize());
        }


        BoolQueryBuilder queryBuilder = null;
        if (indexContext.getTerms() != null && indexContext.getTerms().size() > 0) {
            queryBuilder = QueryBuilders.boolQuery();
//            queryBuilder = queryBuilder.minimumShouldMatch(1);
            for (ESIndexTerm item : indexContext.getTerms()) {
                switch (item.getType()) {
                    case TERM_TYPE:
                        queryBuilder = queryBuilder.must(QueryBuilders.termQuery(item.getField(), item.getValue()));
                        break;
                    case RANGE_TYPE:
                        queryBuilder = queryBuilder.must(QueryBuilders.rangeQuery(item.getField()).gte(item.getRangeBegin()).lt(item.getRangeEnd()));
                        break;
                    case SHOULD_TYPE:
                        queryBuilder = queryBuilder.should(QueryBuilders.termQuery(item.getField(), item.getValue()));
                        break;
                }
            }
        }
        if (queryBuilder != null) {
            searchSourceBuilder.query(queryBuilder);
        }

        if (indexContext.getOrders() != null) {
            for (ESIndexOrder order : indexContext.getOrders()) {
                searchSourceBuilder.sort(order.getFieldName(), order.getOrder());
            }
        }

        AggregationBuilder aggregationBuilder = null;
        AggregationBuilder minAggregationBuilder = null;
        if (indexContext.getAggregationGroups() != null && indexContext.getAggregationGroups().size() > 0) {
            for (ESIndexTerm item : indexContext.getAggregationGroups()) {
                switch (item.getType()) {
                    case AGGREGATION_GROUP_TYPE:
                        if (aggregationBuilder == null) {
                            if (item.getOrders() != null && item.getOrders().size() > 0) {
                                aggregationBuilder = AggregationBuilders.terms(item.getField()).field(item.getFieldName()).order(item.getOrders());
                            } else {
                                aggregationBuilder = AggregationBuilders.terms(item.getField()).field(item.getFieldName());
                            }
                            minAggregationBuilder = aggregationBuilder;
                        } else {
                            if (item.getOrders() != null && item.getOrders().size() > 0) {
                                minAggregationBuilder = minAggregationBuilder.subAggregation(AggregationBuilders.terms(item.getField()).field(item.getFieldName()).order(item.getOrders()).size(Integer.MAX_VALUE));
                            } else {
                                minAggregationBuilder = minAggregationBuilder.subAggregation(AggregationBuilders.terms(item.getField()).field(item.getFieldName()).size(Integer.MAX_VALUE));
                            }
                            minAggregationBuilder = minAggregationBuilder.getSubAggregations().get(0);
                        }
                        break;
                }
            }
        }

        if (aggregationBuilder != null && indexContext.getAggregationColumns() != null && indexContext.getAggregationColumns().size() > 0) {
            searchSourceBuilder.size(0);
            for (ESIndexTerm item : indexContext.getAggregationColumns()) {
                switch (item.getType()) {
                    case AGGREGATION_COUNT_TYPE:
                        minAggregationBuilder = minAggregationBuilder.subAggregation(AggregationBuilders.count(item.getField()).field(item.getFieldName()));
                        break;
                    case AGGREGATION_DISTINCT_TYPE:
                        minAggregationBuilder = minAggregationBuilder.subAggregation(AggregationBuilders.cardinality(item.getField()).field(item.getFieldName()));
                        break;
                    case AGGREGATION_SUM_TYPE:
                        minAggregationBuilder = minAggregationBuilder.subAggregation(AggregationBuilders.sum(item.getField()).field(item.getFieldName()));
                        break;
                    case AGGREGATION_AVG_TYPE:
                        minAggregationBuilder = minAggregationBuilder.subAggregation(AggregationBuilders.avg(item.getField()).field(item.getFieldName()));
                        break;
                }
            }
        } else {
            aggregationBuilder = null;
        }

        if (aggregationBuilder != null) {
            searchSourceBuilder.aggregation(aggregationBuilder);
        }


        System.out.println(searchSourceBuilder.toString());
        System.out.println("indexname = " + indexname + ", type = " + type);
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexname).addType(type).build();
        SearchResult result = null;
        try {
            result = client.execute(search);
        } catch (IOException e) {
            logger.error("ESIndexOperater.getDocumentResult() Has Error:", e);
            return null;
        }
        return result;
    }

    /**
     * 创建索引
     */

    public boolean createIndex() throws IOException {
        final Map<String, Object> indexerSettings = new HashMap<>();
        indexerSettings.put("number_of_shards", shard);
        indexerSettings.put("number_of_replicas", replicas);
        indexerSettings.put("index.refresh_interval", interval);
        CreateIndex.Builder builder = new CreateIndex.Builder(indexname).settings(indexerSettings);
        JestResult result = client.execute(builder.build());
        if (!result.isSucceeded()) {
            System.out.println(result.getErrorMessage());
        }
        return result.isSucceeded();
    }

    /**
     * 创建类型
     */
    public boolean createIndexType() throws IOException {
        XContentBuilder mapping = creatMapping();
        PutMapping putMapping = new PutMapping.Builder(indexname, type, Strings.toString(mapping)).build();
        JestResult result = client.execute(putMapping);
        return result.isSucceeded();

    }

    private XContentBuilder creatMapping() throws IOException {
        XContentBuilder mapping = jsonBuilder() // 用户日志表 for ES
                .startObject()
                .field("dynamic", "true")
                .startObject("_all")
                .field("enabled", "false")
                .endObject()
                .startObject("properties")
                // appid appid
                .startObject("appid")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                // uid uid
                .startObject("uid")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                // reg_time 注册时间
                .startObject("reg_time")
                .field("type", "date")
                .field("format", "yyyy-MM-dd HH:mm:ss")
                .field("index", true)
                .field("store", false)
                .endObject()
                // act_time 激活时间
                .startObject("act_time")
                .field("type", "date")
                .field("format", "yyyy-MM-dd HH:mm:ss")
                .field("index", true)
                .field("store", false)
                .endObject()
                // channel
                .startObject("channel")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // pchannel
                .startObject("pchannel")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // pchannel_name
                .startObject("pchannel_name")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // pchannel_type
                .startObject("pchannel_type")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // prepage_id
                .startObject("prepage_id")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                // prepage_name
                .startObject("prepage_name")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // page_id
                .startObject("page_id")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                // page_name
                .startObject("page_name")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // action_id
                .startObject("action_id")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                // action_name
                .startObject("action_name")
                .field("type", "keyword")
                .field("index", true)
                .field("store", false)
                .endObject()
                // createtime
                .startObject("createtime")
                .field("type", "date")
                .field("format", "yyyy-MM-dd HH:mm:ss")
                .field("index", true)
                .field("store", false)
                .endObject()
                // stat_time
                .startObject("stat_time")
                .field("type", "date")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }
}

