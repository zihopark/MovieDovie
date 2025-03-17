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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="watch")
@Table(name="watch")
public class WatchEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WATCH_SEQ")
	@SequenceGenerator(name="WATCH_SEQ", sequenceName = "watch_seq", initialValue = 1, allocationSize = 1)
	private Long seqno;
	
	@Column(name = "status", length=2, nullable = true)
	private String status;
	
	@Column(name="watchdate", nullable=true)
	private LocalDateTime watchdate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="email")
	private MemberEntity email;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;
	
	
}
