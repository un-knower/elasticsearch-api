package com.wangyanrui.elasticsearch.api.core.pojo.request.crud;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Title: GetAction
 * Description: Get行为 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class GetAction {
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
     * 查询结果集字段
     */
    private String[] fields;
    /**
     * 版本号
     */
    private Long version;

    public GetAction setFields(String... fields) {
        this.fields = fields;
        return this;
    }

    public GetAction setFields(List<String> fields) {
        fields.toArray(this.fields);
        return this;
    }

}
