package com.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenreDTO {
	private Long seqno;
	private String genre;
}
