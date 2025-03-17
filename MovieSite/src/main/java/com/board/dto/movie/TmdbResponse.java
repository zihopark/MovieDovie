package com.board.dto.movie;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TmdbResponse {

	private List<MovieDTO> results;
	private int page;
	private int totalPages;
	private int totalResults;

	// Getters and setters
	public List<MovieDTO> getResults() {
		return results;
	}

}
