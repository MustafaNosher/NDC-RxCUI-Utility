package com.ndc.dao;

import java.util.List;

import com.ndc.bean.RxClassInfoBean;

public interface IRxClassInfoDAO {

	boolean saveAll(List<RxClassInfoBean> rxClassInfoList);
}
