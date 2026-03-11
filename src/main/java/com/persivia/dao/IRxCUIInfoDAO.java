package com.persivia.dao;

import java.util.List;

import com.persivia.bean.RxCUIInfoBean;

public interface IRxCUIInfoDAO {

	void saveAll(List<RxCUIInfoBean> rxcuiInfoList);

	boolean deleteByRxcui(String rxcui);
}
