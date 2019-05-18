package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc, String userId);
	
	public void addVideoLikeCount(String videoId);
	
	public void reduceVideoLikeCount(String videoId);
	
	public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);
	
	public List<VideosVO> queryMyFollowVideos(String userId);
}