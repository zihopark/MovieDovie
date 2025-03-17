package com.board.entity.movie;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Entity(name = "video")
@Table(name = "video")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="VIDEO_SEQ")
	@SequenceGenerator(name="VIDEO_SEQ",sequenceName = "video_seq",initialValue = 1,allocationSize = 1)
	private Long seqno;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "movieId")
	private MovieEntity movieId;

	// 비디오 ID
	@Column(name="video_id")
	private String videoId;
	// 언어 코드 (ISO 639-1)
	@JsonProperty("iso_639_1")
	private String iso639_1;
	// 국가 코드 (ISO 3166-1)
	@JsonProperty("iso_3166_1")
	private String iso3166_1;
	// 비디오 키 영상경로!
	@Column(name="video_key")
	private String videoKey;
	// 비디오 이름
	@Column(name="video_name")
	private String videoNm;
	// 비디오 사이트 (예: YouTube)
	@Column(name="video_site")
	private String videoSite;
	// 비디오 크기
	@Column(name="video_size")
	private int videoSize;
	// 비디오 유형
	@Column(name="video_type")
	private String videoType;
}
