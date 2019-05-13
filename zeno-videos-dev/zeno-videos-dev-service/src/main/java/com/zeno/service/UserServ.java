package com.zeno.service;

import com.imooc.pojo.Users;

public interface UserServ {
	//用户是否存在
	public boolean UsernameIsExist(String username);
	
	//保存信息
	public void saveInfo(Users user);
}
