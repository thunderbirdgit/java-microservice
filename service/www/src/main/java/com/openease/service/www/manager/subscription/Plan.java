package com.openease.service.www.manager.subscription;

import com.openease.common.data.model.account.Tier;
import com.openease.common.util.spring.BeanLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Currency;
import java.util.Locale;

/**
 * Subscription plan
 *
 * @author Alan Czajkowski
 */
public class Plan {

  private Tier tier;

  private Cost cost;

  public String getName() {
    final MessageSource messageSource = BeanLocator.byClassAndName(MessageSource.class, "messageSource");
    Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(tier.getNameKey(), null, locale);
  }

  public Tier getTier() {
    return tier;
  }

  public Plan setTier(Tier tier) {
    this.tier = tier;
    return this;
  }

  public Cost getCost() {
    return cost;
  }

  public Plan setCost(Cost cost) {
    this.cost = cost;
    return this;
  }

  public static class Cost {
    private Currency currency;
    private int perMonth;
    private int perYear;

    public Currency getCurrency() {
      return currency;
    }

    public Cost setCurrency(Currency currency) {
      this.currency = currency;
      return this;
    }

    public int getPerMonth() {
      return perMonth;
    }

    public Cost setPerMonth(int perMonth) {
      this.perMonth = perMonth;
      return this;
    }

    public int getPerYear() {
      return perYear;
    }

    public Cost setPerYear(int perYear) {
      this.perYear = perYear;
      return this;
    }
  }

}
