package com.zeno.service;

import java.util.List;

import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

public interface VideoServ {
	
	//查询bgm信息
	public String saveVideo(Videos videos);
	
	//修改视频的封面
	public void updateVideo(String videoId, String coverPath);
	
	//查询所有视频
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);
	
	//
	public List<String> getHotwords();
	
	//点赞
	public void userLikeVideo(String userId, String videoId, String videoCreatorId);
	
	//取消点赞
	public void userUnlikeVideo(String userId, String videoId, String videoCreatorId);
	
	public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);
	
	public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

}