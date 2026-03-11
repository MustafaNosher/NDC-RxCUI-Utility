package com.persivia.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persivia.bean.RxClassInfoBean;
import com.persivia.dao.impl.RxClassInfoDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RxClassService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RxClassInfoDAO rxClassDao;

	@Value("${RxClass.api.url}")
	private String rxClassesApiUrl;

	private List<RxClassInfoBean> getRxClassInfo() {
		List<RxClassInfoBean> rxClassInfoList = new ArrayList<>();
		try {
			// Fetch data from API
			String jsonResponse = restTemplate.getForObject(rxClassesApiUrl, String.class);

			// Parse JSON response
			JsonNode rootNode = objectMapper.readTree(jsonResponse);
			JsonNode rxClassTreeNode = rootNode.path("rxclassTree");

			// Extract classId and className recursively
			extractClassInfo(rxClassTreeNode, rxClassInfoList);

		} catch (Exception e) {
			log.info("Error Fetching RxClassInfo FROM Provided API:");
			log.error(e.toString());
		}
		return rxClassInfoList;
	}

	private void extractClassInfo(JsonNode treeNode, List<RxClassInfoBean> rxClassInfoList) {
		if (treeNode.isArray()) {
			for (JsonNode node : treeNode) {
				JsonNode minConceptItem = node.path("rxclassMinConceptItem");
				String classId = minConceptItem.path("classId").asText();
				String className = minConceptItem.path("className").asText();

				if (!classId.isEmpty() && !className.isEmpty()) {
					rxClassInfoList.add(new RxClassInfoBean(classId, className));
				}

				// Recursively process nested rxclassTree nodes
				JsonNode nestedTree = node.path("rxclassTree");
				if (nestedTree.isArray()) {
					extractClassInfo(nestedTree, rxClassInfoList);
				}
			}
		}
	}

	public boolean fetchAndstoreRxClass() {
		List<RxClassInfoBean> rxClassInfoList = getRxClassInfo();
		boolean response = rxClassDao.saveAll(rxClassInfoList);
		return response;

	}

}
