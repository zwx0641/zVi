package com.zeno.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import com.imooc.pojo.vo.PublisherVideo;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.utils.IMoocJSONResult;
import com.zeno.service.UserServ;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户相关业务接口", tags = {"用户相关业务controller"})
@RequestMapping("/user")
public class UserController extends BasicController{
	
	@Autowired
	private UserServ userServ;
	
	@ApiOperation(value = "上传头像", notes = "头像接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, 
						dataType = "String", paramType = "query")
	@PostMapping("/uploadFace")
	public IMoocJSONResult uploadFace(String userId, 
			@RequestParam("file") MultipartFile[] files) throws Exception {
		if(StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.errorMsg("用户id为空");
		}
		
		//上传的文件目录
		String fileSpace = "C:/workspace/zenoVi/zeno-videos-store";
		
		//数据库中相对路径
		String uploadPathDB = "/" + userId + "/face";
		
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if(files != null && files.length > 0) {
				
				
				String filename = files[0].getOriginalFilename();
				
				if (!StringUtils.isEmpty(filename)) {
					//绝对路径
					String finalFacePath = fileSpace + uploadPathDB + "/" + filename;
					//相对路径
					uploadPathDB += ("/" + filename);
					
					File outFile = new File(finalFacePath);
					if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}
			} else {
				return IMoocJSONResult.errorMsg("上传出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错");
		} finally {
			if(fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}
		
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		userServ.updateUserInfo(user);
		
		return IMoocJSONResult.ok(uploadPathDB);
	}
	
	
	@ApiOperation(value = "查询用户信息", notes = "查询用户信息接口")
	@ApiImplicitParam(name = "userId", value = "用户id", required = true, 
	dataType = "String", paramType = "query")
	@PostMapping("/query")
	public IMoocJSONResult query(String userId, String fanId) throws Exception {
		if(StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.errorMsg("用户id为空");
		}
		
		Users userInfo = userServ.queryUsersInfo(userId);
		UsersVO usersVO = new UsersVO();
		BeanUtils.copyProperties(userInfo, usersVO);
		
		usersVO.setFollow(userServ.queryIfFollow(userId, fanId));
		
		
		
		return IMoocJSONResult.ok(usersVO);
		
	}
	
	
	@PostMapping("/queryPublisher")
	public IMoocJSONResult query(String loginUserId, String videoId, String publisherId) throws Exception {
		if(StringUtils.isEmpty(publisherId)) {
			return IMoocJSONResult.errorMsg("");
		}
		
		//1.查询发布者信息
		Users userInfo = userServ.queryUsersInfo(publisherId);
		UsersVO publisher = new UsersVO();
		BeanUtils.copyProperties(userInfo, publisher);
		
		//2.点赞关系
		boolean userLikeVideo = userServ.isUserLikeVideo(loginUserId, videoId);
		
		PublisherVideo pv = new PublisherVideo();
		pv.setPublisher(publisher);
		pv.setUserLikeVideo(userLikeVideo);
		
		return IMoocJSONResult.ok(pv);
		
	}
	
	@PostMapping("/beyourfans")
	public IMoocJSONResult beyourfans(String userId, String fanId) throws Exception {
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)) {
			return IMoocJSONResult.errorMsg("");
		}
		
		userServ.saveUserFanRelation(userId, fanId);
		
		return IMoocJSONResult.ok("关注成功");
		
	}
	
	@PostMapping("/dontbeyourfans")
	public IMoocJSONResult dontbeyourfans(String userId, String fanId) throws Exception {
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(fanId)) {
			return IMoocJSONResult.errorMsg("");
		}
		
		userServ.deleteUserFanRelation(userId, fanId);
		
		return IMoocJSONResult.ok("取消关注成功");
		
	}
	
	@PostMapping("/reportUser")
	public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {
		
		
		userServ.reportUser(usersReport);
		
		return IMoocJSONResult.errorMsg("举报成功");
		
	}
}
