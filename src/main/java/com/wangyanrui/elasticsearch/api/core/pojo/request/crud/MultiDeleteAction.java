package com.wangyanrui.elasticsearch.api.core.pojo.request.crud;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Title: MultiDeleteAction
 * Description: 匹配删除行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class MultiDeleteAction {
    /**
     * 索引名称
     */
    @NonNull
    private String index;
    /**
     * 匹配内容
     */
    @NonNull
    private QueryBuilder queryBuilder;

}
