package com.board.service;

import java.util.List;
import java.util.Map;

import com.board.dto.MemberDTO;

public interface ReplyService {
	public List<Map<String,Object>> getList(Long movieId);
	public String saveOrUpdate(String content, String email, Long movieId);
	public List<Map<String, Object>> getTopList(int topCount,MemberDTO memberDTO);
}
