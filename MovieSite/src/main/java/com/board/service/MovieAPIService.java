package com.board.service;

import java.util.List;
import java.util.Map;

import com.board.dto.MemberDTO;
import com.board.dto.movie.MovieDTO;
import com.board.entity.movie.GenreEntity;

public interface MovieAPIService {

	//주간 트렌드 영화 가져오기
	public List<Map<String, Object>> getWeeklyTrend(MemberDTO memberDTO);
	//DB에 저장되어 있는 영화상세정보가져오기
	public Map<String,Object> getMovieDetails(Long id,MemberDTO memberDTO);
	
	//해당 장르리스트 가져오기
	public List<GenreEntity> getGenreList(String genre);
	
	//같은 장르의 영화리스트 가져오기
	public List<Map<String,Object>> findByGenreGetMovieList(List<GenreEntity> genres,int age,MemberDTO memberDTO);
	
	//키워드로 DB에 저장되어 있는 영화가져오기
	public List<Map<String,Object>> findByKeywordMovieList(String keyword,MemberDTO memberDTO);
	
	//키워드로 DB에 저장되어 있는 크루가져오기
	public List<Map<String,Object>> findByKeywordCrewList(String keyword,MemberDTO memberDTO);

	//키워드로 DB에 저장되어 있는 캐스트가져오기
	public List<Map<String,Object>> findByKeywordCastList(String keyword,MemberDTO memberDTO);

	//키워드로 DB에 저장되어 있는 제작사가져오기
	public List<Map<String,Object>> findByKeywordProductionCompanyList(String keyword,MemberDTO memberDTO);
	
	public int clickToMovie(Long movieId);
	
	//저장 및 업데이트
	public void saveTheDataBases(List<MovieDTO> list);
	public void saveTheDataBase(MovieDTO movie);
	public void saveWeeklyTrend(List<MovieDTO> list);
	
	//관리자 업로드 저장 및 업데이트
	void directSave(MovieDTO movie) ;
	
	//관리자페이지 : 영화 수정 - Certification 이 null 인 영화 리스트 뽑아내기
	public List<Object[]> findByCert();


	List<Map<String, Object>> getDirectUploadList(int minusDay,MemberDTO memberDTO);
	
	
}
