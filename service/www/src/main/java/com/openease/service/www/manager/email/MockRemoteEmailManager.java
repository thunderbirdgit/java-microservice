package com.openease.service.www.manager.email;

import com.openease.common.manager.email.RemoteEmailManager;
import com.openease.common.manager.email.request.RemoteEmailSendRequest;
import com.openease.common.manager.exception.GeneralManagerException;
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
import static com.openease.common.manager.lang.MessageKeys.MANAGER_EMAIL_ERROR_GENERALFAILURE;
import static com.openease.common.util.JsonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * Mock remote email manager
 *
 * @author Alan Czajkowski
 */
@Profile({LOCAL})
@Service
public class MockRemoteEmailManager extends DefaultRemoteEmailManager implements RemoteEmailManager {

  private static final transient Logger LOG = LogManager.getLogger(MockRemoteEmailManager.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("Init finished");
  }

  @Override
  public void send(RemoteEmailSendRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      String dateAndTime = LocalDateTime.now().format(ISO_LOCAL_DATE_TIME)
          .replace("-", "")
          .replace(":", "");
      String filePath = "./target/" + RemoteEmailManager.class.getSimpleName() + "/" + request.getClass().getSimpleName() + "-" + dateAndTime + ".json";
      Path path = Paths.get(filePath);
      byte[] bytes = toJson(request).getBytes(UTF_8);
      FileUtils.forceMkdir(path.getParent().toFile());
      Files.write(path, bytes, CREATE);
    } catch (Exception e) {
      LOG.error("Problem sending email", e);
      throw new GeneralManagerException(MANAGER_EMAIL_ERROR_GENERALFAILURE, e);
    }
  }

}
