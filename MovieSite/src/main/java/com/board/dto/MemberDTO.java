package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberDTO {
	private String email;
	private String password;
	private String username;
	private String nickname;
	private String gender;
	private String birthdate;
	private String telno;
	private LocalDateTime regdate;
	private LocalDateTime lastlogindate;
	private LocalDateTime lastlogoutdate;
	private LocalDateTime lastpwdate;
	private Long pwcheck;
	private String role;
	private String orgFilename;
	private String storedFilename;
	private long filesize;
	private Long point;
	private String grade;
	
	//Entity -> DTO
	public MemberEntity dtoToEntity(MemberDTO memberDTO) {
		MemberEntity memberEntity = MemberEntity.builder()
											.email(memberDTO.getEmail())
											.password(memberDTO.getPassword())
											.username(memberDTO.getUsername())
											.nickname(memberDTO.getNickname())
											.gender(memberDTO.getGender())
											.birthdate(memberDTO.getBirthdate())
											.telno(memberDTO.getTelno())
											.regdate(memberDTO.getRegdate())
											.lastlogindate(memberDTO.getLastlogindate())
											.lastlogoutdate(memberDTO.getLastlogoutdate())
											.lastpwdate(memberDTO.getLastpwdate())
											.pwcheck(memberDTO.getPwcheck())
											.role(memberDTO.getRole())
											.org_filename(memberDTO.getOrgFilename())
											.stored_filename(memberDTO.getStoredFilename())
											.filesize(memberDTO.getFilesize())
											.point(memberDTO.getPoint())
											.grade(memberDTO.getGrade())
											.build();
		return memberEntity;
	}
	 
	//DTO -> Entity
	public MemberDTO(MemberEntity memberEntity) {
		this.email = memberEntity.getEmail();
		this.password = memberEntity.getPassword();
		this.username = memberEntity.getUsername();
		this.nickname = memberEntity.getNickname();
		this.gender = memberEntity.getGender();
		this.birthdate = memberEntity.getBirthdate();
		this.telno = memberEntity.getTelno();
		this.regdate= memberEntity.getRegdate();
		this.lastlogindate = memberEntity.getLastlogindate();
		this.lastlogoutdate = memberEntity.getLastlogoutdate();
		this.lastpwdate = memberEntity.getLastpwdate();
		this.pwcheck = memberEntity.getPwcheck();
		this.role = memberEntity.getRole();
		this.orgFilename = memberEntity.getOrg_filename();
		this.storedFilename = memberEntity.getStored_filename();
		this.grade = memberEntity.getGrade();
		// filesize가 null이면 기본값으로 설정 (예: 0L)
	    this.filesize = memberEntity.getFilesize() != null ? memberEntity.getFilesize() : 0L;
	    
		this.point=memberEntity.getPoint();
		
		
	}
}