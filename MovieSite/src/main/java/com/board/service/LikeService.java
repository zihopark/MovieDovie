package com.board.service;

import java.util.List;
import java.util.Map;

import com.board.dto.MemberDTO;

public interface LikeService {

	//좋아요 % 높은 영화 추출
	public List<Object[]> topLikeMovie();
	
	//싫어요 % 높은 영화 추출
	public List<Object[]> topDislikeMovie();
	
	//호불호 % 높은 영화 추출
	public List<Object[]> topDivisiveMovie();

	public void saveOrUpdate(String rating, Long movieId, String email,boolean isAdd);
	
	//나의 like 정보
	public Map<String,Object> getLikeInfo(String email, Long movieId);
	
	//현재 영화의 전체 like정보 추출
	public Map<String,Object> getAllLikeInfo(Long movieId);
	
	public List<Map<String,Object>> getAllLikeByEmail(String email);
	
	public List<Map<String,Object>> getTopList(int topCount,MemberDTO memberDTO);
}
