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
@Entity(name="req_reply")
@Table(name="req_reply")
public class RequestReplyEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQ_REPLY_SEQ")
	@SequenceGenerator(name="REQ_REPLY_SEQ", sequenceName = "req_reply_seq", initialValue = 1, allocationSize = 1)
	private Long seqno;
	
	@Column(name = "email", length=320, nullable = false)
	private String email;
	
	@Column(name="reqseqno", nullable = false)
	private Long reqseqno;
	
	@Column(name = "replywriter", length = 50, nullable = false)
	private String replywriter; //memberEntity 의 nickname
	
	@Column(name = "replycontent", length = 1000, nullable = false)
	private String replycontent;
	
	@Column(name = "replyregdate", nullable = false)
	private LocalDateTime replyregdate;

	
	
	/*
	//FK 만들기
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "email", nullable = false)
	private MemberEntity email;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "reqseqno", referencedColumnName = "seqno", nullable = false)
	private RequestEntity reqseqno;
	*/
	
}
