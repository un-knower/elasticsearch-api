package com.wangyanrui.elasticsearch.api.core;

import com.wangyanrui.common.exception.OperaExceptionHandler;
import com.wangyanrui.elasticsearch.api.constant.ElasticConstant;
import com.wangyanrui.elasticsearch.api.constant.ElasticMessage;
import com.wangyanrui.elasticsearch.api.core.client.ElasticClient;
import com.wangyanrui.elasticsearch.api.core.factory.ElasticFactory;
import com.wangyanrui.elasticsearch.api.core.pojo.request.admin.AdminAction;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Title: ElasticAdmin
 * Description: ElasticSearch Admin Opera
 *
 * @author wangyanrui
 * @version 1.0
 */
public class ElasticAdmin {

    private ElasticClient client;

    private ElasticAdmin() {
    }

    public ElasticAdmin(ElasticClient client) {
        this.client = client;
    }

    public ElasticAdmin(String address, String clusterName) {
        this.client = ElasticFactory.getInstance(address, clusterName);
    }

    public ElasticAdmin(String address, int port, String clusterName) {
        this.client = ElasticFactory.getInstance(address, port, clusterName);
    }

    /**
     * 创建索引
     *
     * @param adminAction adminAction
     * @return 创建的原生ElasticSearch响应
     * @see AdminAction
     */
    public CreateIndexResponse createIndex(AdminAction adminAction) {
        safeDeleteIndex(adminAction.getIndex());

        CreateIndexRequestBuilder requestBuilder = client.getClient().admin()
                .indices()
                .prepareCreate(adminAction.getIndex());

        if (StringUtils.isNotEmpty(adminAction.getSetting())) {
            requestBuilder.setSettings(adminAction.getSetting(), XContentType.JSON);
        }

        if (StringUtils.isNotEmpty(adminAction.getMapping())) {
            requestBuilder.addMapping(ElasticConstant.Default.TYPE_NAME, adminAction.getMapping(), XContentType.JSON);
        }
        CreateIndexResponse response = requestBuilder.get();

        OperaExceptionHandler.flagCheck(!response.isAcknowledged(),
                ElasticMessage.AdminFailureMsg.CREATE_INDEX_FAILURE);

        return response;
    }

    /**
     * 删除索引(安全)
     * 先判断, 后删除索引
     *
     * @param index 索引名称
     */
    public void safeDeleteIndex(String index) {
        IndicesExistsResponse indicesExistsResponse = client.getClient().admin()
                .indices()
                .prepareExists(index)
                .get();

        if (indicesExistsResponse.isExists()) {
            deleteIndex(index);
        }
    }

    /**
     * 删除索引
     *
     * @param index 索引名称
     */
    public void deleteIndex(String index) {
        DeleteIndexResponse deleteResponse = client.getClient().admin()
                .indices()
                .prepareDelete(index)
                .get();

        OperaExceptionHandler.flagCheck(!deleteResponse.isAcknowledged(),
                ElasticMessage.AdminFailureMsg.DELETE_INDEX_FAILURE);
    }


    /**
     * 更新映射
     *
     * @param adminAction adminAction
     * @see AdminAction
     */
    public void putMapping(AdminAction adminAction) {
        PutMappingResponse response = client
                .getClient()
                .admin()
                .indices()
                .preparePutMapping(adminAction.getIndex())
                .setType(ElasticConstant.Default.TYPE_NAME)
                .setSource(adminAction.getMapping(), XContentType.JSON)
                .get();

        OperaExceptionHandler.flagCheck(!response.isAcknowledged(),
                ElasticMessage.AdminFailureMsg.PUT_MAPPING_FAILURE);
    }

    /**
     * 获取映射
     *
     * @param index 索引名称
     * @return 获取映射的原生ElasticSearch响应
     */
    public GetMappingsResponse getMapping(String index) {
        return client.getClient().admin()
                .indices()
                .prepareGetMappings(index)
                .setTypes(ElasticConstant.Default.TYPE_NAME)
                .get();
    }

    /**
     * 更新setting
     *
     * @param adminAction adminAction
     * @see AdminAction
     */
    public void updateSetting(AdminAction adminAction) {
        UpdateSettingsResponse response = client
                .getClient()
                .admin()
                .indices()
                .prepareUpdateSettings(adminAction.getIndex())
                .setSettings(adminAction.getSetting(), XContentType.JSON)
                .get();

        OperaExceptionHandler.flagCheck(!response.isAcknowledged(),
                ElasticMessage.AdminFailureMsg.UPDATE_SETTING_FAILURE);
    }

    /**
     * 获取setting
     *
     * @param index 索引名称
     * @return 获取setting的原生ElasticSearch响应
     */
    public GetSettingsResponse getSetting(String index) {
        return client.getClient().admin().indices()
                .prepareGetSettings(index)
                .get();
    }
}
