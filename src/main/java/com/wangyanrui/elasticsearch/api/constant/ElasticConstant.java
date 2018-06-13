package com.wangyanrui.elasticsearch.api.constant;

/**
 * Title: ElasticConstant
 * Description: ElasticSearch Constant
 *
 * @author wangyanrui
 * @version 1.0
 */
public interface ElasticConstant {

    interface Default {
        String TYPE_NAME = "_doc";
    }

    interface Count {
        // 查询的最小结果集数量, 即不查询结果
        Integer QUERY_ZERO_COUNT = 0;

        // 查询的最大结果集数量
        Integer QUERY_MAX_COUNT = Integer.MAX_VALUE;

        // 聚合的最大结果集数量, 即查询全部聚合结果
        Integer AGG_MAX_COUNT = 0;

    }

}
