package com.power.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.power.service.ex.DictService;
import io.renren.utils.R;

/**
 * 字典查询
 * 
 * @author hwx
 * @date 2017-06-24 11:49:16
 */
@RestController
@RequestMapping("dict")
public class DictController {
	
	@Autowired
	private DictService dictService;
	
	/**
	 * 查询字典
	 */
	@RequestMapping("/{type}")
	public R listbytype(@PathVariable("type") String type){
		List<?> dictCommonList = dictService.queryListByType(type);
		return R.ok().put("data", dictCommonList);
	}
	
	/**
	 * 字典-充电桩型号
	 */
	@RequestMapping("/queryStationModel")
	public R queryStationModel(){
		List<?> models = dictService.queryStationModel();
		return R.ok().put("data", models);
	}
	
	/**
	 * 字典-充电宝型号
	 */
	@RequestMapping("/queryPowerModel")
	public R queryPowerModel(){
		List<?> models = dictService.queryPowerModel();
		return R.ok().put("data", models);
	}
}
