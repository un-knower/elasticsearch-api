package com.wangyanrui.elasticsearch.api.core;

import com.alibaba.fastjson.JSON;
import com.wangyanrui.common.exception.OperaExceptionHandler;
import com.wangyanrui.elasticsearch.api.constant.ElasticConstant;
import com.wangyanrui.elasticsearch.api.constant.ElasticMessage;
import com.wangyanrui.elasticsearch.api.core.client.ElasticClient;
import com.wangyanrui.elasticsearch.api.core.factory.ElasticFactory;
import com.wangyanrui.elasticsearch.api.core.pojo.request.crud.*;
import com.wangyanrui.elasticsearch.api.core.pojo.result.bulk.BulkResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.rest.RestStatus;

import java.util.Objects;

/**
 * Title: ElasticWriter
 * Description: ElasticSearch 的 增删改
 *
 * @author wangyanrui
 * @version 1.0
 * @date 2018/5/22 10:02
 */
@Slf4j
public class ElasticWriter {

    private ElasticClient client;

    private static final String DEFAULT_TYPE_NAME = ElasticConstant.Default.TYPE_NAME;

    private ElasticWriter() {
    }

    public ElasticWriter(ElasticClient client) {
        this.client = client;
    }

    public ElasticWriter(String address, String clusterName) {
        this.client = ElasticFactory.getInstance(address, clusterName);
    }

    public ElasticWriter(String address, int port, String clusterName) {
        this.client = ElasticFactory.getInstance(address, port, clusterName);
    }

    /**
     * 获取索引builder
     *
     * @return IndexRequestBuilder
     */
    public IndexRequestBuilder getIndexRequestBuilder() {
        return client.getClient().prepareIndex();
    }

    /**
     * 获取删除builder
     *
     * @return DeleteRequestBuilder
     */
    public DeleteRequestBuilder getDeleteRequestBuilder() {
        return client.getClient().prepareDelete();
    }

    /**
     * 获取更新builder
     *
     * @return UpdateRequestBuilder
     */
    public UpdateRequestBuilder getUpdateRequestBuilder() {
        return client.getClient().prepareUpdate();
    }

    /**
     * 创建文档.
     *
     * @param indexAction 索引内容封装成的对象
     * @return 索引响应
     */
    public IndexResponse index(IndexAction indexAction) {
        IndexResponse response = getIndexRequestBuilder()
                .setIndex(indexAction.getIndex())
                .setType(DEFAULT_TYPE_NAME)
                .setId(indexAction.getDocId())
                .setSource(
                        JSON.toJSONString(indexAction.getDoc()), XContentType.JSON
                )
                .setCreate(true)
                .get();

        OperaExceptionHandler.flagCheck(!RestStatus.CREATED.equals(response.status()),
                ElasticMessage.OperaFailureMsg.INDEX_FAILURE);

        return response;
    }

    /**
     * 删除文档.
     *
     * @param deleteAction 删除的条件封装成的对象
     * @return 删除响应
     */
    public DeleteResponse delete(DeleteAction deleteAction) {
        DeleteRequestBuilder deleteRequestBuilder = getDeleteRequestBuilder()
                .setIndex(deleteAction.getIndex())
                .setType(DEFAULT_TYPE_NAME)
                .setId(deleteAction.getDocId());

        if (Objects.nonNull(deleteAction.getVersion())) {
            deleteRequestBuilder.setVersion(deleteAction.getVersion());
        }

        DeleteResponse response = deleteRequestBuilder.get();

        OperaExceptionHandler.flagCheck(!RestStatus.OK.equals(response.status()),
                ElasticMessage.OperaFailureMsg.DELETE_FAILURE);

        return response;
    }

