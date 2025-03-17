package com.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Entity(name="notification")
@Table(name="notification")
public class NotificationEntity {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTI_SEQ")
	@SequenceGenerator(name="NOTI_SEQ", sequenceName = "noti_seq", initialValue = 1, allocationSize = 1)
	private Long seqno;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="email") // MemberEntity의 email을 외래 키로 설정
	private MemberEntity memberEntity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="reqseqno") // RequestEntity의 seqno을 외래 키로 설정 > reqseqno 로 이름 설정
	private RequestEntity requestEntity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="rentseqno") // RentEntity의 seqno을 외래 키로 설정 > rentseqno 로 이름 설정
	private RentEntity rentEntity;
	
	@Column(name = "content", length = 1000, nullable = false)
	private String content;
	
	@Column(name="is_read", length = 2, nullable = false)
	private String is_read;
	
	@Column(name="created_at", nullable = false)
	private LocalDateTime created_at;
	
	@Column(name="notitype", length = 50, nullable = false)
	private String notitype;
	
}
