package com.openease.service.www.mvc;

import com.openease.common.web.mvc.home.controller.BaseHomeController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Home controller
 *
 * @author Alan Czajkowski
 */
@Controller
@RequestMapping(path = {"", "/"})
public class HomeController extends BaseHomeController {

  private static final transient Logger LOG = LogManager.getLogger(HomeController.class);

  @PostConstruct
  public void init() {
    LOG.debug("Init started");
    super.init();
    LOG.debug("Init finished");
  }

  /**
   * Handler for front-end UI homepage
   *
   * @param model       {@link Model}
   * @param httpRequest {@link HttpServletRequest}
   *
   * @return redirect to front-end UI homepage
   */
  @Override
  @GetMapping(path = {"", "/"})
  public String index(Model model, HttpServletRequest httpRequest) {
    LOG.debug("{} /:", httpRequest::getMethod);
    return "redirect:/index.html";
  }

}
