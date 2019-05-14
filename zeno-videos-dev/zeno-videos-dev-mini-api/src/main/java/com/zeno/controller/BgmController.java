package com.zeno.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imooc.utils.IMoocJSONResult;
import com.zeno.service.BgmServ;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "bmg相关接口", tags = {"与bgm相关的controller"})
@RequestMapping("/bgm")
public class BgmController {
	
	@Autowired
	private	BgmServ bgmServ;
	
	@ApiOperation(value = "列出bgm列表", notes = "列出bgm的接口")
	@PostMapping("/list")
	public IMoocJSONResult list() {
		return IMoocJSONResult.ok(bgmServ.queryBgmList());
	}
	
	
}
