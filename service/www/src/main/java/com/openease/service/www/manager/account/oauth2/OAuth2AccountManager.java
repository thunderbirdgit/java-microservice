package com.openease.service.www.manager.account.oauth2;

import com.openease.common.data.model.account.Account;
import com.openease.common.data.model.account.OAuth2;
import com.openease.common.data.model.account.OAuth2Provider;
import com.openease.common.manager.account.AccountManager;
import com.openease.common.manager.exception.GeneralManagerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.openease.common.util.JsonUtils.toJson;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * OAuth 2.0 account manager
 *
 * @author Alan Czajkowski
 */
@Service
public class OAuth2AccountManager extends DefaultOAuth2UserService {

  private static final transient Logger LOG = LogManager.getLogger(OAuth2AccountManager.class);

  public static final String USER_ATTRIBUTE_KEY_EMAIL = "email";
  public static final String USER_ATTRIBUTE_KEY_EMAIL_VERIFIED = "email_verified";

  @Autowired
  private AccountManager accountManager;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
    String oAuth2ProviderId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
    LOG.debug("OAuth 2.0 provider: {}", () -> oAuth2ProviderId);

    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

    Account account;

    Object usernameObject = oAuth2User.getAttributes().get(USER_ATTRIBUTE_KEY_EMAIL);
    if (usernameObject != null) {
      String username = usernameObject.toString();
      if (isNotBlank(username)) {
        account = accountManager.findByUsername(username);
        if (account == null) {
          throw new OAuth2AuthenticationException("Username (email) in OAuth 2.0 user details is *not* associated with an account");
        }
      } else {
        throw new OAuth2AuthenticationException("Invalid username (email) in OAuth 2.0 user details");
      }
    } else {
      throw new OAuth2AuthenticationException("Invalid username (email) in OAuth 2.0 user details");
    }

