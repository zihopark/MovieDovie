package com.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FilmoListDTO {
	private Long seqno;
	private Long movieseqno;
	private Long actorseqno;
}
