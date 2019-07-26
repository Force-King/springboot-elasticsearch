package com.bi.elasticsearch.api.controller;

import com.bi.elasticsearch.api.util.RestApiResult;
import com.bi.elasticsearch.api.service.es.ESIndexOperater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CleverApe
 * @Classname EsController
 * @Description ES索引、文档操作接口
 * @Date 2019-07-22 19:11
 * @Version V1.0
 */
@RestController
@RequestMapping(value = "/elasticsearch", produces = "application/json;charset=utf-8")
public class EsController {

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private ESIndexOperater esIndexOperater;

    @Value("${index.name.real.name.uv}")
    private String realNameUV_IndexName;
    @Value("${index.name.user.recommend}")
    private String recommend_IndexName;

    @Value("${index.type.real.name.uv}")
    private String realNameUV_Type;
    @Value("${index.type.user.recommend}")
    private String recommend_Type;

    @RequestMapping("/createIndex_default")
    public String createDefaultIndex() {
        try {
            esIndexOperater.createIndex(realNameUV_IndexName);
            esIndexOperater.createIndex(recommend_IndexName);
        } catch (Exception e) {
            logger.error("createDefaultIndex  Error : ", e);
        }
        return RestApiResult.success().toString();
    }

    @RequestMapping("/createType_default")
    public String createDefaultType() {
        try {
            esIndexOperater.createIndexType(realNameUV_IndexName, realNameUV_Type);
            esIndexOperater.createIndexType(recommend_IndexName, recommend_Type);
        } catch (Exception e) {
            logger.error("createDefaultType Error : ", e);
        }
        return RestApiResult.success().toString();
    }

    @RequestMapping(value = "/createIndex", method = RequestMethod.POST)
    public String createIndex(@RequestParam(value = "index", required = true) String index) {
        try {
            esIndexOperater.createIndex(index);
        } catch (Exception e) {
            logger.error("createIndex Error : ", e);
        }
        return RestApiResult.success().toString();
    }

    @RequestMapping(value = "/createType", method = RequestMethod.POST)
    public String createType(@RequestParam(value = "index", required = true) String index,
                             @RequestParam(value = "type", required = true) String type) {
        try {
            esIndexOperater.createIndexType(index, type);
        } catch (Exception e) {
            logger.error("createType Error : ", e);
        }
        return RestApiResult.success().toString();
    }
}
