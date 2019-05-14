package com.zeno.controller;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import com.zeno.service.UserServ;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户注册登录", tags = {"注册登录controller"})
public class RegistLoginController extends BasicController{
	
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
		
//		String uniqueToken = UUID.randomUUID().toString();
//		redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000*60*30);
//		
//		UsersVO usersVO = new UsersVO();
//		BeanUtils.copyProperties(user, usersVO);
//		usersVO.setUserToken(uniqueToken);
		
		UsersVO usersVO = setRedisSessionToken(user);
		
		return IMoocJSONResult.ok(usersVO);
	}
	
	//设置redis session
	public UsersVO setRedisSessionToken(Users user) {
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000*60*30);
		
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(user, usersVO);
		usersVO.setUserToken(uniqueToken);
		return usersVO;
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
			UsersVO usersVO = setRedisSessionToken(userResult);
			return IMoocJSONResult.ok(usersVO);
		} else {
			return IMoocJSONResult.errorMsg("用户名或密码不正确");
		}
	}
	
	@ApiOperation(value = "注销", notes = "注销接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, 
						dataType = "String", paramType = "query")
	@PostMapping("/logout")
	public IMoocJSONResult logout(String userId) {
		redis.del(USER_REDIS_SESSION + ":" +userId); 
		
		return null;
	}
	
}
