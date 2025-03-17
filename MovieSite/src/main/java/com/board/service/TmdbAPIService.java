package com.board.service;

import java.util.List;

import com.board.dto.movie.MovieDTO;

import reactor.core.publisher.Mono;

public interface TmdbAPIService {
	// 최신 트렌드 영화 가져오기
	public Mono<List<MovieDTO>> getTrendingMovies(String timeWindow, int page, String language);
	//영화 필터 검색
	public Mono<MovieDTO> getDiscoverMovies(String sortBy, String withGenres, int page, String language);
	//영화 상세 정보 검색
    public Mono<MovieDTO> getMovieDetails(Long movieId, String language);
}
