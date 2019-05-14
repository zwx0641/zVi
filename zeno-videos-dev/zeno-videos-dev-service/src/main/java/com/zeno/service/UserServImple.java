package com.zeno.service;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.utils.MD5Utils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServImple implements UserServ {
	
	@Autowired
	private UsersMapper userMapper;
	
	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean UsernameIsExist(String username) {
		// TODO Auto-generated method stub
		Users user = new Users();
		user.setUsername(username);
		
		Users resultUsers = userMapper.selectOne(user);
		
		
		return resultUsers == null ? false : true;
	} 

	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveInfo(Users user) {
		String sidString = sid.nextShort();
		user.setId(sidString);
		userMapper.insert(user);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLogin(String username, String password) {
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("username", username);
		criteria.andEqualTo("password", password);
		Users result = userMapper.selectOneByExample(userExample);
		
		return result;
	}

}
