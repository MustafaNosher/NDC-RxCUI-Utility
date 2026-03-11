package com.ndc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndc.service.HierarchyDrugInfoService;
import com.ndc.service.RxCUIService;
import com.ndc.service.RxClassService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@Component
public class NDCCodesUtilityApplication implements CommandLineRunner {

	// Define the Object for the 3 required Services
	public static RxClassService rxClassService;
	public static RxCUIService rxcuiService;
	public static HierarchyDrugInfoService HierarchyDrugInfoService;

	// We need to define applicationContext since we are doing dynamic bean
	// management
	@Autowired
	ApplicationContextProvider applicationContextProvider;
	@Autowired
	ConfigurableApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(NDCCodesUtilityApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper;
	}

	@Override
	public void run(String... args) throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		log.info("Before Starting Application: {}", dtf.format(now));
		rxClassService = applicationContextProvider.getApplicationContext().getBean(RxClassService.class);

		long startTime = System.currentTimeMillis();

		boolean response1 = rxClassService.fetchAndstoreRxClass();
		if (response1) {
			log.info("RXCLASS SERVICE EXECUTED SUCCESSFULY!!");
		} else {
			log.info("ERRO IN EXECUTING RXCLASS SERVICE");
		}

		rxcuiService = applicationContextProvider.getApplicationContext().getBean(RxCUIService.class);
		boolean response2 = rxcuiService.processAllMappings();
		if (response2) {
			log.info("RXCUI SERVICE EXECUTED SUCCESSFULY!!");
		} else {
			log.info("ERRO IN EXECUTING RXCUI SERVICE");
		}

		HierarchyDrugInfoService = applicationContextProvider.getApplicationContext()
				.getBean(HierarchyDrugInfoService.class);
		boolean response3 = HierarchyDrugInfoService.insertHierarchyDrugInfo();
		if (response3) {
			log.info("HierarchyDrugInfoService SERVICE EXECUTED SUCCESSFULY!!");
		} else {
			log.info("ERRO IN EXECUTING HierarchyDrugInfoService SERVICE");
		}

		long endTime = System.currentTimeMillis();

		long duration = endTime - startTime;

		double durationSeconds = Math.round(duration / 1000.0);

		if (response1 && response2 && response3) {
			log.info("NDC CODES UTILITY HAS BEEN EXECUTED SUCCESSFULY! || Time Taken: " + durationSeconds + "seconds");
		} else {
			log.info("NDC CODES UTILITY HAS FAILED TO EXECUTE DUE TO SOME INTERNAL ERROR!!");
		}

		// Add shutdown hook to stop Tomcat server when application completes
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LocalDateTime shutdownTime = LocalDateTime.now();
			log.info("Shutting down Tomcat server at: {}", dtf.format(shutdownTime));
		}));

		// Gracefully exit the application and stop Tomcat
		SpringApplication.exit(applicationContext, () -> 0);
	}

}
