package com.openease.common.manager.payment.request;

import com.openease.common.manager.base.model.BaseManagerModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Currency;

import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEPAYMENTPROCESSREQUEST_AMOUNT_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEPAYMENTPROCESSREQUEST_AMOUNT_POSITIVE;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEPAYMENTPROCESSREQUEST_BILLINGTOKEN_NOTBLANK;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEPAYMENTPROCESSREQUEST_CURRENCY_NOTNULL;
import static com.openease.common.manager.lang.MessageKeys.VALIDATION_REMOTEPAYMENTPROCESSREQUEST_DESCRIPTION_NOTBLANK;

/**
 * Remote payment: Process request
 *
 * @author Alan Czajkowski
 */
public class RemotePaymentProcessRequest extends BaseManagerModel {

  @NotBlank(message = "{" + VALIDATION_REMOTEPAYMENTPROCESSREQUEST_DESCRIPTION_NOTBLANK + "}")
  private String description;

  @NotNull(message = "{" + VALIDATION_REMOTEPAYMENTPROCESSREQUEST_AMOUNT_NOTNULL + "}")
  @Positive(message = "{" + VALIDATION_REMOTEPAYMENTPROCESSREQUEST_AMOUNT_POSITIVE + "}")
  private Long amount;

  @NotNull(message = "{" + VALIDATION_REMOTEPAYMENTPROCESSREQUEST_CURRENCY_NOTNULL + "}")
  private Currency currency;

  @NotBlank(message = "{" + VALIDATION_REMOTEPAYMENTPROCESSREQUEST_BILLINGTOKEN_NOTBLANK + "}")
  private String billingToken;

  public String getDescription() {
    return description;
  }

  public RemotePaymentProcessRequest setDescription(String description) {
    this.description = description;
    return this;
  }

  public long getAmount() {
    return amount;
  }

  public RemotePaymentProcessRequest setAmount(long amount) {
    this.amount = amount;
    return this;
  }

  public Currency getCurrency() {
    return currency;
  }

  public RemotePaymentProcessRequest setCurrency(Currency currency) {
    this.currency = currency;
    return this;
  }

  public String getBillingToken() {
    return billingToken;
  }

  public RemotePaymentProcessRequest setBillingToken(String billingToken) {
    this.billingToken = billingToken;
    return this;
  }

}
