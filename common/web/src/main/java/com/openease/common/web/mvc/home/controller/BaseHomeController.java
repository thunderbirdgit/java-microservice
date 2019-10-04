package com.openease.common.web.mvc.home.controller;

import com.openease.common.web.mvc.base.BaseMvcController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

import static com.openease.common.util.JsonUtils.toJson;
import static com.openease.common.util.ResourceUtils.readResourceIntoString;

/**
 * Base home controller
 * - used to serve the default home (index) page
 *
 * @author Alan Czajkowski
 */
public abstract class BaseHomeController extends BaseMvcController {

  private static final transient Logger LOG = LogManager.getLogger(BaseHomeController.class);

  public void init() {
    LOG.debug("paths: {}", () -> toJson(getPaths()));
  }

  /**
   * Return home (index) page
   *
   * @param model       {@link Model}
   * @param httpRequest {@link HttpServletRequest}
   *
   * @return name of page
   */
  @GetMapping(path = {"", "/"})
  public String index(Model model, HttpServletRequest httpRequest) {
    LOG.debug("{} /:", httpRequest::getMethod);
    String logoAscii = readResourceIntoString(config.getVendor().getLogoAsciiResourcePath());
    model.addAttribute("logoAscii", logoAscii);
    return "index";
  }

}
