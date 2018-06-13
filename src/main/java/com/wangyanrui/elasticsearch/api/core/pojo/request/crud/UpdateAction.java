package com.wangyanrui.elasticsearch.api.core.pojo.request.crud;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Title: UpdateAction
 * Description: 更新行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UpdateAction {
    /**
     * 索引名称
     */
    @NonNull
    private String index;
    /**
     * 文档ID
     */
    @NonNull
    private String docId;
    /**
     * 更新内容
     * notification: Only need to contain the data which want to be modified
     */
    @NonNull
    private Object doc;
    /**
     * 版本号
     */
    private Long version;
}
