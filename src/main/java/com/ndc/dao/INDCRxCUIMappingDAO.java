package com.ndc.dao;

import java.util.List;

import com.ndc.bean.NDCRxCUIMapping;

public interface INDCRxCUIMappingDAO {

//	List<NDCRxCUIMapping> findAll();

//	List<NDCRxCUIMapping> findBatch(int offset, int limit);

	void updateProcessedStatus(List<String> rxcuiList);

	List<NDCRxCUIMapping> findBatch(String lastNdcCode, int limit);
}
