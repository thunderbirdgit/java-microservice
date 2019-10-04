package com.openease.common.service.elasticsearch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.openease.common.Env.Constants.LOCAL;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.elasticsearch.cluster.ClusterName.CLUSTER_NAME_SETTING;
import static org.elasticsearch.common.network.NetworkModule.HTTP_TYPE_SETTING;
import static org.elasticsearch.common.network.NetworkModule.TRANSPORT_TYPE_SETTING;
import static org.elasticsearch.discovery.DiscoveryModule.SINGLE_NODE_DISCOVERY_TYPE;
import static org.elasticsearch.env.Environment.PATH_HOME_SETTING;
import static org.elasticsearch.transport.Netty4Plugin.NETTY_HTTP_TRANSPORT_NAME;
import static org.elasticsearch.transport.Netty4Plugin.NETTY_TRANSPORT_NAME;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Elasticsearch embedded server (local)
 *
 * @author Alan Czajkowski
 */
@Service("elasticsearchEmbeddedServer")
@Order(HIGHEST_PRECEDENCE)
@Profile({LOCAL})
public class ElasticsearchEmbeddedServerLocal implements ElasticsearchEmbeddedServer {

  private static final transient Logger LOG = LogManager.getLogger(ElasticsearchEmbeddedServerLocal.class);

  private static final String DISCOVERY_TYPE_KEY = "discovery.type";
  private static final String NETWORK_HOST_KEY = "network.host";

  private static final AtomicBoolean STARTED = new AtomicBoolean(false);

  @Value("${service.elasticsearchEmbedded.version}")
  private String version;

  @Value("${service.elasticsearchEmbedded.homeDirectory}")
  private String homeDirectory;

  @Value("${service.elasticsearchEmbedded.cluster.name}")
  private String clusterName;

  @Value("${service.elasticsearchEmbedded.node.name}")
  private String nodeName;

  @Value("${service.elasticsearchEmbedded.network.host}")
  private String networkHost;

  private Node node;

  private Client client;

  /**
   * {@link InitializingBean#afterPropertiesSet} needs to be used
   * instead of {@link PostConstruct}
   *
   * @throws Exception
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    start();
  }

  @Override
  public void start() {
    if (!STARTED.compareAndSet(false, true)) {
      LOG.warn("Server already running");
    } else {
      LOG.info("Server starting ...");
      LOG.debug("version: {}", version);
      LOG.debug("home directory: {}", homeDirectory);
      LOG.debug("cluster name: {}", clusterName);
      LOG.debug("node name: {}", nodeName);
      LOG.debug("network host: {}", networkHost);

      Settings settings = Settings.builder()
          .put(PATH_HOME_SETTING.getKey(), homeDirectory)
          .put(DISCOVERY_TYPE_KEY, SINGLE_NODE_DISCOVERY_TYPE)
          .put(TRANSPORT_TYPE_SETTING.getKey(), NETTY_TRANSPORT_NAME)
          .put(HTTP_TYPE_SETTING.getKey(), NETTY_HTTP_TRANSPORT_NAME)
          .put(CLUSTER_NAME_SETTING.getKey(), clusterName)
          .put(NETWORK_HOST_KEY, networkHost)
          .build();

      try {
        deleteDirectory(new File(homeDirectory));
        node = new ElasticsearchEmbeddedNode(settings, nodeName).start();
        client = node.client();
      } catch (Exception e) {
        LOG.error(e::getMessage, e);
        throw new RuntimeException(e.getMessage(), e);
      }

      LOG.info("Server started");
    }
  }

  @PreDestroy
  @Override
  public void stop() {
    if (!STARTED.compareAndSet(true, false)) {
      LOG.warn("Server not started");
    } else {
      LOG.info("Server stopping ...");

      try {
        getClient().close();
        client = null;
        getNode().close();
        node = null;
        deleteDirectory(new File(homeDirectory));
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }

      LOG.info("Server stopped");
    }
  }

  private Client getClient() {
    if (client == null) {
      throw new RuntimeException("Server not started");
    }
    return client;
  }

  private Node getNode() {
    if (node == null) {
      throw new RuntimeException("Server not started");
    }
    return node;
  }

}
