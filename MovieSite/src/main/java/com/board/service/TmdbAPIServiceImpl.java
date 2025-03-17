package com.board.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.board.dto.movie.MovieDTO;
import com.board.dto.movie.TmdbResponse;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class TmdbAPIServiceImpl implements TmdbAPIService {
	@Value("${tmdb.api.base-url}")
	private String baseUrl;
	
	@Value("${tmdb.api.token}")
	private String token;
	
    private WebClient webClient;
    public TmdbAPIServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://api.themoviedb.org/3")
            .defaultHeader("accept", "application/json")
            .defaultHeader("Accept-Language", "ko-KR")
            .build();
    }

    @PostConstruct
    public void initWebClient() {
        this.webClient = this.webClient.mutate()
            .defaultHeader("Authorization", "Bearer " + token)
            .build();
    }
    
    public Mono<String> getAuthentication(){
    	return webClient.get()
    			.uri("/authentication")
    			.retrieve()
    			.bodyToMono(String.class);
    }
    //==============초반 설정 ^^^^^^^^^^^^^^^^^^^^^^^===================
    
    //최신 유행 영화 가져오기
    //timeWindow : week/day
    //page : 결과의 페이지번호(페이당 20개)
    //language : 결과의 언어
    public Mono<List<MovieDTO>> getTrendingMovies(String timeWindow, int page, String language){
    	 return webClient.get()
    			 .uri(uriBuilder -> uriBuilder
    					 .path("/trending/movie/{timeWindow}")
    					 .queryParam("api_key",token)
    					 .queryParam("page", page)
    					 .queryParam("language",language)
    					 .build(timeWindow))
    			 .retrieve()
    			 .bodyToMono(TmdbResponse.class)
    			 .map(TmdbResponse::getResults)
    			 .onErrorResume(e -> {
    			     log.error("Error fetching trending movies", e);
    			     return Mono.empty();
    			 });    	
    }
    
	//sort_by: 정렬 기준 (예: popularity.desc, vote_average.desc)
	//with_genres: 특정 장르 필터링
	//primary_release_year: 특정 연도의 영화만 필터링
    //vote_average.gte: 최소 평점 설정
    public Mono<MovieDTO> getDiscoverMovies(String sortBy, String withGenres, int page, String language) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("api_key", token)
                        .queryParam("sort_by", sortBy)
                        .queryParam("with_genres", withGenres)
                        .queryParam("page", page)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(MovieDTO.class);
    }
    
    public Mono<MovieDTO> getMovieDetails(Long movieId,String language) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/{movieId}")
                        .queryParam("api_key", token)
                        .queryParam("language", language)
                        .queryParam("include_image_language", "null,ko,en")
                        .queryParam("append_to_response", "credits,videos,release_dates,images")
                        .build(movieId))
                .retrieve()
                .bodyToMono(MovieDTO.class);
    }
    
    public Mono<List<MovieDTO>> getMoviesByGenre(Long genreId, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/discover/movie")
                    .queryParam("api_key", token)
                    .queryParam("with_genres", genreId)
                    .queryParam("page", page)
                    .build())
                .retrieve()
                .bodyToMono(TmdbResponse.class)
   			 .map(TmdbResponse::getResults)
   			 .onErrorResume(e -> {
   			     log.error("Error fetching trending movies", e);
   			     return Mono.empty();
   			 });    	
    }
    
    public Mono<Map<String, Integer>> getGenreList() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/genre/movie/list")
                    .queryParam("api_key", token)
                    .queryParam("language", "ko")
                    .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {})
                .map(response -> {
                    Map<String, Integer> genreMap = new HashMap<>();
                    List<Map<String, Object>> genres = response.get("genres");
                    for (Map<String, Object> genre : genres) {
                        genreMap.put((String) genre.get("name"), ((Integer) genre.get("id")));
                    }
                    return genreMap;
                });
    }
    
    public Mono<List<MovieDTO>> getMoviesByGenreName(String genreName, int page) {
        return getGenreList()
            .flatMap(genreMap -> {
                Integer genreId = genreMap.get(genreName);
                if (genreId == null) {
                    return Mono.error(new IllegalArgumentException("Invalid genre name"));
                }
                return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("api_key", token)
                        .queryParam("with_genres", genreId)
                        .queryParam("page", page)
                        .queryParam("language", "ko-KR")
                        .build())
                    .retrieve()
                    .bodyToMono(TmdbResponse.class)
          			 .map(TmdbResponse::getResults)
          			 .onErrorResume(e -> {
          			     log.error("Error genre name = ", e);
          			     return Mono.empty();
          			 });  
				});
	}
    
    public Mono<List<List<MovieDTO>>> searchMovies(String query,int pageCnt) {
        return Flux.range(1, pageCnt)
        		.flatMap(page -> 
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/search/movie")
                .queryParam("api_key", token)
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("language", "ko-KR")
                .build())
            .retrieve()
            .bodyToMono(TmdbResponse.class)
            .map(TmdbResponse::getResults)
            .onErrorResume(e->{
            	 log.error("Error searching movie = ", e);
  			     return Mono.empty();
            })).collect(Collectors.toList());
    }
    
//    public Mono<PersonResponse> searchPerson(String query) {
//        return webClient.get()
//            .uri(uriBuilder -> uriBuilder
//                .path("/search/person")
//                .queryParam("api_key", token)
//                .queryParam("query", query)
//                .queryParam("language", "ko-KR")
//                .build())
//            .retrieve()
//            .bodyToMono(PersonResponse.class);
//    }
}
