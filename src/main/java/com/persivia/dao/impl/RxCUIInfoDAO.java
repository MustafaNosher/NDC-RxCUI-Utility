package com.persivia.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.persivia.bean.RxCUIInfoBean;
import com.persivia.dao.IRxCUIInfoDAO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class RxCUIInfoDAO implements IRxCUIInfoDAO {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String INSERT_SQL = "INSERT INTO RxcuiInfo (classId, className, rxcui, name) VALUES (?, ?, ?, ?)";
	private static final String DELETE_RXCUI_INFO_SQL = "DELETE FROM RxcuiInfo WHERE rxcui = ?";
	private static final String DELETE_HIERARCHY_DRUG_INFO_SQL = "DELETE FROM HierarchyDrugInfo WHERE rxcui = ?";
	private static final String SELECT_RXCUI_SQL = "SELECT COUNT(*) FROM RxcuiInfo WHERE rxcui = ?";

	@Override
	public void saveAll(List<RxCUIInfoBean> rxcuiInfoList) {

		jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RxCUIInfoBean rxcuiInfo = rxcuiInfoList.get(i);
				ps.setString(1, rxcuiInfo.getClassId());
				ps.setString(2, rxcuiInfo.getClassName());
				ps.setString(3, rxcuiInfo.getRxcui());
				ps.setString(4, rxcuiInfo.getName());
			}

			@Override
			public int getBatchSize() {
				return rxcuiInfoList.size();
			}
		});
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean deleteByRxcui(String rxcui) {
		// Check if the rxcui exists in the database
		Integer count = jdbcTemplate.queryForObject(SELECT_RXCUI_SQL, new Object[] { rxcui }, Integer.class);

		if (count != null && count > 0) {
			// First delete from the child table
			jdbcTemplate.update(DELETE_HIERARCHY_DRUG_INFO_SQL, rxcui);
			// Then delete from the main table
			jdbcTemplate.update(DELETE_RXCUI_INFO_SQL, rxcui);
			log.info("RxCUI deleted successfully.");
			return true;
		} else {
			log.info("RxCUI not found in the database.");
			return false;
		}
	}
}
