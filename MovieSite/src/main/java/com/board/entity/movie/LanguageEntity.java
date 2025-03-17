package com.board.entity.movie;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

@Entity(name="language")
@Table(name="language")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LanguageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="LANGUAGE_SEQ")
	@SequenceGenerator(name="LANGUAGE_SEQ",sequenceName = "language_seq",initialValue = 1,allocationSize = 1)
	private Long seqno;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;
	
	private String englishNm;
	
	private String iso639_1;
	private String languageNm;
}
