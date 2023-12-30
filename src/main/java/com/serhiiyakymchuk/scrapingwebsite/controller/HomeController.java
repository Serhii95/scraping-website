package com.serhiiyakymchuk.scrapingwebsite.controller;

import com.serhiiyakymchuk.scrapingwebsite.domain.service.ScrapingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final ScrapingService scrapingService;

	public HomeController(ScrapingService scrapingService) {
		this.scrapingService = scrapingService;
	}

	@GetMapping({"", "/"})
	public String home(Model model) {
		model.addAttribute("jobFunctions", scrapingService.getJobFunctions());

		return "home/home";
	}

}
