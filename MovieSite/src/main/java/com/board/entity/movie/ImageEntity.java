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

@Entity(name="image")
@Table(name="image")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="IMAGE_SEQ")
	@SequenceGenerator(name="IMAGE_SEQ",sequenceName = "image_seq",initialValue = 1,allocationSize = 1)
	private Long seqno;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;

	@Column(name="aspect_ratio")
	private double aspectRatio;
	// 이미지 파일 경로
	@Column(name="file_path")
	private String filePath;
	// 언어 코드 (ISO 639-1)
	@Column(name="iso_639_1")
	private String iso639_1;
	// 이미지 평균 평점
	@Column(name="vote_average")
	private double voteAverage;
	// 이미지 투표 수
	@Column(name="vote_count")
	private int voteCount;
	// 이미지 너비
	@Column(name="image_width")
	private int imgWidth;
	// 이미지 높이
	@Column(name="image_height")
	private int imgHeight;
	
	@Column(name="image_type")
	private String imgType;
	
}
