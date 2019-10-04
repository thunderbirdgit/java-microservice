package com.openease.service.email.mvc;

import com.openease.common.web.mvc.home.controller.BaseHomeController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;

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

}
