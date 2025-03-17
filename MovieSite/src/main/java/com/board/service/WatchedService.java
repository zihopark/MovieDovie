package com.board.service;

import java.util.List;
import java.util.Map;

import com.board.dto.MemberDTO;

public interface WatchedService {
	public void saveOrUpdate(String action, Long movieId, String email);

	public Map<String, Object> getWatchedInfo(String email, Long movieId);

	public Map<String, Object> getAllWatchedInfo(Long movieId);
	public List<Map<String,Object>> getAllWatchedByEmail(String email);

	
	List<Map<String,Object>> getTopList(int topCount,MemberDTO memberDTO);
}