    return account;
  }

  public Account createOrUpdateAccountFromOAuth2User(OAuth2User oAuth2User, OAuth2Provider oAuth2Provider) {
    Account account = null;

    if (oAuth2User != null) {
      LOG.trace("{}: {}", () -> oAuth2User.getClass().getSimpleName(), () -> toJson(oAuth2User, true));

      if (oAuth2User.getAttributes() != null) {
        LOG.trace("{}.attributes: {}", () -> oAuth2User.getClass().getSimpleName(), () -> toJson(oAuth2User.getAttributes(), true));

        Account oAuth2Account = new Account()
            .setOAuth2(new OAuth2()
                .setProvider(oAuth2Provider)
                .setUserAttributes(oAuth2User.getAttributes())
            );

        switch (oAuth2Provider) {
          case GOOGLE:
            updateAccountWithGoogleUserDetails(oAuth2Account);
            break;
          case FACEBOOK:
            updateAccountWithFacebookUserDetails(oAuth2Account);
            break;
          case APPLE:
            //TODO: implement
            break;
        }

        // prune attributes
        Map<String, Object> oAuth2UserAttributes = oAuth2User.getAttributes().entrySet()
            .stream()
            .filter(entry -> entry.getValue() instanceof String
                || entry.getValue() instanceof Boolean
                || entry.getValue() instanceof Long
                || entry.getValue() instanceof Integer
                || entry.getValue() instanceof Float
                || entry.getValue() instanceof Double
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        LOG.trace("pruned attributes: {}", () -> toJson(oAuth2UserAttributes, true));
        oAuth2Account.getOAuth2().setUserAttributes(oAuth2UserAttributes);

        String username = oAuth2Account.getUsername();

        if (isBlank(username)) {
          LOG.error("OAuth 2.0 account username (email) *not* found from provider: {}", () -> oAuth2Provider);
          throw new OAuth2AuthenticationException("OAuth 2.0 account username (email) *not* found from provider: " + oAuth2Provider);
        }
        LOG.info("OAuth 2.0 sign-in successful for account username (email) [{}] using provider: {}", () -> username, () -> oAuth2Provider);

        account = accountManager.findByUsername(username);
        if (account == null) {
          LOG.debug("Local account with username (email) [{}] does *not* exist, creating local account ...", () -> username);
          String randomPassword = UUID.randomUUID().toString();
          //TODO: remove
          LOG.trace("... with random pw: {}", () -> randomPassword);
          try {
            LOG.debug("oAuth2Account: {}", oAuth2Account::toStringUsingMixIn);
            Account savedAccount = accountManager.create(oAuth2Account, randomPassword);
            LOG.debug("account: {}", () -> (savedAccount == null ? null : savedAccount.toStringUsingMixIn()));
            account = savedAccount;
          } catch (GeneralManagerException me) {
            LOG.error(me::getMessage, me);
            throw new OAuth2AuthenticationException("Unable to create local account using OAuth 2.0 account from provider: " + oAuth2Provider);
          }
        } else {
          LOG.debug("account: {}", account::toStringUsingMixIn);
          LOG.debug("Local account with username (email) [{}] already exists, updating account OAuth 2.0 information ...", () -> username);
          account.getOAuth2()
              .setProvider(oAuth2Account.getOAuth2().getProvider())
              .setUserAttributes(oAuth2Account.getOAuth2().getUserAttributes());
          try {
            LOG.debug("oAuth2Account: {}", account::toStringUsingMixIn);
            Account updatedAccount = accountManager.update(account);
            LOG.debug("account: {}", () -> (updatedAccount == null ? null : updatedAccount.toStringUsingMixIn()));
            account = updatedAccount;
          } catch (GeneralManagerException me) {
            LOG.error(me::getMessage, me);
            throw new OAuth2AuthenticationException("Unable to create local account using OAuth 2.0 account from provider: " + oAuth2Provider);
          }
        }
      } else {
        LOG.error("{}.attributes is null", () -> oAuth2User.getClass().getSimpleName());
        throw new OAuth2AuthenticationException("Invalid OAuth 2.0 user attributes");
      }
    } else {
      LOG.error("{} is null", () -> oAuth2User.getClass().getSimpleName());
      throw new OAuth2AuthenticationException("Invalid OAuth 2.0 user");
    }

    return account;
  }

  private void updateAccountWithGenericUserDetails(Account oAuth2Account) {
    Map<String, Object> oAuth2UserAttributes = oAuth2Account.getOAuth2().getUserAttributes();

    Object emailObject = oAuth2UserAttributes.get(USER_ATTRIBUTE_KEY_EMAIL);
    if (emailObject != null) {
      oAuth2Account.setUsername(emailObject.toString());
    }

    Object emailVerifiedObject = oAuth2UserAttributes.get(USER_ATTRIBUTE_KEY_EMAIL_VERIFIED);
    if (emailVerifiedObject != null) {
      String emailVerified = trim(lowerCase(emailVerifiedObject.toString()));
      switch (emailVerified) {
        case "true":
        case "yes":
        case "y":
        case "1":
          oAuth2Account.setVerified(true);
          break;
      }
    }
  }

  private void updateAccountWithGoogleUserDetails(Account oAuth2Account) {
    Map<String, Object> oAuth2UserAttributes = oAuth2Account.getOAuth2().getUserAttributes();

    updateAccountWithGenericUserDetails(oAuth2Account);

    Object firstNameObject = oAuth2UserAttributes.get("given_name");
    if (firstNameObject != null) {
      String firstName = trim(firstNameObject.toString());
      if (isNotBlank(firstName)) {
        oAuth2Account.setFirstName(firstName);
      } else {  // first name fallback
        String name = trim((String) oAuth2UserAttributes.get("name"));
        if (isNotBlank(name)) {
          oAuth2Account.setFirstName(name);
        }
      }
    }

    Object lastNameObject = oAuth2UserAttributes.get("family_name");
    if (lastNameObject != null) {
      String lastName = trim(lastNameObject.toString());
      if (isNotBlank(lastName)) {
        oAuth2Account.setLastName(lastName);
      }
    }

    Object pictureUrlObject = oAuth2UserAttributes.get("picture");
    if (pictureUrlObject != null) {
      String pictureUrl = trim(pictureUrlObject.toString());
      if (isNotBlank(pictureUrl)) {
        oAuth2Account.setImageUrl(pictureUrl);
      }
    }
  }

  private void updateAccountWithFacebookUserDetails(Account oAuth2Account) {
    Map<String, Object> oAuth2UserAttributes = oAuth2Account.getOAuth2().getUserAttributes();

    updateAccountWithGenericUserDetails(oAuth2Account);

    Object firstNameObject = oAuth2UserAttributes.get("first_name");
    if (firstNameObject != null) {
      String firstName = trim(firstNameObject.toString());
      if (isNotBlank(firstName)) {
        oAuth2Account.setFirstName(firstName);
      } else {  // first name fallback
        String name = trim((String) oAuth2UserAttributes.get("name"));
        if (isNotBlank(name)) {
          oAuth2Account.setFirstName(name);
        }
      }
    }

    Object lastNameObject = oAuth2UserAttributes.get("last_name");
    if (lastNameObject != null) {
      String lastName = trim(lastNameObject.toString());
      if (isNotBlank(lastName)) {
        oAuth2Account.setLastName(lastName);
      }
    }

    Object pictureObject = oAuth2UserAttributes.get("picture");
    if (pictureObject != null) {
      if (pictureObject instanceof String) {  /* case: picture: "..." */
        String pictureUrl = trim(pictureObject.toString());
        if (isNotBlank(pictureUrl)) {
          oAuth2Account.setImageUrl(pictureUrl);
        }
      } else if (pictureObject instanceof Map) {  /* case: picture: [ data: [ url: "..." ] ] */
        Map<String, Object> pictureMap = (Map<String, Object>) pictureObject;
        Object pictureMapDataObject = pictureMap.get("data");
        if (pictureMapDataObject != null) {
          Map<String, Object> pictureMapData = (Map<String, Object>) pictureMapDataObject;
          Object pictureUrlObject = pictureMapData.get("url");
          if (pictureUrlObject != null) {
            String pictureUrl = trim(pictureUrlObject.toString());
            if (isNotBlank(pictureUrl)) {
              oAuth2Account.setImageUrl(pictureUrl);
            }
          }
        }
      }
    }
  }

}
