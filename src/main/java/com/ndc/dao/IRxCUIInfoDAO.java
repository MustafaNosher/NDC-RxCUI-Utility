package com.ndc.dao;

import java.util.List;

import com.ndc.bean.RxCUIInfoBean;

public interface IRxCUIInfoDAO {

	void saveAll(List<RxCUIInfoBean> rxcuiInfoList);

	boolean deleteByRxcui(String rxcui);
}
