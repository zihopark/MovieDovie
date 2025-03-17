package com.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.board.dto.movie.MovieDTO;
import com.board.service.MovieAPIServiceImpl;
import com.board.service.TmdbAPIServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MakeController {
    private final MovieAPIServiceImpl movieService;
    private final TmdbAPIServiceImpl tmdbService;
    
    @GetMapping("/make/movie")
    public Mono<String> getMake(Model model) {
        return tmdbService.getGenreList()
                .doOnNext(genres -> model.addAttribute("genres", genres))
                .thenReturn("make/movie");
    }
    
    // @PostMapping("/make/movie/save-all")
    // @ResponseBody
    // public Map<String,Object> postSaveAll(){
    // 	  List<MovieDTO> list = tmdb.getTrendingMovies("week", 1, "ko-KR").block();
    //       List<MovieDTO> temp = new ArrayList<>();
          
    //       for(MovieDTO d : list) {
    //           MovieDTO dto = tmdb.getMovieDetails(d.getId(), "ko-KR").block();
    //           temp.add(dto);
    //       }

    //       movie.saveWeeklyTrend(temp);
    // 	Map<String,Object> data = new HashMap<>();
    	
    // 	data.put("message", "good");
    	
    // 	return data;
    // }


    @PostMapping("/make/movie/save-all")
    @ResponseBody
    public Mono<Map<String,Object>> postSaveAll(){
        return tmdbService.getTrendingMovies("week", 1, "ko-KR")
        .flatMapMany(Flux::fromIterable)
        .flatMap(movie -> tmdbService.getMovieDetails(movie.getId(), "ko-KR"))
        .collectList()
        .flatMap(movieList -> {
            movieService.saveWeeklyTrend(movieList);
            Map<String,Object> data = new HashMap<>();
            data.put("message", "good");
            return Mono.just(data);
        });
    }

    // @PostMapping("/make/movie/save")
    // @ResponseBody
    // public Map<String,String> postMovies(@RequestParam("movieId") Long movieId) {
    // 	MovieDTO dto = tmdb.getMovieDetails(movieId, "ko-KR").block();
    // 	Map<String,String> data = new HashMap<>();
    	
    // 	//todo : 저장이 안될 시 오류처리 코드 작성해야함
    // 	data.put("message", "good");
    	
    // 	movie.directSave(dto);
    // 	return data;
    // }

    @PostMapping("/make/movie/save")
    @ResponseBody
    public Mono<Map<String,String>> postMovies(@RequestParam("movieId") Long movieId) {
        return tmdbService.getMovieDetails(movieId, "ko-KR")
                .flatMap(dto -> {
                    movieService.directSave(dto);
                    Map<String,String> data = new HashMap<>();
                    data.put("message", "good");
                    return Mono.just(data);
                });
    }
	
    // @PostMapping("/make/movie/searchKeyword")
	// @ResponseBody
	// public List<MovieDTO> postSearchKeyword(@RequestParam("keyword") String keyword){
    // 	 List<List<MovieDTO>> list = tmdb.searchMovies(keyword,2).block();
    // 	return list.get(0);
    // }

    @PostMapping("/make/movie/searchKeyword")
    @ResponseBody
    public Mono<List<MovieDTO>> postSearchKeyword(@RequestParam("keyword") String keyword) {
        return tmdbService.searchMovies(keyword, 1)
                .map(lists -> lists.get(0));
    }

    @PostMapping("/make/movie/searchTrend")
    @ResponseBody
    public Mono<List<MovieDTO>> postSearchTrend() {
        return tmdbService.getTrendingMovies("day", 1, "ko-KR");
    }
    
    @PostMapping("/api/movies/save-by-genre")
    @ResponseBody
    public Mono<String> saveMoviesByGenre(@RequestParam("genreId") Long genreId) {
        return tmdbService.getMoviesByGenre(genreId, 1)
        .flatMapMany(Flux::fromIterable)
        .flatMap(movie -> tmdbService.getMovieDetails(movie.getId(), "ko-KR"))
        .collectList()
        .flatMap(movieList -> {
            try {
                movieService.saveTheDataBases(movieList);
               return Mono.just(genreId + ":" + movieList.size() + "개가 성공적으로 저장되었습니다.");
            } catch (Exception e) {
                return Mono.just("영화 저장 중 오류가 발생했습니다: " + e.getMessage());
            }
        });
    }
}