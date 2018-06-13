package com.wangyanrui.elasticsearch.api.core;

import com.wangyanrui.common.exception.OperaExceptionHandler;
import com.wangyanrui.elasticsearch.api.constant.ElasticConstant;
import com.wangyanrui.elasticsearch.api.constant.ElasticMessage;
import com.wangyanrui.elasticsearch.api.core.client.ElasticClient;
import com.wangyanrui.elasticsearch.api.core.factory.ElasticFactory;
import com.wangyanrui.elasticsearch.api.core.pojo.request.crud.GetAction;
import com.wangyanrui.elasticsearch.api.core.pojo.request.search.SearchAction;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Title: ElasticReader
 * Description: ElasticSearch 的 查询
 *
 * @author wangyanrui
 * @version 1.0
 * @date 2018/5/22 10:06
 */
@Slf4j
public class ElasticReader {

    private ElasticClient client;

    private static final String DEFAULT_TYPE_NAME = ElasticConstant.Default.TYPE_NAME;

    private ElasticReader() {
    }

    public ElasticReader(ElasticClient client) {
        this.client = client;
    }

    public ElasticReader(String address, String clusterName) {
        this.client = ElasticFactory.getInstance(address, clusterName);
    }

    public ElasticReader(String address, int port, String clusterName) {
        this.client = ElasticFactory.getInstance(address, port, clusterName);
    }


    /**
     * 获取单个文档
     *
     * @param index 索引名称
     * @param id    文档ID
     * @return GetResponse
     */
    public GetResponse get(String index, String id) {
        return get(new GetAction(index, id));
    }

    /**
     * 获取单个文档
     *
     * @param getAction 查询条件
     * @return GetResponse
     * @see GetAction
     */
    public GetResponse get(GetAction getAction) {
        GetRequestBuilder getRequestBuilder = client.getClient().
                prepareGet(getAction.getIndex(), DEFAULT_TYPE_NAME, getAction.getDocId());

        if (Objects.nonNull(getAction.getFields())) {
            getRequestBuilder.setStoredFields(getAction.getFields());
        }

        return doGet(getRequestBuilder);
    }

    /**
     * 获取多个文档(MultiGet)
     *
     * @param index  索引名称
     * @param docIds 文档ID(可变参数)
     * @return GetResponse集合
     */
    public List<GetResponse> multiGet(String index, String... docIds) {
        MultiGetRequestBuilder multiGetRequestBuilder = client.getClient().prepareMultiGet();
        for (String docId : docIds) {
            multiGetRequestBuilder.add(index, DEFAULT_TYPE_NAME, docId);
        }

        return doGet(multiGetRequestBuilder, docIds.length);
    }

    /**
     * 获取多个文档(MultiGet)
     *
     * @param itemList MultiGetRequest.Item的List集合
     * @return GetResponse集合
     * @see MultiGetRequest.Item
     */
    public List<GetResponse> multiGet(List<MultiGetRequest.Item> itemList) {
        MultiGetRequestBuilder multiGetRequestBuilder = client.getClient().prepareMultiGet();
        for (MultiGetRequest.Item item : itemList) {
            multiGetRequestBuilder.add(item.index(), DEFAULT_TYPE_NAME, item.id());
        }

        return doGet(multiGetRequestBuilder, itemList.size());
    }

    /**
     * 匹配查询文档
     *
     * @param searchAction 查询条件
     * @return 匹配查询响应
     * @see SearchAction
     */
    public SearchResponse search(SearchAction searchAction) {
        SearchRequestBuilder searchRequestBuilder = buildSearchRequestByQuery(searchAction);

        return doSearch(searchRequestBuilder);
    }

    /**
     * 匹配查询文档(MultiSearch)
     *
     * @param searchActions 批量查询条件
     * @return SearchResponse集合
     */
    public List<SearchResponse> multiSearch(SearchAction... searchActions) {
        MultiSearchRequestBuilder multiSearchRequestBuilder = client.getClient().prepareMultiSearch();

        for (SearchAction query : searchActions) {
            multiSearchRequestBuilder.add(buildSearchRequestByQuery(query));
        }

        MultiSearchResponse.Item[] responses = multiSearchRequestBuilder.get().getResponses();
        List<SearchResponse> result = new ArrayList<>(responses.length);

        for (MultiSearchResponse.Item item : responses) {
            result.add(item.getResponse());
        }

        return result;
    }


    /**
     * 执行Get请求
     *
     * @param getRequestBuilder Get请求
     * @return Get响应
     */
    private GetResponse doGet(GetRequestBuilder getRequestBuilder) {
        log.debug("\r\n" + getRequestBuilder.toString());

        GetResponse response = getRequestBuilder.get();

        OperaExceptionHandler.flagCheck(!response.isExists(),
                ElasticMessage.OperaFailureMsg.GET_FAILURE);

        return response;
    }

    /**
     * 执行MultiGet请求
     *
     * @param multiGetRequestBuilder MultiGet请求
     * @param getCapacity            请求的数量(主要用于初始化返回List的大小)
     * @return Get响应集合
     */
    private List<GetResponse> doGet(MultiGetRequestBuilder multiGetRequestBuilder, Integer getCapacity) {
        log.debug("\r\n" + multiGetRequestBuilder.toString());

        MultiGetResponse multiGetResponse = multiGetRequestBuilder.get();

        List<GetResponse> result = new ArrayList<>(getCapacity);
        for (MultiGetItemResponse multiGetItemResponse : multiGetResponse) {
            GetResponse response = multiGetItemResponse.getResponse();
            OperaExceptionHandler.flagCheck(!response.isExists(),
                    ElasticMessage.OperaFailureMsg.MULTI_GET_FAILURE);
            result.add(response);
        }

        return result;
    }

    /**
     * 执行查询
     *
     * @param searchRequestBuilder 查询请求
     * @return 查询响应
     */
    private SearchResponse doSearch(SearchRequestBuilder searchRequestBuilder) {
        log.debug("\r\n" + searchRequestBuilder.toString());

        return searchRequestBuilder.get();
    }


    /**
     * 根据搜索条件, 构建一个具体的搜索请求
     *
     * @param searchAction 搜索条件
     * @return 具体的搜索请求
     */
    private SearchRequestBuilder buildSearchRequestByQuery(SearchAction searchAction) {
        SearchRequestBuilder searchRequestBuilder = client.getClient()
                .prepareSearch(searchAction.getIndices())
                .setTypes(DEFAULT_TYPE_NAME);

        if (Objects.nonNull(searchAction.getQueryBuilder())) {
            searchRequestBuilder.setQuery(searchAction.getQueryBuilder());
        }
        if (Objects.nonNull(searchAction.getSortBuilder())) {
            searchRequestBuilder.addSort(searchAction.getSortBuilder());
        }
        if (Objects.nonNull(searchAction.getFrom())) {
            searchRequestBuilder.setFrom(searchAction.getFrom());
        }
        if (Objects.nonNull(searchAction.getSize())) {
            searchRequestBuilder.setSize(searchAction.getSize());
        }
        if (Objects.nonNull(searchAction.getFields())) {
            searchRequestBuilder.storedFields(searchAction.getFields());
        }

        if (Objects.nonNull(searchAction.getAggregationBuilders())) {
            for (AbstractAggregationBuilder aggregationBuilder : searchAction.getAggregationBuilders()) {
                searchRequestBuilder.addAggregation(aggregationBuilder);
            }
        }

        return searchRequestBuilder;
    }

}
