package com.persivia.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persivia.bean.NDCRxCUIMapping;
import com.persivia.bean.RxCUIInfoBean;
import com.persivia.dao.impl.NDCRxCUIMappingDAO;
import com.persivia.dao.impl.RxCUIInfoDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RxCUIService {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private NDCRxCUIMappingDAO ndcRxcuiDAO;

	@Autowired
	private RxCUIInfoDAO rxcuiDAO;

	@Value("${RxCUI.api.url}")
	private String rxCUIApiUrl;

	// Few Records/Results from API have very large data which required to get
	// TRIMMED before saving it
	private static final int MAX_CLASS_ID_LENGTH = 255;
	private static final int MAX_CLASS_NAME_LENGTH = 1000;
	private static final int MAX_RXCUI_LENGTH = 255;
	private static final int MAX_NAME_LENGTH = 1000;
	private static final int BATCH_SIZE = 1000;

	// This function is the main fucntion and rest of the functions act as an
	// utility to this function
	public boolean processAllMappings() throws IOException, InterruptedException, ExecutionException {
		String lastNDC = "";
		Set<String> processedRxcuiSet = new HashSet<>();

		List<NDCRxCUIMapping> batchList;
		try {
			do {
//			batchList = ndcRxcuiDAO.findBatch(offset, BATCH_SIZE);// Applies Pagination with a certain batch size on

				// RXCui
				batchList = ndcRxcuiDAO.findBatch(lastNDC, BATCH_SIZE);

//			if (!batchList.isEmpty()) {
//				log.info("Size of Data/BatchList Fetched from Data Base:" + Integer.toString(batchList.size()));
//				String firstNDC = batchList.get(0).getNdcCode();
//				lastNDC = batchList.get(batchList.size() - 1).getNdcCode();
//				log.info("First NDC Code in this Batch: " + firstNDC);
//				log.info("Last NDC in this Batch: " + lastNDC + "\n");
//				List<String> NDCList = batchList.stream().map(NDCRxCUIMapping::getNdcCode).collect(Collectors.toList());
//				log.info("Size of NDCList:" + Integer.toString(NDCList.size()));
//				ndcRxcuiDAO.updateProcessedStatus(NDCList);
//			} else {
//				log.info("BatchList Completely Processed!");
//			}
				if (!batchList.isEmpty()) {

					List<RxCUIInfoBean> rxcuiInfoList = fetchDrugInfoByRxcuiInBatches(processedRxcuiSet, batchList, 20);
					saveAllData(rxcuiInfoList);
					List<String> ndcList = batchList.stream().map(NDCRxCUIMapping::getNdcCode)
							.collect(Collectors.toList());
					ndcRxcuiDAO.updateProcessedStatus(ndcList);
				}
			} while (!batchList.isEmpty());
			return true;
		} catch (Exception e) {
			log.error("Error occurred during processing RxCUI Data: ", e);
			return false;
		}
	}

	private List<RxCUIInfoBean> fetchDrugInfoByRxcuiInBatches(Set<String> processedRxcuiSet,
			List<NDCRxCUIMapping> ndcRxcuiMappings, int batchSize) throws InterruptedException, ExecutionException {
		List<RxCUIInfoBean> allRxcuiInfoList = new ArrayList<>();
//		Set<String> processedRxcuiSet = new HashSet<>();
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		List<Future<List<RxCUIInfoBean>>> futures = new ArrayList<>();

		int totalBatches = (int) Math.ceil((double) ndcRxcuiMappings.size() / batchSize);

		for (int batchNumber = 0; batchNumber < totalBatches; batchNumber++) {
			int start = batchNumber * batchSize;
			int end = Math.min(start + batchSize, ndcRxcuiMappings.size());
			List<NDCRxCUIMapping> batchList = ndcRxcuiMappings.subList(start, end);

			Callable<List<RxCUIInfoBean>> task = () -> fetchDrugInfoByRxcuiBatch(batchList, processedRxcuiSet);
			futures.add(executorService.submit(task));
		}

		for (Future<List<RxCUIInfoBean>> future : futures) {
			allRxcuiInfoList.addAll(future.get());
		}

		executorService.shutdown();
		return allRxcuiInfoList;
	}

	private List<RxCUIInfoBean> fetchDrugInfoByRxcuiBatch(List<NDCRxCUIMapping> batchList,
			Set<String> processedRxcuiSet) throws IOException {
		List<RxCUIInfoBean> rxcuiInfoList = new ArrayList<>();
		for (NDCRxCUIMapping mapping : batchList) {
			String rxcui = mapping.getRxcui().replace(".0", "");

			if (processedRxcuiSet.contains(rxcui)) {
				continue;
			}

			String apiUrl = String.format(rxCUIApiUrl, rxcui);
			String response = restTemplate.getForObject(apiUrl, String.class);
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode rxclassDrugInfoList = rootNode.path("rxclassDrugInfoList").path("rxclassDrugInfo");

			if (!rxclassDrugInfoList.isArray() || rxclassDrugInfoList.size() == 0) {
				RxCUIInfoBean placeholderInfo = new RxCUIInfoBean();
				placeholderInfo.setClassId(truncateValue("N/A", MAX_CLASS_ID_LENGTH));
				placeholderInfo.setClassName(truncateValue("N/A", MAX_CLASS_NAME_LENGTH));
				placeholderInfo.setRxcui(truncateValue(rxcui, MAX_RXCUI_LENGTH));
				placeholderInfo.setName(truncateValue("N/A", MAX_NAME_LENGTH));
				rxcuiInfoList.add(placeholderInfo);
				processedRxcuiSet.add(rxcui);
				continue;
			}

			for (JsonNode rxcuiInfo : rxclassDrugInfoList) {
				String classId = truncateValue(rxcuiInfo.path("rxclassMinConceptItem").path("classId").asText(),
						MAX_CLASS_ID_LENGTH);
				String className = truncateValue(rxcuiInfo.path("rxclassMinConceptItem").path("className").asText(),
						MAX_CLASS_NAME_LENGTH);
				String rxcuiValue = truncateValue(rxcuiInfo.path("minConcept").path("rxcui").asText(),
						MAX_RXCUI_LENGTH);
				String name = truncateValue(rxcuiInfo.path("minConcept").path("name").asText(), MAX_NAME_LENGTH);

				RxCUIInfoBean drugInfo = new RxCUIInfoBean();
				drugInfo.setClassId(classId);
				drugInfo.setClassName(className);
				drugInfo.setRxcui(rxcuiValue);
				drugInfo.setName(name);
				rxcuiInfoList.add(drugInfo);
			}

			processedRxcuiSet.add(rxcui);
		}

		return rxcuiInfoList;
	}

	private String truncateValue(String value, int maxLength) {
		if (value != null && value.length() > maxLength) {
			return value.substring(0, maxLength);
		}
		return value;
	}

//	public List<NDCRxCUIMapping> fetchAllNdcRxcuiMappings() {
//		return ndcRxcuiDAO.findAll();
//	}

	private void saveAllData(List<RxCUIInfoBean> rxcuiInfo) throws IOException {
		rxcuiDAO.saveAll(rxcuiInfo);
	}

	public boolean deleteRxcui(String rxCui) {

		return rxcuiDAO.deleteByRxcui(rxCui);
	}

}
