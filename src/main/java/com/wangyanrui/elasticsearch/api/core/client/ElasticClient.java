package com.wangyanrui.elasticsearch.api.core.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * Title: ElasticClient
 * Description: ElasticSearch Client
 *
 * @author wangyanrui
 * @version 1.0
 */
@Slf4j
public class ElasticClient {
    @Getter
    private String address;
    @Getter
    private int port;
    @Getter
    private String clusterName;
    @Getter
    private TransportClient client;

    public ElasticClient(String address, String clusterName) {
        this.address = address;
        this.clusterName = clusterName;
    }

    public ElasticClient(String address, int port, String clusterName) {
        this.address = address;
        this.port = port;
        this.clusterName = clusterName;
    }

    /**
     * 重新建立连接
     */
    public void reConnect() {
        close();
        connect();
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (Objects.nonNull(this.client)) {
            client.close();
            client = null;
        }
    }

    /**
     * 建立连接
     *
     * @return ElasticClient Instance
     */
    public ElasticClient connect() {
        if (Objects.isNull(client)) {
            log.debug("ElasticSearch: ClusterName[{" + this.clusterName + "}] connecting...");

            // In order to enable sniffing, set client.transport.sniff to true
            Settings settings = Settings.builder()
                    .put("cluster.name", this.clusterName)
                    .put("client.transport.sniff", true).build();

            // connect
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(new InetSocketAddress(address, port)));

            log.debug("ElasticSearch: ClusterName[{" + this.clusterName + "}] connected");
        } else {
            log.debug("ElasticSearch: ClusterName[{" + this.clusterName + "}] is connected");
        }
        return this;
    }
}
