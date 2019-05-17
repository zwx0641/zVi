package com.zeno.service.impl;

import java.lang.annotation.Annotation;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.SearchRecordsMapper;
import com.imooc.mapper.UsersLikeVideosMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.VideosMapper;
import com.imooc.mapper.VideosMapperCustom;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.PagedResult;
import com.zeno.service.VideoServ;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServImple implements VideoServ {
	
	@Autowired
	private VideosMapper videosMapper;
	
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	
	@Autowired
	private UsersMapper userMapper;
	
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	
	@Autowired UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private Sid sid;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos videos) {
		String id = sid.nextShort();
		videos.setId(id);
		
		videosMapper.insertSelective(videos);
		return id;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideo(String videoId, String coverPath) {
		Videos videos = new Videos();
		videos.setId(videoId);
		videos.setCoverPath(coverPath);
		videosMapper.updateByPrimaryKeySelective(videos);
		
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
		
		//保存热搜词
		String desc = video.getVideoDesc();
		if(isSaveRecord != null && isSaveRecord == 1) {
			SearchRecords record = new SearchRecords();
			String recordId = sid.nextShort();
			record.setId(recordId);
			record.setContent(desc);
			searchRecordsMapper.insert(record);
		}
		
		PageHelper.startPage(page, pageSize);
		
		List<VideosVO> list = videosMapperCustom.queryAllVideos(desc);
		
		PageInfo<VideosVO> pageList = new PageInfo<>(list);
		
		PagedResult pagedResult = new PagedResult();
		pagedResult.setPage(page);
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());
		
		return pagedResult;
		
		
	}
	
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotwords() {
		
		return searchRecordsMapper.getHotwords();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userLikeVideo(String userId, String videoId, String videoCreatorId) {
		//保存用户视频喜欢关联关系表
		String likeId = sid.nextShort();
		
		
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(likeId);
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		
		
		usersLikeVideosMapper.insert(ulv);
		
		//视频喜欢数量累加
		videosMapperCustom.addVideoLikeCount(videoId);

		//用户喜欢数量累加
		userMapper.addGetLikeCount(videoCreatorId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userUnlikeVideo(String userId, String videoId, String videoCreatorId) {
		//删除用户视频喜欢关联关系表
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		
		usersLikeVideosMapper.deleteByExample(example);
		
		//视频喜欢数量累-
		videosMapperCustom.reduceVideoLikeCount(videoId);
		
		//用户喜欢数量累-
		userMapper.reduceGetLikeCount(videoCreatorId);
		
	} 


}
