package com.persivia.dao;

import java.util.List;

import com.persivia.bean.RxClassInfoBean;

public interface IRxClassInfoDAO {

	boolean saveAll(List<RxClassInfoBean> rxClassInfoList);
}
