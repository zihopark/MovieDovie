package com.board.dto.movie;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MovieListResponse {

    @JsonProperty("movieListResult")
    private MovieListResult movieListResult;

    @Data
    public static class MovieListResult {
        @JsonProperty("totCnt")
        private int totalCount;

        @JsonProperty("source")
        private String source;

        @JsonProperty("movieList")
        private List<MovieDTO> movieList;
    }
}