package com.openease.common.service.elasticsearch;

import com.openease.common.util.interfaces.Startable;
import com.openease.common.util.interfaces.Stoppable;
import org.springframework.beans.factory.InitializingBean;

/**
 * Interface: Elasticsearch embedded server
 *
 * @author Alan Czajkowski
 */
public interface ElasticsearchEmbeddedServer extends Startable, Stoppable, InitializingBean {

}
