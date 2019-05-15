package com.zeno.service;

import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

public interface VideoServ {
	
	//查询bgm信息
	public String saveVideo(Videos videos);
	
	//修改视频的封面
	public void updateVideo(String videoId, String coverPath);
	
	//查询所有视频
	public PagedResult getAllVideos(Integer page, Integer pageSize);
}