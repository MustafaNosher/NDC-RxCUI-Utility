package com.ndc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ndc.dao.IHierarchyDrugInfoDAO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class HierarchyDrugInfoDAO implements IHierarchyDrugInfoDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public boolean insertHierarchyDrugInfo() {
		String sql = "WITH Hierarchy AS ("
				+ "    SELECT ri1.classId AS Level1_ID, ri1.className AS Level1_Name, NULL AS Level2_ID, NULL AS Level2_Name, "
				+ "           NULL AS Level3_ID, NULL AS Level3_Name, NULL AS Level4_ID, NULL AS Level4_Name "
				+ "    FROM RxClassInfo ri1 " + "    WHERE LEN(ri1.classId) = 1 " + "    UNION ALL "
				+ "    SELECT LEFT(ri2.classId, 1) AS Level1_ID, h1.className AS Level1_Name, "
				+ "           ri2.classId AS Level2_ID, ri2.className AS Level2_Name, NULL AS Level3_ID, NULL AS Level3_Name, "
				+ "           NULL AS Level4_ID, NULL AS Level4_Name " + "    FROM RxClassInfo ri2 "
				+ "    JOIN RxClassInfo h1 ON LEFT(ri2.classId, 1) = h1.classId " + "    WHERE LEN(ri2.classId) = 3 "
				+ "    UNION ALL " + "    SELECT LEFT(ri3.classId, 1) AS Level1_ID, h1.className AS Level1_Name, "
				+ "           LEFT(ri3.classId, 3) AS Level2_ID, h2.className AS Level2_Name, "
				+ "           ri3.classId AS Level3_ID, ri3.className AS Level3_Name, "
				+ "           NULL AS Level4_ID, NULL AS Level4_Name " + "    FROM RxClassInfo ri3 "
				+ "    JOIN RxClassInfo h1 ON LEFT(ri3.classId, 1) = h1.classId "
				+ "    JOIN RxClassInfo h2 ON LEFT(ri3.classId, 3) = h2.classId " + "    WHERE LEN(ri3.classId) = 4 "
				+ "    UNION ALL " + "    SELECT LEFT(ri4.classId, 1) AS Level1_ID, h1.className AS Level1_Name, "
				+ "           LEFT(ri4.classId, 3) AS Level2_ID, h2.className AS Level2_Name, "
				+ "           LEFT(ri4.classId, 4) AS Level3_ID, h3.className AS Level3_Name, "
				+ "           ri4.classId AS Level4_ID, ri4.className AS Level4_Name " + "    FROM RxClassInfo ri4 "
				+ "    JOIN RxClassInfo h1 ON LEFT(ri4.classId, 1) = h1.classId "
				+ "    JOIN RxClassInfo h2 ON LEFT(ri4.classId, 3) = h2.classId "
				+ "    JOIN RxClassInfo h3 ON LEFT(ri4.classId, 4) = h3.classId " + "    WHERE LEN(ri4.classId) = 5 "
				+ ") "
				+ "INSERT INTO HierarchyDrugInfo (Level1_ID, Level1_Name, Level2_ID, Level2_Name, Level3_ID, Level3_Name, Level4_ID, Level4_Name, RXCUI, DrugName) "
				+ "SELECT DISTINCT h.Level1_ID, h.Level1_Name, h.Level2_ID, h.Level2_Name, h.Level3_ID, h.Level3_Name, h.Level4_ID, h.Level4_Name, "
				+ "                ri.rxcui AS RXCUI, ri.name AS DrugName " + "FROM RxcuiInfo ri "
				+ "LEFT JOIN Hierarchy h ON ri.classId = h.Level4_ID " + "ORDER BY h.Level1_ID;";

		try {
			jdbcTemplate.update(sql);
			log.info("Data inserted successfully In HierarchyDrugInfo Table .");
			return true;
		} catch (Exception e) {
			log.info("Data insertion failed In HierarchyDrugInfo. Transaction rolled back.");
			return false;
		}
	}
}
