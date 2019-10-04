package com.openease.service.payment.api.v1.payments;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.payment.request.PaymentProcessRequest;
import com.openease.common.web.api.base.BaseApiController;
import com.openease.common.web.api.base.exception.ApiException;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.service.payment.manager.payment.PaymentManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.web.api.ApiVersion.Constants.V1_CONTEXT;
import static com.openease.common.web.util.ApiUtils.createSuccessApiResponse;
import static com.openease.service.payment.api.v1.payments.PaymentsController.PAYMENTS_CONTEXT;
import static com.openease.service.payment.manager.payment.PaymentManager.PAYMENTS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Payments controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = V1_CONTEXT + PAYMENTS_CONTEXT, produces = APPLICATION_JSON_VALUE)
public class PaymentsController extends BaseApiController {

  private static final transient Logger LOG = LogManager.getLogger(PaymentsController.class);

  public static final String PAYMENTS_CONTEXT = "/" + PAYMENTS;

  @Autowired
  private PaymentManager paymentManager;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    LOG.debug("paths: {}", () -> toJson(getPaths()));
    LOG.debug("Init finished");
  }

  /**
   * Call to process payment
   *
   * @return {@link SuccessApiResponse}
   */
  @PostMapping(path = {"", "/"})
  public SuccessApiResponse process(@RequestBody @Valid PaymentProcessRequest request) {
    LOG.trace("request: {}", request);
    if (request == null) {
      throw new ApiException();
    }

    SuccessApiResponse response;

    try {
      paymentManager.process(request);
      response = createSuccessApiResponse(CREATED);
    } catch (GeneralManagerException me) {
      LOG.warn(me::getMessage, me);
      throw new ApiException(me);
    }

    LOG.trace("response: {}", response);
    return response;
  }

}
