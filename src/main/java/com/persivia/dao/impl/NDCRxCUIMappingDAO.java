package com.persivia.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.persivia.bean.NDCRxCUIMapping;
import com.persivia.dao.INDCRxCUIMappingDAO;

@Repository
public class NDCRxCUIMappingDAO implements INDCRxCUIMappingDAO {

	private final JdbcTemplate jdbcTemplate;

	public NDCRxCUIMappingDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// NEEDS TO BE REMOVED AT THE END FROM HERE AND DAO INTERFACE
//	@Override
//	public List<NDCRxCUIMapping> findAll() {
//		String sql = "SELECT * FROM NdcRxcuiMapping";
//		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(NDCRxCUIMapping.class));
//	}

//	@Override
//	public List<NDCRxCUIMapping> findBatch(int offset, int limit) {
//		String sql = "SELECT * FROM NdcRxcuiMapping WHERE processed = 0 ORDER BY NDCCode OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
//		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(NDCRxCUIMapping.class), offset, limit);
//	}
	@Override
	public List<NDCRxCUIMapping> findBatch(String lastNdcCode, int limit) {
		String sql = "SELECT * FROM NdcRxcuiMapping WHERE processed = 0 AND NDCCode > ? ORDER BY NDCCode OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(NDCRxCUIMapping.class), lastNdcCode, limit);
	}

	@Override
	public void updateProcessedStatus(List<String> NDCList) {
		String sql = "UPDATE NdcRxcuiMapping SET processed = 1 WHERE processed = 0 AND NDCCode IN ("
				+ NDCList.stream().map(r -> "?").collect(Collectors.joining(", ")) + ")";
		jdbcTemplate.update(sql, NDCList.toArray());

	}

}
