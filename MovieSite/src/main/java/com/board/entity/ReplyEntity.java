package com.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.board.entity.movie.MovieEntity;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="reply")
@Table(name="reply")
public class ReplyEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLY_SEQ")
	@SequenceGenerator(name="REPLY_SEQ", sequenceName = "reply_seq", initialValue = 1, allocationSize = 1)
	private Long seqno;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="email")
	private MemberEntity email;
	
	@Column(name = "reply_content", length = 1000, nullable = false)
	private String replycontent;
	
	@Column(name = "regdate", nullable = false)
	private LocalDateTime regdate;

	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;
	
}
