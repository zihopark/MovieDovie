package com.board.entity;

import java.time.LocalDateTime;
import com.board.dto.MemberDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name="member")
@Table(name="member")
public class MemberEntity {
	
	
	//Column(name="컬럼명", length=길이, nullable=null여부(false : not null / true : null)
	@Id
	@Column(name="email", length=320, nullable=false, unique = true)
	private String email;
	
	@Column(name="password", length=200, nullable=false)
	private String password;
	
	@Column(name="username", length=50, nullable=false)
	private String username;
	
	@Column(name="nickname", length=50, nullable=true)
	private String nickname;
	
	@Column(name="gender", length=20, nullable=true)
	private String gender;
	
	@Column(name="birthdate", nullable=true)
	private String birthdate;
	
	@Column(name="telno", length=50, nullable=false)
	private String telno;
	
	@Column(name="regdate", nullable=false)
	private LocalDateTime regdate;
	
	@Column(name="lastlogindate", nullable=true)
	private LocalDateTime lastlogindate;
	
	@Column(name="lastlogoutdate", nullable=true)
	private LocalDateTime lastlogoutdate;
	
	@Column(name="lastpwdate", nullable=true)
	private LocalDateTime lastpwdate;
	
	@Column(name="pwcheck", nullable=true)
	private Long pwcheck;
	
	@Column(name="role", length=50, nullable=false)
	private String role;
	
	@Column(name="org_filename")
	private String org_filename;
	
	@Column(name="stored_filename")
	private String stored_filename;
	
	@Column(name="filesize",nullable=true)
	private Long filesize;
	
	@Column(name="memeberPoint", columnDefinition = "integer default 0")
	private Long point;
	
	//회원등급	
	@Column(name="member_grade", length=50, nullable=true,columnDefinition = "varchar(50) default 'BRONZE'")
	private String grade;
	
	
	public void updateMemberInfo(MemberDTO member) {
		
		this.email = member.getEmail();
		this.username = member.getUsername();
		this.nickname = member.getNickname();
		this.gender = member.getGender();
		this.birthdate = member.getBirthdate();
		this.telno = member.getTelno();
		this.filesize = member.getFilesize();
		this.point = member.getPoint();
		this.org_filename = member.getOrgFilename();
		this.stored_filename = member.getStoredFilename();
		this.grade = member.getGrade();
		this.lastpwdate = LocalDateTime.now();
		this.pwcheck = 1L;
	
		
	}

	
	

}
