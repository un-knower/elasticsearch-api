package com.wangyanrui.elasticsearch.api.core.factory;

import com.wangyanrui.elasticsearch.api.core.client.ElasticClient;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: ElasticFactory
 * Description: ElasticSearch Factory Class
 *
 * @author wangyanrui
 * @version 1.0
 */
@Slf4j
public class ElasticFactory {

    private static final Map<String, ElasticClient> CACHE = new ConcurrentHashMap<>();

    /**
     * Get Instance
     *
     * @param address     address
     * @param clusterName clusterName
     * @return ElasticClient Instance
     */
    @Synchronized
    public static ElasticClient getInstance(String address, String clusterName) {
        return getInstance(address, 9300, clusterName);
    }

    /**
     * Get Instance
     *
     * @param address     address
     * @param port        port
     * @param clusterName clusterName
     * @return ElasticClient Instance
     */
    @Synchronized
    public static ElasticClient getInstance(String address, int port, String clusterName) {
        ElasticClient instance = CACHE.get(clusterName);
        if (Objects.isNull(instance)) {
            instance = new ElasticClient(address, port, clusterName);
            CACHE.put(clusterName, instance);
        }

        return instance.connect();
    }

    /**
     * shutdown
     */
    @Synchronized
    public static void shutdown() {
        int count = 0;
        log.debug("ElasticClient shutdown starting ...");
        for (ElasticClient instance : CACHE.values()) {
            if (Objects.nonNull(instance)) {
                instance.close();
                instance = null;
                count++;
            }
        }
        log.debug("ElasticClient shutdown success, close count = " + count);
    }
}
