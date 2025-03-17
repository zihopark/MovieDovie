package com.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="profilefile")
@Table(name="profilefile")
public class FileEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROFILE_FILE_SEQ")
	@SequenceGenerator(name="PROFILE_FILE_SEQ", sequenceName = "profile_file_seq", initialValue = 1, allocationSize = 1)
	private Long fileseqno;
	
	@Column(name="seqno", nullable=false)
	private Long seqno;
	
	@Column(name="email", length=20, nullable=false)
	private String email;
	
	@Column(name="org_filename", length=200, nullable=false)
	private String org_filename;
	
	@Column(name="stored_filename", length=200, nullable=false)
	private String stored_filename;
	
	@Column(name="filesize", nullable=false)
	private Long filesize;
	
	@Column(name="checkfile", length=2, nullable=false)
	private String checkfile;

}
