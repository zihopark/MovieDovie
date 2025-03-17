package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.RequestReplyEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestReplyDTO {

	private Long seqno;
	private String email;
	private Long reqseqno;
	private String replywriter;
	private String replycontent;
	private LocalDateTime replyregdate;
	
	//Entity -> DTO 이동
	public RequestReplyDTO(RequestReplyEntity requestReplyEntity) {
		this.seqno = requestReplyEntity.getSeqno();
		this.email = requestReplyEntity.getEmail();
		this.reqseqno = requestReplyEntity.getReqseqno();
		this.replywriter = requestReplyEntity.getReplywriter();
		this.replycontent = requestReplyEntity.getReplycontent();
		this.replyregdate = requestReplyEntity.getReplyregdate();
	}
	
	//DTO --> Entity 이동
	public RequestReplyEntity dtoToEntity(RequestReplyDTO dto){
		
		RequestReplyEntity requestReplyEntity = RequestReplyEntity.builder()
																.seqno(dto.getSeqno())
																.reqseqno(dto.getReqseqno())
																.email(dto.getEmail())
																.replywriter(dto.getReplywriter())
																.replycontent(dto.getReplycontent())
																.replyregdate(dto.getReplyregdate())
																.build();					
		return requestReplyEntity;
	}
	
	
	
}
