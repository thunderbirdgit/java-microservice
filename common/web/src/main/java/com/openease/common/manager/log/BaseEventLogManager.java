package com.openease.common.manager.log;

import com.openease.common.data.dao.log.EventLogDao;
import com.openease.common.data.model.account.Account;
import com.openease.common.data.model.log.EventLog;
import com.openease.common.manager.exception.GeneralManagerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.openease.common.data.lang.MessageKeys.CRUD_BADREQUEST;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.util.HttpUtils.getHeadersAsMap;
import static com.openease.common.web.util.HttpUtils.getIpAddress;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.PageRequest.of;

/**
 * Base event log manager
 *
 * @author Alan Czajkowski
 */
public abstract class BaseEventLogManager implements EventLogManager {

  private static final transient Logger LOG = LogManager.getLogger(BaseEventLogManager.class);

  @Value("${manager.event-log.enabled}")
  protected boolean enabled;

  @Autowired
  protected EventLogDao eventLogDao;

  public void init() {
    LOG.debug("enabled: {}", enabled);
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, String description, String rawData, String accountId) throws GeneralManagerException {
    EventLog eventLog = null;

    if (enabled) {
      eventLog = new EventLog()
          .setId()
          .setIpAddress(getIpAddress(httpRequest))
          .setDescription(description)
          .setRawData(rawData)
          .setAccountId(accountId);
      LOG.trace("create({})", toJson(eventLog));
      try {
        eventLog = eventLogDao.save(eventLog);
      } catch (Exception e) {
        LOG.warn(e.getMessage());
        throw new GeneralManagerException(CRUD_BADREQUEST, e.getMessage());
      }
    } else {
      LOG.warn("{} disabled, ignoring create(..., \"{}\", ...)", EventLogManager.class::getSimpleName, () -> description);
    }

    return eventLog;
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, String description, String rawData, Account account) throws GeneralManagerException {
    String accountId = account == null
        ? null
        : account.getId();
    return create(httpRequest, description, rawData, accountId);
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, String description, String rawData) throws GeneralManagerException {
    return create(httpRequest, description, rawData, (String) null);
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, String description, Account account) throws GeneralManagerException {
    String accountId = account == null
        ? null
        : account.getId();
    return create(httpRequest, description, null, accountId);
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, String description) throws GeneralManagerException {
    return create(httpRequest, description, null, (String) null);
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest, Account account) throws GeneralManagerException {
    EventLog eventLog = null;

    if (enabled) {
      String queryString = isNotBlank(httpRequest.getQueryString())
          ? ("?" + httpRequest.getQueryString())
          : "";
      String description = "HTTP " + httpRequest.getMethod() + " " + httpRequest.getRequestURL() + queryString;
      String accountId = account == null
          ? null
          : account.getId();
      eventLog = create(httpRequest, description, toJson(getHeadersAsMap(httpRequest)), accountId);
    } else {
      LOG.warn("{} disabled, ignoring create({})", EventLogManager.class::getSimpleName, HttpServletRequest.class::getSimpleName);
    }

    return eventLog;
  }

  @Override
  public EventLog create(HttpServletRequest httpRequest) throws GeneralManagerException {
    return create(httpRequest, (Account) null);
  }

  @Override
  public List<EventLog> findByAccountId(String accountId, int pageNumber, int pageSize) {
    Page<EventLog> eventLogs = eventLogDao.findByAccountIdOrderByCreatedDesc(accountId, of(pageNumber, pageSize));
    return eventLogs.getContent();
  }

  @Override
  public List<EventLog> findByIpAddress(String ipAddress, int pageNumber, int pageSize) {
    Page<EventLog> eventLogs = eventLogDao.findByIpAddressOrderByCreatedDesc(ipAddress, of(pageNumber, pageSize));
    return eventLogs.getContent();
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

}
