package io.renren.service.impl;

import io.renren.dao.SysUserDao;
import io.renren.entity.SysUserEntity;
import io.renren.service.SysRoleService;
import io.renren.service.SysUserRoleService;
import io.renren.service.SysUserService;
import io.renren.utils.Constant;
import io.renren.utils.RRException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * 系统用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:46:09
 */
@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService {
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysRoleService sysRoleService;

	@Override
	public List<String> queryAllPerms(Long userId) {
		return sysUserDao.queryAllPerms(userId);
	}

	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return sysUserDao.queryAllMenuId(userId);
	}

	@Override
	public SysUserEntity queryByUserName(String username) {
		return sysUserDao.queryByUserName(username);
	}
	
	@Override
	public SysUserEntity queryObject(Long userId) {
		return sysUserDao.queryObject(userId);
	}

	@Override
	public List<SysUserEntity> queryList(Map<String, Object> map){
		return sysUserDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysUserDao.queryTotal(map);
	}

	@Override
	@Transactional
	public void save(SysUserEntity user) {
		user.setCreateTime(new Date());
		//sha256加密
		user.setPassword(new Sha256Hash(user.getPassword()).toHex());
		sysUserDao.save(user);
		
		//检查角色是否越权
		checkRole(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional
	public void update(SysUserEntity user) {
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			String oldPwd = sysUserDao.queryObject(user.getUserId()).getPassword();
			if(!user.getPassword().equals(oldPwd)){
				user.setPassword(new Sha256Hash(user.getPassword()).toHex());
			}
		}
		sysUserDao.update(user);
		
		//检查角色是否越权
		checkRole(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional
	public void deleteBatch(Long[] userId) {
		sysUserDao.deleteBatch(userId);
	}

	@Override
	public int updatePassword(Long userId, String password, String newPassword) {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("password", password);
		map.put("newPassword", newPassword);
		return sysUserDao.updatePassword(map);
	}
	
	/**
	 * 检查角色是否越权
	 */
	private void checkRole(SysUserEntity user){
		//如果不是超级管理员，则需要判断用户的角色是否自己创建
		if(user.getCreateUserId() == Constant.SUPER_ADMIN){
			return ;
		}
		
		//查询用户创建的角色列表
		List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());
		
		//判断是否越权
		if(!roleIdList.containsAll(user.getRoleIdList())){
			throw new RRException("新增用户所选角色，不是本人创建");
		}
	}
/**
 * 查询用户的可视用户范围
 */
	@Override
	public List<SysUserEntity> queryByAgencyId(String username) {
		List <SysUserEntity> list ;
		List <SysUserEntity> userlist;
		list=new ArrayList<SysUserEntity>();
		userlist = this.queryUserList(username);
		list.addAll(userlist);
		list.add(sysUserDao.queryByUserName(username));
		if(userlist.size()!=0&&userlist!=null){
			for(SysUserEntity u :userlist){
			List<SysUserEntity> ulist = this.queryByAgencyId(u.getUsername());
			list.addAll(ulist);
			}
			for (int i = 0; i < list.size() - 1; i++) {
				for (int j = list.size() - 1; j > i; j--) {
					if (list.get(j).getAgencyId() == list.get(i).getAgencyId()) {
						list.remove(j);
					}
				}
			}
			return list;
		}else{
			return list;
		}
	}
		
	public  List<SysUserEntity> queryUserList(String username){
		SysUserEntity user = sysUserDao.queryByUserName(username);
		List<SysUserEntity> userList = sysUserDao.queryByParentId(user.getAgencyId());
		return userList;
	}
		

}
