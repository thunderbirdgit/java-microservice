package com.openease.common.service.elasticsearch;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.transport.Netty4Plugin;

import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * Elasticsearch embedded node
 *
 * @author Alan Czajkowski
 */
public class ElasticsearchEmbeddedNode extends Node {

  public ElasticsearchEmbeddedNode(Settings settings, String nodeName) {
    super(
        InternalSettingsPreparer.prepareEnvironment(
            settings,
            Collections.emptyMap(),
            null,
            () -> nodeName
        ),
        asList(
            Netty4Plugin.class
//            AnalysisPhoneticPlugin.class,
//            PainlessPlugin.class
        ),
        false
    );
  }

//  @Override
//  protected void registerDerivedNodeNameWithLogger(String s) {
//    // do nothing
//  }

}
