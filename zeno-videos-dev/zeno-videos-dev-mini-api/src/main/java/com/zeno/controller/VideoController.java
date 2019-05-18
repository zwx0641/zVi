package com.zeno.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.druid.util.StringUtils;
import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Videos;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;
import com.imooc.utils.ScreenshotVideo;
import com.zeno.service.BgmServ;
import com.zeno.service.VideoServ;

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
	
	@Autowired
	private VideoServ videosServ;
	
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
		String coverPathDB = "/" + userId + "/video";
		String finalVideoPath = "";
		
		
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if(file != null) {
				
				
				String filename = file.getOriginalFilename();
				
				String filenamePrefix = filename.split("\\.")[0];
				
				if (!StringUtils.isEmpty(filename)) {
					//绝对路径
					finalVideoPath = FILE_SPACE + uploadPathDB + "/" + filename;
					//相对路径
					uploadPathDB += ("/" + filename);
					coverPathDB += ( "/" + filenamePrefix + ".jpg");
					
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
			uploadPathDB = "/" + userId + "/video/" + videoOutputName;
			finalVideoPath = FILE_SPACE +uploadPathDB;
			toolMergeVideoMp3.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
		}
		
		//把视频截图
		ScreenshotVideo screenshotVideo = new ScreenshotVideo(FFMPEGEXE);
		screenshotVideo.convertor(finalVideoPath, FILE_SPACE + coverPathDB);
		
		//把视频保存到数据库
		Videos videos = new Videos();
		videos.setAudioId(bgmId);
		videos.setUserId(userId);
		videos.setVideoSeconds((float)videoSeconds);
		videos.setVideoHeight(videoHeight);
		videos.setVideoDesc(desc);
		videos.setVideoWidth(videoWidth);
		videos.setVideoPath(uploadPathDB);
		videos.setStatus(VideoStatusEnum.SUCCESS.value);
		videos.setCreateTime(new Date());
		videos.setCoverPath(coverPathDB);
		String returnId = videosServ.saveVideo(videos);
		return IMoocJSONResult.ok(returnId);
	}
	

	@ApiOperation(value = "上传封面", notes = "封面接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户id", required = true, 
				dataType = "String", paramType = "form"),
		@ApiImplicitParam(name = "videoId", value = "视频id", required = true, 
				dataType = "String", paramType = "form"),
	})
	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
	public IMoocJSONResult uploadFace(String userId, String videoId,
			@ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {
		if(StringUtils.isEmpty(videoId) || StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.errorMsg("视频id或用户id为空");
		}
		
		//上传的文件目录
		//String fileSpace = "C:/workspace/zenoVi/zeno-videos-store";
		
		//数据库中相对路径
		String uploadPathDB = "/" + userId + "/video";
		String finalCoverPath = "";
		
		
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if(file != null) {
				
				
				String filename = file.getOriginalFilename();
				
				if (!StringUtils.isEmpty(filename)) {
					//绝对路径
					finalCoverPath = FILE_SPACE + uploadPathDB + "/" + filename;
					//相对路径
					uploadPathDB += ("/" + filename);
					
					File outFile = new File(finalCoverPath);
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
		
		videosServ.updateVideo(videoId, uploadPathDB);

		return IMoocJSONResult.ok(uploadPathDB);
	}
	
	//分页和搜索查询列表
	//isSaveRecord = 1 - 需要保存
	//				0 - 不需要保存
	@PostMapping(value = "/showAll")
	public IMoocJSONResult showAll(@RequestBody Videos video, 
			Integer isSaveRecord, Integer page, Integer pageSize) throws Exception{
		
		if(page == null) {
			page = 1;
		}
		
		if (pageSize == null) {
			pageSize = PAGE_SIZE;
		}
		
		PagedResult result = videosServ.getAllVideos(video, isSaveRecord, page, PAGE_SIZE);
		
		return IMoocJSONResult.ok(result);
		
	}
	
	@PostMapping(value = "/showMyLike")
	public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception{
		
		if(StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.ok();
		}
		
		if(page == null) {
			page = 1;
		}
		
		if(pageSize == null) {
			pageSize = 6;
		}
		
		PagedResult result = videosServ.queryMyLikeVideos(userId, page, pageSize);
		
		return IMoocJSONResult.ok(result);
		
	}
	
	@PostMapping(value = "/showMyFollow")
	public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception{
		
		if(StringUtils.isEmpty(userId)) {
			return IMoocJSONResult.ok();
		}
		
		if(page == null) {
			page = 1;
		}
		
		int pageSize = 6;
		
		PagedResult result = videosServ.queryMyFollowVideos(userId, page, pageSize);
		
		return IMoocJSONResult.ok(result);
		
	}
	
	@PostMapping(value = "/hot")
	public IMoocJSONResult hot() throws Exception {
		
		
		return IMoocJSONResult.ok(videosServ.getHotwords());
	}
	
	@PostMapping(value = "/userLike")
	public IMoocJSONResult userLike(String userId, String videoId, String videoCreatorId) 
			throws Exception {
		
		videosServ.userLikeVideo(userId, videoId, videoCreatorId);
		
		return IMoocJSONResult.ok();
	}
	
	@PostMapping(value = "/userUnlike")
	public IMoocJSONResult userUnlike(String userId, String videoId, String videoCreatorId) 
			throws Exception {
		
		videosServ.userUnlikeVideo(userId, videoId, videoCreatorId);
		return IMoocJSONResult.ok(videosServ.getHotwords());
	}
}
