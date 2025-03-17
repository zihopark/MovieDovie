package com.board.entity;

import java.time.LocalDateTime;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="request")
@Table(name="request")
public class RequestEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_SEQ")
	@SequenceGenerator(name="REQUEST_SEQ", sequenceName = "request_seq", initialValue = 1, allocationSize = 1)
	private long seqno;
	
	@Column(name = "title", length=50, nullable = false)
	private String title;
	
	@Column(name="reqdate",nullable=false)
	private LocalDateTime reqdate;
	
	@Column(name = "status", length=50, nullable = false)
	private String status;
	
	@Column(name = "content", length=2000, nullable = true)
	private String content;
	
	@Column(name = "email", length=320, nullable = false)
	private String email;
	
	/* FK 만들기
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="email", nullable = false)
	private MemberEntity email;
	*/
}
