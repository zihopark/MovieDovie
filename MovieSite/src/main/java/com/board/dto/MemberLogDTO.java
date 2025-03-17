package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.MemberLogEntity;

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
public class MemberLogDTO {
	private Long seqno;
	private LocalDateTime inouttime;
	private String status;
	private String email;
	
	//Entity --> DTO 이동
		public MemberLogDTO(MemberLogEntity memberLogEntity) {
			this.seqno = memberLogEntity.getSeqno();
			this.inouttime = memberLogEntity.getInouttime();
			this.status = memberLogEntity.getStatus();
			this.email = memberLogEntity.getEmail();
		}
			
		//DTO --> Entity 이동
		public MemberLogEntity dtoToEntity(MemberLogDTO dto) {
			
			MemberLogEntity memberLogEntity = MemberLogEntity.builder()
												.seqno(dto.getSeqno())
												.inouttime(dto.getInouttime())
												.status(dto.getEmail())
												.email(dto.getEmail())
												.build();									
			return memberLogEntity;
		}
}
