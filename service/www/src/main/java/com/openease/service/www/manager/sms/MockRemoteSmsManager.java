package com.openease.service.www.manager.sms;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.sms.RemoteSmsManager;
import com.openease.common.manager.sms.request.RemoteSmsSendRequest;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_SMS_ERROR_GENERALFAILURE;
import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * Mock remote SMS manager
 *
 * @author Alan Czajkowski
 */
@Profile({LOCAL})
@Service
public class MockRemoteSmsManager extends DefaultRemoteSmsManager implements RemoteSmsManager {

  private static final transient Logger LOG = LogManager.getLogger(MockRemoteSmsManager.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  @Override
  public void send(RemoteSmsSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      String dateAndTime = LocalDateTime.now().format(ISO_LOCAL_DATE_TIME)
          .replace("-", "")
          .replace(":", "");
      String filePath = "./target/" + RemoteSmsManager.class.getSimpleName() + "/" + request.getClass().getSimpleName() + "-" + dateAndTime + ".json";
      Path path = Paths.get(filePath);
      byte[] bytes = toJson(request).getBytes(UTF_8);
      FileUtils.forceMkdir(path.getParent().toFile());
      Files.write(path, bytes, CREATE);
    } catch (Exception e) {
      LOG.error("Problem sending SMS", e);
      throw new GeneralManagerException(MANAGER_SMS_ERROR_GENERALFAILURE, e);
    }
  }

}
