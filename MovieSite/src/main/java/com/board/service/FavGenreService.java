package com.board.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.board.dto.FavGenreDTO;

public interface FavGenreService {

	//관리자페이지 - 통계 : 회원들이 좋아하는 장르 인기 순으로 뽑아내기
	public List<Object[]> favGenreResult();
	
	//관리자페이지 - 통계 : 남성이 좋아하는 장르 인기 순으로 뽑아내기
	public List<Object[]> findTopGenresForMale();
	
	//관리자페이지 - 통계 : 여성이 좋아하는 장르 인기 순으로 뽑아내기
	public List<Object[]> findTopGenresForFemale();
	
	
	public void saveTheFavGenre(FavGenreDTO favGenre);
	
	public Optional<Map<String,Object>> getFavGenre(String email);
}
