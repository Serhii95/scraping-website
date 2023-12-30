package com.serhiiyakymchuk.scrapingwebsite.domain.service;

import com.serhiiyakymchuk.scrapingwebsite.domain.dto.JobDTO;

import java.util.List;

public interface ScrapingService {

	List<String> getJobFunctions();

	List<JobDTO> search(String query, List<String> jobFunctions);
}
