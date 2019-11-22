package com.zeno.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;

public interface UserServ {
	//用户是否存在
	public boolean UsernameIsExist(String username);
	
	//保存信息
	public void saveInfo(Users user);

	//对应用户名及密码
	public Users queryUserForLogin(String username, String password);
	
	//修改信息
	public void updateUserInfo(Users user);
	
	//
	public Users queryUsersInfo(String userId);
	
	//
	public boolean isUserLikeVideo(String userId, String videoId);
	
	//用户粉丝关系
	public void saveUserFanRelation(String userId, String fanId);
	public void deleteUserFanRelation(String userId, String fanId);
	
	//查询是否关注
	public boolean queryIfFollow(String userId, String fanId);
	
	public void reportUser(UsersReport usersReport);
	
	
}