    /**
     * 删除匹配文档.
     *
     * @param deleteAction 批量删除的条件封装成的对象
     * @return 批量删除响应
     */
    public BulkByScrollResponse deleteByQuery(MultiDeleteAction deleteAction) {
        return DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client.getClient())
                .source(deleteAction.getIndex())
                .filter(deleteAction.getQueryBuilder())
                .get();
    }

    /**
     * 更新文档.
     * <p>
     * notification: The param(<em>updateAction.doc</em>) only need to contain the data which want to be modified
     *
     * @param updateAction 更新内容封装成的对象
     * @return 更新响应
     */
    public UpdateResponse update(UpdateAction updateAction) {
        UpdateRequestBuilder updateRequestBuilder = getUpdateRequestBuilder()
                .setIndex(updateAction.getIndex())
                .setType(DEFAULT_TYPE_NAME)
                .setId(updateAction.getDocId())
                .setDoc(
                        JSON.toJSONString(updateAction.getDoc()),
                        XContentType.JSON
                );

        if (Objects.nonNull(updateAction.getVersion())) {
            updateRequestBuilder.setVersion(updateAction.getVersion());
        }

        UpdateResponse response = updateRequestBuilder.get();

        OperaExceptionHandler.flagCheck(!RestStatus.OK.equals(response.status()),
                ElasticMessage.OperaFailureMsg.UPDATE_FAILURE);

        return response;
    }

    /**
     * 更新匹配文档.
     * <p>
     * ElasticSearch must install plugin
     *
     * @param updateAction 更新内容封装成的对象
     * @return 更新响应
     */
    public BulkByScrollResponse updateByQuery(MultiUpdateAction updateAction) {
        return UpdateByQueryAction.INSTANCE
                .newRequestBuilder(client.getClient())
                .source(updateAction.getIndex())
                .script(updateAction.getScript())
                .filter(updateAction.getQueryBuilder())
                .abortOnVersionConflict(false)
                .get();
    }

    /**
     * 批量索引.
     *
     * @param requestBuilders IndexRequestBuilder(可变参数)
     * @return BulkResult.BulkIndexResult Instance
     * @see IndexRequest
     * @see BulkResult.BulkIndexResult
     */
    public BulkResult.BulkIndexResult bulkIndex(IndexRequestBuilder... requestBuilders) {
        return bulk(requestBuilders).getIndexResult();
    }

    /**
     * 批量 删除.
     *
     * @param requestBuilders DeleteRequestBuilder(可变参数)
     * @return BulkResult.BulkDeleteResult Instance
     * @see DeleteRequest
     * @see BulkResult.BulkDeleteResult
     */
    public BulkResult.BulkDeleteResult bulkDelete(DeleteRequestBuilder... requestBuilders) {
        return bulk(requestBuilders).getDeleteResult();
    }

    /**
     * 批量 更新.
     *
     * @param requestBuilders UpdateRequestBuilder(可变参数)
     * @return BulkResult.BulkUpdateResult Instance
     * @see UpdateRequest
     * @see BulkResult.BulkUpdateResult
     */
    public BulkResult.BulkUpdateResult bulkUpdate(UpdateRequestBuilder... requestBuilders) {
        return bulk(requestBuilders).getUpdateResult();
    }

    /**
     * 批量 索引、删除、更新.
     *
     * @param requestBuilders ActionRequest(可变参数)
     * @return BulkResult Instance
     * @see IndexRequest
     * @see DeleteRequest
     * @see UpdateRequest
     * @see BulkResult
     */
    public BulkResult bulk(ActionRequestBuilder... requestBuilders) {
        BulkRequestBuilder bulkRequestBuilder = client.getClient().prepareBulk();

        for (ActionRequestBuilder requestBuilder : requestBuilders) {
            if (requestBuilder instanceof IndexRequestBuilder) {
                bulkRequestBuilder.add((IndexRequestBuilder) requestBuilder);
            } else if (requestBuilder instanceof DeleteRequestBuilder) {
                bulkRequestBuilder.add((DeleteRequestBuilder) requestBuilder);
            } else if (requestBuilder instanceof UpdateRequestBuilder) {
                bulkRequestBuilder.add((UpdateRequestBuilder) requestBuilder);
            } else {
                OperaExceptionHandler.throwException(ElasticMessage.OperaErrorMsg.BULK_ERROR);
            }
        }

        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        BulkItemResponse[] items = bulkItemResponses.getItems();

        // build result data
        BulkResult result = new BulkResult();
        if (bulkItemResponses.hasFailures()) {
            result.setFailureMessage(bulkItemResponses.buildFailureMessage());
        }

        for (BulkItemResponse item : items) {
            ActionResponse response = item.getResponse();

            if (response instanceof IndexResponse) {
                IndexResponse indexResponse = (IndexResponse) response;
                if (RestStatus.CREATED.equals(indexResponse.status())) {
                    result.getIndexResult().getSuccessResponseList().add(indexResponse);
                } else {
                    result.getIndexResult().getFailureResponseList().add(indexResponse);
                }
            } else if (response instanceof DeleteResponse) {

                DeleteResponse deleteResponse = (DeleteResponse) response;
                if (RestStatus.OK.equals(deleteResponse.status())) {
                    result.getDeleteResult().getSuccessResponseList().add(deleteResponse);
                } else {
                    result.getDeleteResult().getFailureResponseList().add(deleteResponse);
                }

            } else if (response instanceof UpdateResponse) {

                UpdateResponse updateResponse = (UpdateResponse) response;
                if (RestStatus.OK.equals(updateResponse.status())) {
                    result.getUpdateResult().getSuccessResponseList().add(updateResponse);
                } else {
                    result.getUpdateResult().getFailureResponseList().add(updateResponse);
                }

            }
        }

        return result;
    }
}
