package com.ndc.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndc.service.HierarchyDrugInfoService;
import com.ndc.service.RxCUIService;
import com.ndc.service.RxClassService;

import lombok.extern.slf4j.Slf4j;

//NOTE: THIS IS A UTILITY HENCE THIS FILE IS NOT BEING USED. ALL THE REQUIRED FUNCTIONS ARE CALLED DIRECTLY IN MAIN CLASS
@Slf4j
@RestController
@RequestMapping("/api")
public class DrugController {

	@Autowired
	private RxCUIService rxcuiService;

	@Autowired
	private RxClassService rxClassService;

	@Autowired
	private HierarchyDrugInfoService HierarchyDrugInfoService;

	// API CALL TO CHECK THE COMPLETE UTILITY

	@GetMapping("/ndcutility")
	public String NDCUtility() throws IOException, InterruptedException, ExecutionException {

		long startTime = System.currentTimeMillis();

		boolean response1 = rxClassService.fetchAndstoreRxClass();

		boolean response2 = rxcuiService.processAllMappings();

		boolean response3 = HierarchyDrugInfoService.insertHierarchyDrugInfo();

		long endTime = System.currentTimeMillis();

		long duration = endTime - startTime;

		double durationSeconds = Math.round(duration / 1000.0);

		if (!response1 && !response2 && !response3) {
			return "NDC UTILITY HAS FAILEd TO EXECUTE DUE TO SOME INTERNAL ERROR!!";
		}
		if (durationSeconds > 0) {
			return "NDC UTILITY HAS BEEN EXECUTED SUCCESSFULY!! " + "| Execution time: " + durationSeconds + " seconds";
		} else {
			return "NDC UTILITY HAS BEEN EXECUTED SUCCESSFULY!! " + "| Execution time: " + duration + " Milliseconds";
		}
	}

	// CALL TO DELETE A SINGLE RXCUI RELATED DATA FROM DATABASE

	@GetMapping("/delete")
	public String delete(@RequestParam String rxcui) {
		if (rxcui == null || rxcui.isEmpty()) {
			return "RxCUI value is required.";
		}

		boolean response = rxcuiService.deleteRxcui(rxcui);
		if (response) {
			return "Data against the provided:" + rxcui + " Deleted Successfuly!";
		} else {
			return "Data against the provided:" + rxcui + " does not Exists!";
		}
	}

//-------------------------------------------------------------------------------------------------------------------

	@GetMapping("/check")
	String generalTest() {
		return "TESTING";
	}

}
