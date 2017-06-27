package com.power.service;

import com.power.entity.ChargeModelEntity;

import java.util.List;
import java.util.Map;

/**
 * 收费模式
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-24 16:01:17
 */
public interface ChargeModelService {
	
	ChargeModelEntity queryObject(Long id);
	
	List<ChargeModelEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(ChargeModelEntity chargeModel);
	
	void update(ChargeModelEntity chargeModel);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
}