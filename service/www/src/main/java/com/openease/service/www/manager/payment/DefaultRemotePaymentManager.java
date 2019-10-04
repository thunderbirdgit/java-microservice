package com.openease.service.www.manager.payment;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.payment.RemotePaymentManager;
import com.openease.common.manager.payment.request.PaymentProcessRequest;
import com.openease.common.manager.payment.request.RemotePaymentProcessRequest;
import com.openease.common.web.util.RestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.gargoylesoftware.htmlunit.HttpMethod.POST;
import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.NOT;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_PAYMENT_ERROR_GENERALFAILURE;
import static com.openease.common.manager.lang.MessageKeys.MANAGER_PAYMENT_ERROR_REMOTEAPIFAILURE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Default remote payment manager
 *
 * @author Alan Czajkowski
 */
@Profile({NOT + LOCAL})
@Service
public class DefaultRemotePaymentManager implements RemotePaymentManager {

  private static final transient Logger LOG = LogManager.getLogger(DefaultRemotePaymentManager.class);

  @Value("${manager.remotePayment.api.baseUrl}")
  private String remotePaymentApiBaseUrl;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("remote payment API base URL: {}", remotePaymentApiBaseUrl);
    LOG.debug("Init finished");
  }

  @Override
  public void process(RemotePaymentProcessRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      //TODO

      PaymentProcessRequest paymentProcessRequest = new PaymentProcessRequest()
          .setDescription(request.getDescription())
          .setAmount(request.getAmount())
          .setCurrency(request.getCurrency())
          .setBillingToken(request.getBillingToken());

      // call remote payment API
      LOG.debug("API call: {} {} | Payload:{}{}", () -> POST, () -> remotePaymentApiBaseUrl, System::lineSeparator, () -> paymentProcessRequest);
      WebResponse webResponse = RestUtils.jsonRequest(POST, remotePaymentApiBaseUrl, paymentProcessRequest);

      // check response
      HttpStatus.Series httpStatusSeries = HttpStatus.Series.valueOf(webResponse.getStatusCode());
      switch (httpStatusSeries) {
        case SUCCESSFUL:
          LOG.debug("Call to remote payment API succeeded");
          break;
        default:
          String errorPayload = webResponse.getContentAsString(UTF_8);
          LOG.error("Call to remote payment API failed:{}{}", System::lineSeparator, () -> errorPayload);
          throw new GeneralManagerException(MANAGER_PAYMENT_ERROR_REMOTEAPIFAILURE, "Call to remote payment API failed");
      }
    } catch (Exception e) {
      LOG.error("Problem processing payment", e);
      throw new GeneralManagerException(MANAGER_PAYMENT_ERROR_GENERALFAILURE, e);
    }
  }

}
