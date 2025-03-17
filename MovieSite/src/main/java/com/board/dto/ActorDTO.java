package com.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActorDTO {
	private Long seqno;
	private Long code;
	private String name;
	private String nameen;
	private String gender;
	private String role;
}
