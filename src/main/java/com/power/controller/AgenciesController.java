package com.power.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.power.entity.AgenciesEntity;
import com.power.service.AgenciesService;
import io.renren.utils.PageUtils;
import io.renren.utils.Query;
import io.renren.utils.R;


/**
 * 
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-06-21 23:02:38
 */
@RestController
@RequestMapping("agencies")
public class AgenciesController {
	@Autowired
	private AgenciesService agenciesService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("agencies:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<AgenciesEntity> agenciesList = agenciesService.queryList(query);
		int total = agenciesService.queryTotal(query);
		
		PageUtils pageUtil = new PageUtils(agenciesList, total, query.getLimit(), query.getPage());
		
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("agencies:info")
	public R info(@PathVariable("id") Long id){
		AgenciesEntity agencies = agenciesService.queryObject(id);
		
		return R.ok().put("agencies", agencies);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("agencies:save")
	public R save(@RequestBody AgenciesEntity agencies){
		agenciesService.save(agencies);
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("agencies:update")
	public R update(@RequestBody AgenciesEntity agencies){
		agenciesService.update(agencies);
		
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("agencies:delete")
	public R delete(@RequestBody Long[] ids){
		agenciesService.deleteBatch(ids);
		
		return R.ok();
	}
	
}