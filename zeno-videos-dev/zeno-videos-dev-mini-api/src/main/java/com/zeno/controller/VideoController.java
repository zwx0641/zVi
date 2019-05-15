package com.zeno.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.imooc.pojo.Bgm;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.zeno.service.BgmServ;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "视频相关处理接口", tags = {"与视频处理相关的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController{
	
	@Autowired
	private BgmServ bgmServ;
	
	@ApiOperation(value = "上传视频", notes = "视频接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户id", required = true, 
				dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, 
		dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoSeconds", value = "视频长度", required = true, 
		dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, 
		dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, 
		dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "desc", value = "视频描述", required = false, 
		dataType = "String", paramType = "form")
	})
	@PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
	public IMoocJSONResult uploadFace(String userId, String bgmId, double videoSeconds,
			int videoWidth, int videoHeight, String desc,
			@ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {
		if(StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.errorMsg("用户id为空");
		}
		
		//上传的文件目录
		//String fileSpace = "C:/workspace/zenoVi/zeno-videos-store";
		
		//数据库中相对路径
		String uploadPathDB = "/" + userId + "/video";
		String finalVideoPath = "";
		
		
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if(file != null) {
				
				
				String filename = file.getOriginalFilename();
				
				if (!StringUtils.isEmpty(filename)) {
					//绝对路径
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + filename;
					//相对路径
					uploadPathDB += ("/" + filename);
					
					File outFile = new File(finalVideoPath);
					if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
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
		
		//判断bgmID， 查询bgm信息并合成新的视频
		if (!StringUtils.isEmpty(bgmId)) {
			Bgm bgm = bgmServ.queryBgmById(bgmId);
			String mp3InputPath = FILE_SPACE + bgm.getPath();
			
			String videoInputPath = finalVideoPath;
			MergeVideoMp3 toolMergeVideoMp3 = new MergeVideoMp3(FFMPEGEXE);
			
			String videoOutputName = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = "/" + userId + "/video" + videoOutputName;
			finalVideoPath = FILE_SPACE +uploadPathDB;
			toolMergeVideoMp3.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
		}
		
		
		return IMoocJSONResult.ok(uploadPathDB);
	}
	
}
