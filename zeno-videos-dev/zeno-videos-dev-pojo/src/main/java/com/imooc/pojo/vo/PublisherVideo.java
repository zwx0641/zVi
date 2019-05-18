package com.imooc.pojo.vo;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "用户对象", description = "用户对象")
public class PublisherVideo {
 
	public UsersVO publisher;
	
	public UsersVO getPublisher() {
		return publisher;
	}

	public void setPublisher(UsersVO publisher) {
		this.publisher = publisher;
	}
	
	public boolean userLikeVideo;

	public boolean isUserLikeVideo() {
		return userLikeVideo;
	}

	public void setUserLikeVideo(boolean userLikeVideo) {
		this.userLikeVideo = userLikeVideo;
	}
	
	
}