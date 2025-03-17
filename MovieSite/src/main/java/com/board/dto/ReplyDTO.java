package com.board.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReplyDTO {
	private Long seqno;
	private String content;
	private LocalDateTime replydate;
	private String email;
	private Long movieseqno;
}
