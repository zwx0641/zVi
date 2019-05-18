package com.zeno.service.impl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.imooc.mapper.UsersFansMapper;
import com.imooc.mapper.UsersLikeVideosMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersFans;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.utils.IMoocJSONResult;
import com.zeno.service.UserServ;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServImple implements UserServ {
	
	@Autowired
	private UsersMapper userMapper;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideoMapper;
	
	@Autowired
	private UsersFansMapper usersFansMapper;
	
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

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateUserInfo(Users user) {
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", user.getId());
		userMapper.updateByExampleSelective(user, userExample);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUsersInfo(String userId) {
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", userId);
		Users user = userMapper.selectOneByExample(userExample);
		
		return user;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean isUserLikeVideo(String userId, String videoId) {
		
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(videoId)) {
			return false;
		}
		
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		
		List<UsersLikeVideos> list = usersLikeVideoMapper.selectByExample(example);
		
		if(list != null && list.size() > 0) {
			return true;
		}
		System.out.print(list);
		
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUserFanRelation(String userId, String fanId) {
		String relId = sid.nextShort();
		
		
		UsersFans usersFans = new UsersFans();
		usersFans.setId(relId);
		usersFans.setUserId(userId);
		usersFans.setFanId(fanId);
		
		usersFansMapper.insert(usersFans);
		
		userMapper.addFansCount(userId);
		userMapper.addFollowersCount(fanId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteUserFanRelation(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		
		usersFansMapper.deleteByExample(example);
		userMapper.reduceFansCount(userId);
		userMapper.reduceFollowersCount(fanId);
	}


	@Override
	public boolean queryIfFollow(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		
		List<UsersFans> list = usersFansMapper.selectByExample(example);
		
		if(list != null && !list.isEmpty() && list.size() > 0) {
			return true;
		}
		
		return false;
	}
}
