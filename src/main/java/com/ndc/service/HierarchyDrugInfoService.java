package com.ndc.service;

import org.springframework.stereotype.Service;

import com.ndc.dao.impl.HierarchyDrugInfoDAO;

@Service
public class HierarchyDrugInfoService {

	private final HierarchyDrugInfoDAO hierarchyDrugInfoDAO;

	public HierarchyDrugInfoService(HierarchyDrugInfoDAO hierarchyDrugInfoDAO) {
		this.hierarchyDrugInfoDAO = hierarchyDrugInfoDAO;
	}

	public boolean insertHierarchyDrugInfo() {
		return hierarchyDrugInfoDAO.insertHierarchyDrugInfo();
	}
}
