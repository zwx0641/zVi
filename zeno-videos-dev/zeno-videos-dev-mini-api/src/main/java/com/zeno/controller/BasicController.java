package com.zeno.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.imooc.utils.RedisOperator;

@RestController
public class BasicController {
	
	@Autowired
	public RedisOperator redis;
	
	public static final String USER_REDIS_SESSION = "user-redis-session";
	
	//文件保存地址
	public static final String FILE_SPACE = "C:/workspace/zenoVi/zeno-videos-store";
	
	//ffmpeg工具目录
	public static final String FFMPEGEXE = "C:\\ffmpeg\\bin\\ffmpeg.exe";
}
