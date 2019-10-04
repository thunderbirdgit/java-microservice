package com.openease.common.data.model.log;

import com.openease.common.data.model.account.HasAccountId;
import com.openease.common.data.model.base.BaseAuditDataModel;
import org.springframework.data.elasticsearch.annotations.Document;

import static com.openease.common.data.model.log.EventLog.EVENT_LOGS;

/**
 * Event log data model
 *
 * @author Alan Czajkowski
 */
@Document(indexName = EVENT_LOGS)
public class EventLog extends BaseAuditDataModel<EventLog> implements HasAccountId<EventLog> {

  public static final String EVENT_LOGS = "event-logs";

  private String ipAddress;

  private String description;

  private String rawData;

  private String accountId;

  public EventLog() {
    this(null);
  }

  public EventLog(String id) {
    super(id);
    this.ipAddress = null;
    this.description = null;
    this.rawData = null;
    this.accountId = null;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public EventLog setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public EventLog setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getRawData() {
    return rawData;
  }

  public EventLog setRawData(String rawData) {
    this.rawData = rawData;
    return this;
  }

  @Override
  public String getAccountId() {
    return accountId;
  }

  @Override
  public EventLog setAccountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

}
