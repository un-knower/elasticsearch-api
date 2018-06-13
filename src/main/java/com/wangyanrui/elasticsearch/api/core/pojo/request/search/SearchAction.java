package com.wangyanrui.elasticsearch.api.core.pojo.request.search;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.List;

/**
 * Title: SearchAction
 * Description: 搜索行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SearchAction {
    /**
     * 索引名称
     */
    @NonNull
    private String[] indices;
    /**
     * QueryBuilder
     */
    private QueryBuilder queryBuilder;
    /**
     * 排序方式
     */
    private SortBuilder sortBuilder;
    /**
     * 数据起始
     */
    private Integer from;
    /**
     * 数据量
     */
    private Integer size;
    /**
     * 查询结果集字段
     */
    private String[] fields;
    /**
     * 聚合 数组
     */
    private AbstractAggregationBuilder[] aggregationBuilders;
    /**
     * PostFilter
     */
    private QueryBuilder postFilter;

    public SearchAction(String... indices) {
        this.indices = indices;
    }

    public SearchAction setIndices(String... indices) {
        this.indices = indices;
        return this;
    }

    public SearchAction setFields(String... fields) {
        this.fields = fields;
        return this;
    }

    public SearchAction setAggregationBuilders(AbstractAggregationBuilder... aggregationBuilders) {
        this.aggregationBuilders = aggregationBuilders;
        return this;
    }

    public SearchAction setIndices(List<String> indices) {
        indices.toArray(this.indices);
        return this;
    }

    public SearchAction setFields(List<String> fields) {
        fields.toArray(this.fields);
        return this;
    }

    public SearchAction setAggregationBuilders(List<AbstractAggregationBuilder> aggregationBuilders) {
        aggregationBuilders.toArray(this.aggregationBuilders);
        return this;
    }

}
