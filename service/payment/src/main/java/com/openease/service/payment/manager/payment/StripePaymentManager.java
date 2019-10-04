package com.openease.service.payment.manager.payment;

import com.openease.common.manager.exception.GeneralManagerException;
import com.openease.common.manager.payment.request.PaymentProcessRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.TokenCreateParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.openease.common.manager.lang.MessageKeys.MANAGER_PAYMENT_ERROR_GENERALFAILURE;
import static com.openease.common.util.JsonPasswordSerializer.MASK;

/**
 * Stripe Payment manager
 *
 * @author Alan Czajkowski
 * @see <a href="https://stripe.com/docs/api">Stripe | API</a>
 */
@Service
public class StripePaymentManager implements PaymentManager {

  private static final transient Logger LOG = LogManager.getLogger(StripePaymentManager.class);

  @Value("${manager.payment.stripe.api.key.public}")
  private String apiPublicKey;

  @Value("${manager.payment.stripe.api.key.private}")
  private String apiPrivateKey;

  private RequestOptions requestOptions;

  @PostConstruct
  public void init() {
    LOG.debug("Init started");

    LOG.debug("Stripe API public key: {}", apiPublicKey);
    LOG.debug("Stripe API private key: {}", MASK);

    requestOptions = new RequestOptions.RequestOptionsBuilder()
        .setApiKey(apiPrivateKey)
        .build();

    LOG.debug("Init finished");
  }

  @Override
  public void process(PaymentProcessRequest request) throws GeneralManagerException {
    LOG.debug("request: {}", request);

    try {
      ChargeCreateParams chargeCreateParams = ChargeCreateParams.builder()
          .setAmount(request.getAmount())
          .setCurrency(request.getCurrency().toString())
          .setDescription(request.getDescription())
          .setSource(request.getBillingToken())
          .build();

      Charge charge = Charge.create(chargeCreateParams, requestOptions);
      //TODO: charge

//      TokenCreateParams tokenCreateParams = TokenCreateParams.builder()
//          .setAccount().build();
//      Token token = Token.create(tokenCreateParams);
    } catch (StripeException se) {
      LOG.error("Problem processing payment", se);
      throw new GeneralManagerException(MANAGER_PAYMENT_ERROR_GENERALFAILURE, "Problem processing payment", se);
    }
  }

}
