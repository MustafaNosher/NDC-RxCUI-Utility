package com.ndc.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ndc.bean.RxClassInfoBean;
import com.ndc.dao.IRxClassInfoDAO;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class RxClassInfoDAO implements IRxClassInfoDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String INSERT_SQL = "INSERT INTO RxClassInfo (classId, className) VALUES (?, ?)";

	@Override
	public boolean saveAll(List<RxClassInfoBean> rxClassInfoList) {
		try {
			jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					RxClassInfoBean rxClassInfo = rxClassInfoList.get(i);
					ps.setString(1, rxClassInfo.getClassId());
					ps.setString(2, rxClassInfo.getClassName());
				}

				@Override
				public int getBatchSize() {
					return rxClassInfoList.size();
				}
			});
			log.info("RxClass Info DataBase insert successful.");
			return true;
		} catch (Exception e) {
			log.info("Error saving list of RxClassInfo in DataBase: " + e.getMessage());
			return false;
		}
	}

}
