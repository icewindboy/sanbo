package com.sanbo.erp.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

	@RequestMapping(value = {"/" ,"/index.html" }, method = RequestMethod.GET)
	public ModelAndView index() {

		ModelAndView modelAndView = new ModelAndView("/welcome.ftl");
		modelAndView.addObject("welcome", " freemarker");

		modelAndView.addObject("CREATE_HTML", false); 
		
		return modelAndView;
	}
}
