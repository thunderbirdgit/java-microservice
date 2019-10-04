package com.openease.common.data.lang;

/**
 * Message keys
 * location: classpath:/com/openease/common/data/lang/common-data-messages.properties
 *
 * @author Alan Czajkowski
 */
public class MessageKeys {

  /**
   * General messages
   */
  public static final String CRUD_CREATE_SUCCESS =
      "crud.create.success";
  public static final String CRUD_CREATE_FAILURE =
      "crud.create.failure";
  public static final String CRUD_READ_SUCCESS =
      "crud.read.success";
  public static final String CRUD_UPDATE_SUCCESS =
      "crud.update.success";
  public static final String CRUD_UPDATE_STALE =
      "crud.update.stale";
  public static final String CRUD_DELETE_SUCCESS =
      "crud.delete.success";
  public static final String CRUD_DELETE_FAILURE =
      "crud.delete.failure";
  public static final String CRUD_ID_MISMATCH =
      "crud.id.mismatch";
  public static final String CRUD_NOTFOUND =
      "crud.notFound";
  public static final String CRUD_BADREQUEST =
      "crud.badRequest";
  public static final String CRUD_OPERATION_UNSUPPORTED =
      "crud.operation.unsupported";
  public static final String CRUD_DISABLE_SUCCESS =
      "crud.disable.success";
  public static final String CRUD_DISABLE_FAILURE =
      "crud.disable.failure";

  /**
   * General validation messages
   */
  public static final String VALIDATION_ID_PATTERNMISMATCH =
      "validation.id.patternMismatch";
  public static final String VALIDATION_CREATED_NOTNULL =
      "validation.created.notNull";
  public static final String VALIDATION_LASTMODIFIED_NOTNULL =
      "validation.lastModified.notNull";
  public static final String VALIDATION_EMAIL_NOTBLANK =
      "validation.email.notBlank";
  public static final String VALIDATION_EMAIL_INVALID =
      "validation.email.invalid";
  public static final String VALIDATION_PASSWORD_NOTBLANK =
      "validation.password.notBlank";
  public static final String VALIDATION_PASSWORD_LENGTH =
      "validation.password.length";

  /**
   * Validation messages for {@link com.openease.common.data.model.account.Account}
   */
  public static final String VALIDATION_ACCOUNT_PASSWORDRESETCODE_NOTBLANK =
      "validation.account.passwordResetCode.notBlank";
  public static final String VALIDATION_ACCOUNT_FIRSTNAME_NOTBLANK =
      "validation.account.firstName.notBlank";
  public static final String VALIDATION_ACCOUNT_LASTNAME_NOTBLANK =
      "validation.account.lastName.notBlank";
  public static final String VALIDATION_ACCOUNT_TIER_NOTNULL =
      "validation.account.tier.notNull";
  public static final String VALIDATION_ACCOUNT_ROLES_NOTEMPTY =
      "validation.account.roles.notEmpty";

  /**
   * Messages for enum {@link com.openease.common.data.model.account.Tier}
   */
  public static final String ENUM_TIER_TIER0_NAME =
      "tier.tier0.name";
  public static final String ENUM_TIER_TIER1_NAME =
      "tier.tier1.name";
  public static final String ENUM_TIER_TIER2_NAME =
      "tier.tier2.name";

  /**
   * Messages for enum {@link com.openease.common.data.model.account.Gender}
   */
  public static final String ENUM_GENDER_FEMALE_NAME =
      "gender.female.name";
  public static final String ENUM_GENDER_MALE_NAME =
      "gender.male.name";
  public static final String ENUM_GENDER_OTHER_NAME =
      "gender.other.name";
  public static final String ENUM_GENDER_UNKNOWN_NAME =
      "gender.unknown.name";

}
