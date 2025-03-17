package com.board.entity.movie;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="cast")
@Table(name="cast")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CastEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator ="CAST_SEQ" )
	@SequenceGenerator(name="CAST_SEQ",sequenceName = "cast_seq",initialValue = 1,allocationSize = 1)
	private Long castseqno;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;
	
	@Column(name="castid")
	private Long castId;
	
	@Column(name="cast_name")
	private String castNm;
	
	private String characterNm;
	
	private String profilePath;
	
	@Column(name="cast_order")
	private Integer castOrd;
}
