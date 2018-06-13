package com.wangyanrui.elasticsearch.api.core.pojo.result.bulk;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: BulkResult
 * Description: 批量操作结果 数据封装
 *
 * @author wangyanrui
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class BulkResult {
    /**
     * index result
     */
    private BulkIndexResult indexResult = new BulkIndexResult();
    /**
     * delete result
     */
    private BulkDeleteResult deleteResult = new BulkDeleteResult();
    /**
     * update result
     */
    private BulkUpdateResult updateResult = new BulkUpdateResult();
    /**
     * failure message
     */
    private String failureMessage;

    @Setter
    @Getter
    private static class BulkSubResult<T> {
        /**
         * success response result list
         */
        public List<T> successResponseList = new ArrayList<>();
        /**
         * failure response result list
         */
        public List<T> failureResponseList = new ArrayList<>();
    }

    /**
     * Title: BulkIndexResult
     * Description: 批量索引结果 数据封装
     */
    public class BulkIndexResult extends BulkSubResult<IndexResponse> {

    }

    /**
     * Title: BulkDeleteResult
     * Description: 批量删除结果 数据封装
     */
    public class BulkDeleteResult extends BulkSubResult<DeleteResponse> {

    }

    /**
     * Title: BulkUpdateResult
     * Description: 批量更新结果 数据封装
     */
    public class BulkUpdateResult extends BulkSubResult<UpdateResponse> {

    }
}
