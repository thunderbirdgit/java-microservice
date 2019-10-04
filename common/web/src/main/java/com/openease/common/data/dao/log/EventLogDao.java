package com.openease.common.data.dao.log;

import com.openease.common.data.dao.base.BaseDao;
import com.openease.common.data.model.log.EventLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Event log DAO
 *
 * @author Alan Czajkowski
 */
public interface EventLogDao extends BaseDao<EventLog> {

  Page<EventLog> findByAccountIdOrderByCreatedDesc(String accountId, Pageable pageable);

  Page<EventLog> findByIpAddressOrderByCreatedDesc(String ipAddress, Pageable pageable);

}
