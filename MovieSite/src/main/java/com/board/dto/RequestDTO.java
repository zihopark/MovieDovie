package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.RequestEntity;

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
public class RequestDTO {
	private Long seqno;
	private String title;
	private LocalDateTime reqdate;
	private String status;
	private String content;
	private String email;
	

	//Entity --> DTO 이동
	public RequestDTO(RequestEntity requestEntity) {
		this.seqno = requestEntity.getSeqno();
		this.title = requestEntity.getTitle();
		this.reqdate = requestEntity.getReqdate();
		this.status = requestEntity.getStatus();
		this.content = requestEntity.getContent();
		this.email = requestEntity.getEmail();
	}
		
	//DTO --> Entity 이동
	public RequestEntity dtoToEntity(RequestDTO dto) {
		
		RequestEntity requestEntity = RequestEntity.builder()
											.seqno(dto.getSeqno())
											.title(dto.getTitle())
											.reqdate(dto.getReqdate())
											.status(dto.getStatus())
											.content(dto.getContent())
											.email(dto.getEmail())
											.build();									
		return requestEntity;
	}
}
