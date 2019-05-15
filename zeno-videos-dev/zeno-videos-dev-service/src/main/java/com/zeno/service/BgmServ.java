package com.zeno.service;

import java.util.List;

import com.imooc.pojo.Bgm;

public interface BgmServ {
	//查询背景音乐列表
	public List<Bgm> queryBgmList();
	
	//查询bgm信息
	public Bgm queryBgmById(String bgmId);
}