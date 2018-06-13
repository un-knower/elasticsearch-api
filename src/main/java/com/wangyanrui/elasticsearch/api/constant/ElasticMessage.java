package com.wangyanrui.elasticsearch.api.constant;

/**
 * Title: ElasticMessage
 * Description: ElasticSearch Message
 *
 * @author wangyanrui
 * @version 1.0
 */
public interface ElasticMessage {

    /**
     * 操作失败
     */
    interface OperaFailureMsg {
        String INDEX_FAILURE = "index data failure";

        String DELETE_FAILURE = "delete data failure";

        String UPDATE_FAILURE = "update data failure";

        String GET_FAILURE = "doc not exist";

        String MULTI_GET_FAILURE = "part of doc not exist";
    }

    /**
     * 操作错误
     */
    interface OperaErrorMsg {
        String BULK_ERROR = "error request type";
    }

    /**
     * Admin 操作失败
     */
    interface AdminFailureMsg {
        String CREATE_INDEX_FAILURE = "create index failure";
        String DELETE_INDEX_FAILURE = "delete index failure";

        String PUT_MAPPING_FAILURE = "put mapping failure";

        String UPDATE_SETTING_FAILURE = "update setting failure";
    }

}
