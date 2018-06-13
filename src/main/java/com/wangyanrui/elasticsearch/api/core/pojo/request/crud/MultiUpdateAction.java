package com.wangyanrui.elasticsearch.api.core.pojo.request.crud;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;

/**
 * Title: UpdateAction
 * Description: 更新行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class MultiUpdateAction {
    /**
     * 索引名称
     */
    @NonNull
    private String index;
    /**
     * 更新脚本
     */
    @NonNull
    private Script script;
    /**
     * 匹配内容
     */
    @NonNull
    private QueryBuilder queryBuilder;
}
