package com.openease.service.www;

import com.openease.common.config.Config;
import com.openease.common.manager.info.request.InfoVerifyValidatorRequest;
import com.openease.common.manager.info.response.InfoStatusResponse;
import com.openease.common.web.api.base.model.ApiFieldError;
import com.openease.common.web.api.base.model.Status;
import com.openease.common.web.api.base.model.response.ErrorApiResponse;
import com.openease.common.web.api.base.model.response.SuccessApiResponse;
import com.openease.common.web.api.information.InfoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.openease.common.Env.Constants.LOCAL;
import static com.openease.common.Env.Constants.TEST;
import static com.openease.common.manager.info.request.InfoVerifyValidatorRequest.VALIDATION_FAILURE_MESSAGE;
import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.util.JsonUtils.toObject;
import static com.openease.common.web.lang.MessageKeys.INFO_CONTROLLER_ERROR4XX_MESSAGE;
import static com.openease.common.web.lang.MessageKeys.INFO_CONTROLLER_ERROR5XX_MESSAGE;
import static com.openease.common.web.util.MvcUtils.getPath;
import static java.beans.Introspector.decapitalize;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({LOCAL, TEST})
public class WwwWebApplicationFT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Config config;

  @Autowired
  @Qualifier("messageSource")
  protected MessageSource messageSource;

  @Test
  public void testHome() throws Exception {
    MvcResult result = mockMvc
        .perform(
            get("/")
                .characterEncoding(UTF_8.toString())
        )
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andReturn();
  }

  @Test
  public void testIndex() throws Exception {
    MvcResult result = mockMvc
        .perform(
            get("/index.html")
                .characterEncoding(UTF_8.toString())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(TEXT_HTML))
        .andReturn();
  }

  @Test
  public void testInfoStatus() throws Exception {
    InfoStatusResponse expectedResult = new InfoStatusResponse()
        .setName(config.getName())
        .setVersion(config.getVersion())
        .setEnvironment(config.getEnv().toString());

    MvcResult result = mockMvc
        .perform(
            get(getPath(InfoController.class) + "/_status")
                .accept(APPLICATION_JSON)
                .characterEncoding(UTF_8.toString())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andReturn();

    MockHttpServletResponse httpResponse = result.getResponse();

    String json = httpResponse.getContentAsString(UTF_8);
    assertTrue(isNotBlank(json), "API response should not be empty");

    SuccessApiResponse<Map> apiResponse = toObject(json, SuccessApiResponse.class);
    InfoStatusResponse actualResult = toObject(toJson(apiResponse.getResult()), InfoStatusResponse.class);
    assertEquals(expectedResult.getName(), actualResult.getName(), "InfoStatusResponse.name is correct: " + expectedResult.getName());
    assertEquals(expectedResult.getVersion(), actualResult.getVersion(), "InfoStatusResponse.version is correct: " + expectedResult.getVersion());
    assertEquals(expectedResult.getEnvironment(), actualResult.getEnvironment(), "InfoStatusResponse.environment is correct: " + expectedResult.getEnvironment());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInfoVerifyValidator() throws Exception {
    InfoVerifyValidatorRequest request = new InfoVerifyValidatorRequest();

    List<ApiFieldError> expectedApiFieldErrors = new ArrayList<>();
    expectedApiFieldErrors.add(new ApiFieldError()
        .setObjectName(decapitalize(request.getClass().getSimpleName()))
        .setField("attribute")
        .setMessage(VALIDATION_FAILURE_MESSAGE)
        .setRejectedValue(null)
        .setCode(NotNull.class.getSimpleName())
    );
    Status expectedStatus = new Status(BAD_REQUEST)
        .setMessage("Field errors, total: " + expectedApiFieldErrors.size())
        .setAdditionalInfo(expectedApiFieldErrors);

    MvcResult result = mockMvc
        .perform(
            post(getPath(InfoController.class) + "/_verifyValidator")
                .accept(APPLICATION_JSON)
                .characterEncoding(UTF_8.toString())
                .contentType(APPLICATION_JSON)
                .content(toJson(request))
        )
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andReturn();

    MockHttpServletResponse httpResponse = result.getResponse();

    String json = httpResponse.getContentAsString(UTF_8);
    assertTrue(isNotBlank(json), "API response should not be empty");

    ErrorApiResponse apiResponse = toObject(json, ErrorApiResponse.class);
    Status actualStatus = apiResponse.getStatus();
    List<ApiFieldError> actualApiFieldErrors = (List<ApiFieldError>) apiResponse.getStatus().getAdditionalInfo();
    assertEquals(expectedStatus, actualStatus, "API response status should match expectation");
    assertEquals(expectedApiFieldErrors.size(), actualApiFieldErrors.size(), "API response field errors should match expectation");
  }

  @Test
  public void testInfoVerifyUtf8() throws Exception {
    final String UTF8_CHARS = "äöü";

    String expectedResult = UTF8_CHARS;

    MvcResult result = mockMvc
        .perform(
            get(getPath(InfoController.class) + "/_verifyUtf8")
                .param("q", UTF8_CHARS)
                .accept(APPLICATION_JSON)
                .characterEncoding(UTF_8.toString())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andReturn();

    MockHttpServletResponse httpResponse = result.getResponse();

    String json = httpResponse.getContentAsString(UTF_8);
    assertTrue(isNotBlank(json), "API response should not be empty");

    SuccessApiResponse<String> apiResponse = toObject(json, SuccessApiResponse.class);
    String actualResult = apiResponse.getResult();
    assertEquals(expectedResult, actualResult, "UTF-8 response should match request: " + expectedResult);
  }

  @Test
  public void testInfoVerifyError4xx() throws Exception {
    String localizedMessage = messageSource.getMessage(INFO_CONTROLLER_ERROR4XX_MESSAGE, null, getLocale());

    Status expectedStatus = new Status(I_AM_A_TEAPOT)
        .setMessage(localizedMessage);

    MvcResult result = mockMvc
        .perform(
            get(getPath(InfoController.class) + "/_verifyError4xx")
                .accept(APPLICATION_JSON)
                .characterEncoding(UTF_8.toString())
        )
        .andDo(print())
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andReturn();

    MockHttpServletResponse httpResponse = result.getResponse();

    String json = httpResponse.getContentAsString(UTF_8);
    assertTrue(isNotBlank(json), "API response should not be empty");

    ErrorApiResponse apiResponse = toObject(json, ErrorApiResponse.class);
    Status actualStatus = apiResponse.getStatus();
    assertEquals(expectedStatus, actualStatus, "API response status should match expectation");
  }

  @Test
  public void testInfoVerifyError5xx() throws Exception {
    String localizedMessage = messageSource.getMessage(INFO_CONTROLLER_ERROR5XX_MESSAGE, null, getLocale());

    Status expectedStatus = new Status(INTERNAL_SERVER_ERROR)
        .setMessage(localizedMessage);

    MvcResult result = mockMvc
        .perform(
            get(getPath(InfoController.class) + "/_verifyError5xx")
                .accept(APPLICATION_JSON)
                .characterEncoding(UTF_8.toString())
        )
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
        .andReturn();

    MockHttpServletResponse httpResponse = result.getResponse();

    String json = httpResponse.getContentAsString(UTF_8);
    assertTrue(isNotBlank(json), "API response should not be empty");

    ErrorApiResponse apiResponse = toObject(json, ErrorApiResponse.class);
    Status actualStatus = apiResponse.getStatus();
    assertEquals(expectedStatus, actualStatus, "API response status should match expectation");
  }

}
