package com.wangyanrui.elasticsearch.api.core.pojo.request.admin;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Title: AdminAction
 * Description: Admin行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class AdminAction {
    /**
     * 索引名称
     */
    @NonNull
    private String index;
    /**
     * setting
     */
    private String setting;
    /**
     * mapping
     */
    private String mapping;
}
