package com.openease.common.manager.log;

import com.openease.common.data.model.account.Account;
import com.openease.common.data.model.log.EventLog;
import com.openease.common.manager.exception.GeneralManagerException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Interface: Event log manager
 *
 * @author Alan Czajkowski
 */
public interface EventLogManager {

  EventLog create(HttpServletRequest httpRequest, String description, String rawData, String accountId) throws GeneralManagerException;

  EventLog create(HttpServletRequest httpRequest, String description, String rawData, Account account) throws GeneralManagerException;

  EventLog create(HttpServletRequest httpRequest, String description, String rawData) throws GeneralManagerException;

  EventLog create(HttpServletRequest httpRequest, String description, Account account) throws GeneralManagerException;

  EventLog create(HttpServletRequest httpRequest, String description) throws GeneralManagerException;

  EventLog create(HttpServletRequest httpRequest, Account account) throws GeneralManagerException;

  EventLog create(HttpServletRequest httpRequest) throws GeneralManagerException;

  List<EventLog> findByAccountId(String accountId, int pageNumber, int pageSize);

  List<EventLog> findByIpAddress(String ipAddress, int pageNumber, int pageSize);

  boolean isEnabled();

}
