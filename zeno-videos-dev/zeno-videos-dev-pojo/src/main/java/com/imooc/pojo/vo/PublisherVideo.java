package com.imooc.pojo.vo;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "用户对象", description = "用户对象")
public class PublisherVideo {
    
	public UsersVO pulisher;
	
	public boolean userLikeVideo;

	public UsersVO getPulisher() {
		return pulisher;
	}

	public void setPulisher(UsersVO pulisher) {
		this.pulisher = pulisher;
	}

	public boolean isUserLikeVideo() {
		return userLikeVideo;
	}

	public void setUserLikeVideo(boolean userLikeVideo) {
		this.userLikeVideo = userLikeVideo;
	}
	
	
}