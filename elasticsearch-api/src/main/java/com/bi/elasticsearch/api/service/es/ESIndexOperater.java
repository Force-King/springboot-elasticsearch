package com.bi.elasticsearch.api.service.es;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import com.bi.elasticsearch.api.enums.ESIndexTermType;
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
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ESIndexOperater {
    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private JestClient client;

    @Value("${index.name}")
    private String indexName;
    @Value("${index.type}")
    private String type;

    @Value("${index.shard}")
    private int shard;
    @Value("${index.replicas}")
    private int replicas;
    @Value("${index.interval}")
    private String interval;



    private static String COMMA_ANALYZER="{\n" +
            "     \t\"analysis\": {\n" +
            "                \"analyzer\": {\n" +
            "            \t\"comma\": {\n" +
            "                         \"type\": \"pattern\",\n" +
            "                         \"pattern\":\",\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "     }";

    /**
     * 创建索引
     */

    public boolean createIndex(String indexName) throws IOException {
        final Map<String, Object> indexerSettings = new HashMap<>();
        indexerSettings.put("number_of_shards", shard);
        indexerSettings.put("number_of_replicas", replicas);
        indexerSettings.put("index.refresh_interval", interval);
//        indexerSettings.put("index.analysis", COMMA_ANALYZER);
        CreateIndex.Builder builder = new CreateIndex.Builder(indexName).settings(indexerSettings);
        JestResult result = client.execute(builder.build());
        if (!result.isSucceeded()) {
            logger.error(result.getErrorMessage());
        }
        return result.isSucceeded();
    }

    /**
     * 创建类型
     */
    public boolean createIndexType(String indexName, String type) throws IOException {
        XContentBuilder mapping = mapping = creatMapping();
        PutMapping putMapping = new PutMapping.Builder(indexName, type, Strings.toString(mapping)).build();
        JestResult result = client.execute(putMapping);
        if (!result.isSucceeded()) {
            logger.error(result.getErrorMessage());
        }
        return result.isSucceeded();

    }

    private XContentBuilder creatMapping() throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .field("dynamic", "true")
                .startObject("_all")
                .field("enabled", "false")
                .endObject()
                .startObject("properties")
                //uid
                .startObject("uid")
                .field("type", "integer")
                .field("index", true)
                .field("store", false)
                .endObject()
                //pIds
                .startObject("pIds")
                .field("type", "text")
                .field("analyzer", "comma")
                .field("search_analyzer", "comma")
                .field("index", true)
                .field("store", false)
                .endObject()
                // createTime
                .startObject("createTime")
                .field("type", "date")
                .field("format", "yyyy-MM-dd HH:mm:ss")
                .field("index", true)
                .field("store", false)
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }

    /**
     * 创建文档
     */
    public boolean addDocument(String indexName, String type, Map<String, Object> obj) throws IOException {
        Bulk.Builder bulk = new Bulk.Builder();
        bulk.addAction(new Index.Builder(obj).index(indexName).type(type).build());
        BulkResult result = client.execute(bulk.build());
        if (!result.isSucceeded()) {
            for (BulkResult.BulkResultItem item : result.getItems()) {
                logger.error(item.error);
            }
        }
        return result.isSucceeded();
    }

    /**
     * 查询索引
     */
    public SearchResult getDocumentResult(String indexName, String type, ESIndexContext indexContext) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        if (indexContext.getPage() != 0 && indexContext.getPageSize() != 0) {
            searchSourceBuilder.from((indexContext.getPage() - 1) * indexContext.getPageSize() + 1);
            searchSourceBuilder.size(indexContext.getPageSize());
        }

        // 精确查询
        BoolQueryBuilder boolQueryBuilder = null;
        if (indexContext.getTerms() != null && indexContext.getTerms().size() > 0) {
            boolQueryBuilder = QueryBuilders.boolQuery();
            for (ESIndexTerm item : indexContext.getTerms()) {
                switch (item.getType()) {
                    case TERM_TYPE:
                        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termQuery(item.getField(), item.getValue()));
                        break;
                    case RANGE_TYPE:
                        boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.rangeQuery(item.getField()).gte(item.getRangeBegin()).lt(item.getRangeEnd()));
                        break;
                    case SHOULD_TYPE:
                        boolQueryBuilder = boolQueryBuilder.should(QueryBuilders.termQuery(item.getField(), item.getValue()));
                        break;
                }
            }
        }
        if (boolQueryBuilder != null) {
            searchSourceBuilder.query(boolQueryBuilder);
        }
        if (indexContext.getOrders() != null) {
            for (ESIndexOrder order : indexContext.getOrders()) {
                searchSourceBuilder.sort(order.getFieldName(), order.getOrder());
            }
        }

        //分词查询
        MatchQueryBuilder matchQueryBuilder = null;
        if (indexContext.getViews() != null && indexContext.getViews().size() > 0) {
            for (ESIndexTerm item : indexContext.getViews()) {
                switch (item.getType()) {
                    case MATCH_TYPE:
                        matchQueryBuilder = QueryBuilders.matchQuery(item.getField(), item.getValue());
                        break;
                }
            }
        }
        if (matchQueryBuilder != null) {
            searchSourceBuilder.query(matchQueryBuilder);
        }

        //聚合查询
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

        if (indexContext.getAggregationColumns() != null && indexContext.getAggregationColumns().size() > 0) {
            if(aggregationBuilder == null) {
                for (ESIndexTerm item : indexContext.getAggregationColumns()) {
                    switch (item.getType()) {
                        case AGGREGATION_COUNT_TYPE:
                            aggregationBuilder = AggregationBuilders.count(item.getField()).field(item.getFieldName());
                            break;
                        case AGGREGATION_DISTINCT_TYPE:
                            aggregationBuilder = AggregationBuilders.cardinality(item.getField()).field(item.getFieldName());
                            break;
                    }
                }
            } else {
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
            }

        }

        if (aggregationBuilder != null) {
            if(minAggregationBuilder != null) {
                searchSourceBuilder.aggregation(minAggregationBuilder);
            } else {
                searchSourceBuilder.aggregation(aggregationBuilder);
            }

        }

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName).addType(type).build();
        SearchResult result;
        try {
            result = client.execute(search);
        } catch (IOException e) {
            logger.error("ESIndexOperater.getDocumentResult() Has Error:", e);
            return null;
        }
        return result;
    }

    /**
     * 查询实名登陆UV
     */
    public SearchResult getDocumentRealNameUv(String startTime, String endTime) {
        String queryStr = "{\n" +
                " \"query\": {\n" +
                "        \"bool\":{\n" +
                "            \"must\":[{\n" +
                "                \"range\":{\n" +
                "                    \"createTime\": {\n" +
                "                          \"gte\": \""+startTime+"\",\n" +
                "                          \"lt\": \""+endTime+"\"\n" +
                "                      }\n" +
                "                }\n" +
                "            }]\n" +
                "        }       \n" +
                "    },\n" +
                "    \"size\": 0,\n" +
                "    \"aggs\": {\n" +
                "        \"login_uv\": {\n" +
                "            \"cardinality\": {\n" +
                "                \"field\": \"uid\"\n" +
                "              }\n" +
                "        }\n" +
                "     }\n" +
                "}";

        Search search = new Search.Builder(queryStr).addIndex(indexName).addType(type).build();
        SearchResult result;
        try {
            result = client.execute(search);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        return result;
    }

    /**
     * 查询可推荐用户数
     */
    public SearchResult getDocumentRecommend(Integer pid, String startTime, String endTime) {
        String queryStr = "{\n" +
                " \"query\": {\n" +
                "        \"bool\":{\n" +
                "            \"must\":[{\n" +
                "                \"range\":{\n" +
                "                    \"createTime\": {\n" +
                "                          \"gte\": \""+startTime+"\",\n" +
                "                          \"lt\": \""+endTime+"\"\n" +
                "                      }\n" +
                "                }\n" +
                "            },{\n" +
                "                \"term\":{\n" +
                "                    \"pIds\":\""+pid+"\"\n" +
                "                }\n" +
                "            }]\n" +
                "        }       \n" +
                "    },\n" +
                "    \"size\": 0,\n" +
                "    \"aggs\": {\n" +
                "        \"user_count\": {\n" +
                "            \"cardinality\": {\n" +
                "                \"field\": \"uid\"\n" +
                "              }\n" +
                "        }\n" +
                "      }\n" +
                "}";

        Search search = new Search.Builder(queryStr).addIndex(indexName).addType(type).build();
        SearchResult result;
        try {
            result = client.execute(search);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        return result;
    }


}

