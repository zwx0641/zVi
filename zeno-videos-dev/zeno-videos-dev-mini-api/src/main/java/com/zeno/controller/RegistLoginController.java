package com.zeno.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.imooc.pojo.Users;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import com.zeno.service.UserServ;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户注册登录", tags = {"注册登录controller"})
public class RegistLoginController {
	
	@Autowired
	private UserServ userServ;
	
	@ApiOperation(value = "注册", notes = "注册接口")
	@PostMapping("/regist")
	public IMoocJSONResult regist(@RequestBody Users user) throws Exception {
		
		
		//1  判断用户名密码不为空
		if (StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getUsername())) {
			return IMoocJSONResult.errorMsg("用户名或密码不能为空");
		}
		
		//2 判断用户名是否存在
		boolean usernameExist = userServ.UsernameIsExist(user.getUsername());
		
		
		//3 保存用户注册信息
		if (!usernameExist) {
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			user.setFansCounts(0);
			user.setFollowCounts(0);
			user.setReceiveLikeCounts(0);
			
			userServ.saveInfo(user);
		} else {
			IMoocJSONResult.errorMsg("此用户名已被占用");
		}
		user.setPassword(null);
		return IMoocJSONResult.ok(user);
	}
	
	@ApiOperation(value = "登录", notes = "登录接口")
	@PostMapping("/login")
	public IMoocJSONResult login(@RequestBody Users user) throws Exception {
		String username = user.getUsername();
		String password = user.getPassword();
		
//		Thread.sleep(3000);
		
		// 1. 判断用户名和密码必须不为空
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return IMoocJSONResult.ok("用户名或密码不能为空");
		}
		
		// 2. 判断用户是否存在
		Users userResult = userServ.queryUserForLogin(username, MD5Utils.getMD5Str(user.getPassword()));
		
		// 3. 返回
		if (userResult != null) {
			userResult.setPassword("");
			return IMoocJSONResult.ok(userResult);
		} else {
			return IMoocJSONResult.errorMsg("用户名或密码不正确");
		}
	}
	
}
