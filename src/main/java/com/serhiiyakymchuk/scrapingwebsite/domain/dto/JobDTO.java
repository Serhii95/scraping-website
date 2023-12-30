package com.serhiiyakymchuk.scrapingwebsite.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobDTO {
	private String jobUrl;
	private String positionName;
	private String logoUrl;
	private String organizationTitle;
	private String location;
	private String tags;
}
