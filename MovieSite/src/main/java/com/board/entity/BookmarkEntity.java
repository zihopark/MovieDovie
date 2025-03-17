	package com.board.entity;

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

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name="bookmark")
@Table(name="bookmark")
public class BookmarkEntity {

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOKMARK_SEQ")
	@SequenceGenerator(name="BOOKMARK_SEQ", sequenceName = "bookmark_seq", initialValue = 1, allocationSize = 1)
	private Long seqno;

	@Column(name = "status", length=2, nullable = false)
	private String status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="email")
	private MemberEntity email;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;
	
}
