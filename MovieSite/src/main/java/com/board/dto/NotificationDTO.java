package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.MemberEntity;
import com.board.entity.NotificationEntity;
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
public class NotificationDTO {

	private Long seqno;
	private MemberEntity memberEntity;
	private RequestEntity requestEntity;
	private String content;
	private String is_read;
	private LocalDateTime created_at;
	private String notitype;
	
	
	//Entity -> DTO 이동
	public NotificationDTO(NotificationEntity notificationEntity) {
		this.seqno = notificationEntity.getSeqno();
		this.memberEntity = notificationEntity.getMemberEntity();
		this.requestEntity = notificationEntity.getRequestEntity();
		this.content = notificationEntity.getContent();
		this.is_read = notificationEntity.getIs_read();
		this.created_at = notificationEntity.getCreated_at();
		this.notitype = notificationEntity.getNotitype();
	}
	
	
	//DTO -> Entity 이동
	public NotificationEntity dtoToEntity(NotificationDTO dto) {
		
		NotificationEntity notificationEntity = NotificationEntity.builder()
																.seqno(dto.getSeqno())
																.memberEntity(dto.getMemberEntity())
																.requestEntity(dto.getRequestEntity())
																.content(dto.getContent())
																.is_read(dto.getIs_read())
																.created_at(dto.getCreated_at())
																.notitype(dto.getNotitype())
																.build();
		return notificationEntity;
	}
	
	
}
