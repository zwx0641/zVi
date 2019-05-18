package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
	
	public void addGetLikeCount(String userId);
	
	public void reduceGetLikeCount(String userId);
	
	public void addFansCount(String userId);
	
	public void reduceFansCount(String userId);
	
	public void addFollowersCount(String userId);
	
	public void reduceFollowersCount(String userId);
}