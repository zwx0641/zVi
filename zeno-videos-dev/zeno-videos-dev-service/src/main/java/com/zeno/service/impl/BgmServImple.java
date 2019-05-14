package com.zeno.service.impl;

import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.mapper.BgmMapper;
import com.imooc.pojo.Bgm;
import com.zeno.service.BgmServ;

@Service
public class BgmServImple implements BgmServ {
	
	@Autowired
	private BgmMapper bgmMapper;
	
	@Autowired
	private Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		
		return bgmMapper.selectAll();
	} 


}
