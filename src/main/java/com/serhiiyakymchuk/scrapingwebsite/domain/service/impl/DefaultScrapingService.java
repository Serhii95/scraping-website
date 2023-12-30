package com.serhiiyakymchuk.scrapingwebsite.domain.service.impl;

import com.serhiiyakymchuk.scrapingwebsite.domain.dto.JobDTO;
import com.serhiiyakymchuk.scrapingwebsite.domain.service.ScrapingService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultScrapingService implements ScrapingService {

	@Value("${parse.website.techstarsjobs}")
	private String techstarsjobsWebsiteURL;

	private final String DATA_NOT_FOUND = "NOT_FOUND";

	@Override
	public List<String> getJobFunctions() {
		final WebDriver driver = new ChromeDriver();
		final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));

		try {
			driver.get(techstarsjobsWebsiteURL);
			closeCookiesPopup(wait);
			wait.until(webDriver -> webDriver.findElement(By.xpath("//button[ancestor::div[@data-testid='filter-option-item-0']]"))).click();

			return driver
					.findElements(By.xpath("//button[ancestor::div[@data-testid='dropdown-content'] and ancestor::div[@role='listitem']]"))
					.stream()
					.map(WebElement::getText)
					.toList();

		} catch (Exception e) {
			return null;
		} finally {
			driver.close();
		}
	}

	@Override
	public List<JobDTO> search(String query, List<String> jobFunctions) {
		final WebDriver driver = new ChromeDriver();
		final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		final Actions actions = new Actions(driver);

		final ArrayList<JobDTO> jobsDTO = new ArrayList<>();
		try {
			driver.get(techstarsjobsWebsiteURL);
			closeCookiesPopup(wait);

			if (query != null && !query.isEmpty()) {
				wait.until(webDriver -> webDriver.findElement(By.cssSelector("input[data-testid='search-input']"))).sendKeys(query);
			}

			if (!jobFunctions.isEmpty()) {
				for (String jobFunction : jobFunctions) {
					wait.until(webDriver -> webDriver.findElement(By.xpath("//button[ancestor::div[@data-testid='filter-option-item-0']]"))).click();
					wait.until(webDriver -> webDriver.findElement(By.xpath("//button[ancestor::div[@data-testid='dropdown-content'] and ancestor::div[@role='listitem'] and text()='" + jobFunction + "']"))).click();
				}
			}

			WebElement loadMoreBtn = wait.until(webDriver -> webDriver.findElement(By.xpath("//button[@data-testid='load-more']")));
			actions.scrollToElement(loadMoreBtn).moveToElement(loadMoreBtn).click(loadMoreBtn).perform();

			WebElement body = wait.until(webDriver -> webDriver.findElement(By.xpath("//div[@data-testid='body']")));
			int initialHeight = body.getSize().getHeight();
			while (true) {
				final WebElement footerLogo = wait.until(webDriver -> webDriver.findElement(By.xpath("//a[@data-testid='footer-getro-logo']")));
				actions.scrollToElement(footerLogo).perform();

				body = wait.until(webDriver -> webDriver.findElement(By.xpath("//div[@data-testid='body']")));
				final int currentHeight = body.getSize().getHeight();
				if (currentHeight == initialHeight) {
					break;
				}

				initialHeight = currentHeight;
			}

			final List<WebElement> jobCards = wait.until(webDriver -> webDriver.findElements(By.xpath("//div[@data-testid='job-list-item']")));
			for (WebElement jobCard : jobCards) {
				try {
					final String jobUrl = jobCard.findElements(By.cssSelector("a[data-testid='read-more']")).stream().map(webElement -> webElement.getAttribute("href")).findFirst().orElse(DATA_NOT_FOUND);
					final String positionName = jobCard.findElements(By.cssSelector("div.kToBwF")).stream().map(WebElement::getText).findFirst().orElse(DATA_NOT_FOUND);
					final String logoUrl = jobCard.findElements(By.cssSelector("img[data-testid='image']")).stream().findFirst().map(element -> element.getAttribute("src")).orElse(DATA_NOT_FOUND);
					final String organizationTitle = jobCard.findElements(By.cssSelector("a[data-testid='link']")).stream().findFirst().map(WebElement::getText).orElse(DATA_NOT_FOUND);
					final String location = jobCard.findElements(By.cssSelector("meta[itemprop='address']")).stream().findFirst().map(webElement -> webElement.getAttribute("content")).orElse(DATA_NOT_FOUND);
					final String tags = jobCard.findElements(By.cssSelector("div[data-testid='tag']")).stream().map(webElement -> webElement.findElement(By.tagName("div")).getText()).collect(Collectors.joining(","));

					final JobDTO.JobDTOBuilder jobDTOBuilder = JobDTO.builder()
							.jobUrl(jobUrl)
							.positionName(positionName)
							.logoUrl(logoUrl)
							.organizationTitle(organizationTitle)
							.location(location)
							.tags(tags);

					jobsDTO.add(jobDTOBuilder.build());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			driver.close();
		}

		return jobsDTO;
	}

	private void closeCookiesPopup(WebDriverWait wait) {
		wait.until(webDriver -> webDriver.findElement(By.xpath("//button[ancestor::div[@id='onetrust-close-btn-container']]"))).click();
	}
}
