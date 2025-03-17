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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="favGenre")
@Table(name="favGenre")
public class FavGenreEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAV_GENRE_SEQ")
	@SequenceGenerator(name="FAV_GENRE_SEQ", sequenceName = "fav_genre_seq", initialValue = 1, allocationSize = 1)
	private Long seqno;
	
	@Column(name = "genre1", length=50, nullable = true)
	private String genre1;
	
	@Column(name = "genre2", length=50, nullable = true)
	private String genre2;
	
	@Column(name = "genre3", length=50, nullable = true)
	private String genre3;
	
	@Column(name = "email", length=320, nullable = false)
	private String email;
	
	
}
