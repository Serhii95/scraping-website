package com.serhiiyakymchuk.scrapingwebsite.controller;

import com.serhiiyakymchuk.scrapingwebsite.domain.dto.JobDTO;
import com.serhiiyakymchuk.scrapingwebsite.domain.service.ScrapingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

	private final ScrapingService scrapingService;

	public SearchController(ScrapingService scrapingService) {
		this.scrapingService = scrapingService;
	}

	@GetMapping("/search")
	public List<JobDTO> search(@RequestParam String query, @RequestParam List<String> jobFunctions) {
		return scrapingService.search(query, jobFunctions );
	}

}
