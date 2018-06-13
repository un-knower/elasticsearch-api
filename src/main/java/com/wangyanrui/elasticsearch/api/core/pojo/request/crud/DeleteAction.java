package com.wangyanrui.elasticsearch.api.core.pojo.request.crud;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Title: DeleteAction
 * Description: 删除行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class DeleteAction {
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
     * 版本号
     */
    private Long version;
}
