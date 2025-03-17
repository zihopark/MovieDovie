package com.board.dto.movie;

import java.time.LocalDate;
import java.util.List;

import com.board.entity.movie.MovieEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MovieDTO {

	// ================기본 프로퍼티=============
	private MovieEntity entity;
	// 영화 ID
	private Long id;
	// 제목
	private String title;
	// 원제
	@JsonProperty("original_title")
	private String originalTitle;
	// 태그라인
	private String tagline;
	// 줄거리
	private String overview;
	// 상영 시간 (분)
	private Integer runtime;
	// 배경 이미지 경로
	@JsonProperty("backdrop_path")
	private String backdropPath;
	// 포스터 이미지 경로
	@JsonProperty("poster_path")
	private String posterPath;
	// 원어
	@JsonProperty("original_language")
	private String originalLanguage;
	// IMDB ID
	@JsonProperty("imdb_id")
	private String imdbId;
	// 인기도 (사이트 내 인기도?)
	private double popularity;
	// 개봉일
	@JsonProperty("release_date")
	private LocalDate releaseDate;
	// 평균 평점 사이트내 회원들이 평가한 점수!
	@JsonProperty("vote_average")
	private double voteAverage;
	// 투표 수 투표한 회원 수!
	@JsonProperty("vote_count")
	private Integer voteCount;

	private String isCart;
	private String isBookmarked;
	// 제작사 목록
	@JsonProperty("production_companies")
	private List<ProductionCompany> productionCompanies;
	// 제작 국가 목록
	@JsonProperty("production_countries")
	private List<ProductionCountry> productionCountries;
	// 사용된 언어 목록
	@JsonProperty("spoken_languages")
	private List<SpokenLanguage> spokenLanguages;
	// 상태 (개봉, 제작 중 등) 개봉한 것만 가져오기 필터링!!
	private String status;
	// 비디오 여부
	private boolean video;

	public boolean getVideo() {
		return this.video;
	}

	// 소속 컬렉션 정보
	@JsonProperty("belongs_to_collection")
	private CollectionInfo belongsToCollection;
	// 장르 목록
	private List<Genre> genres;

	// =============================================

	// 성인 영화 여부 X
	private boolean adult;
	// 예산 X
	private long budget;
	// 홈페이지 URL X
	private String homepage;
	// 수익 X
	private long revenue;
	// 키워드 정보 X
	@JsonProperty("keywords")
	private Keywords keywords;

	@Data
	public static class Keywords {
		// 키워드 목록
		private List<Keyword> keywords;
	}

	@Data
	public static class Keyword {
		// 키워드 ID
		private Long id;
		// 키워드 이름
		private String name;
	}

	// 비디오 정보
	@JsonProperty("videos")
	private Videos videos;

	@Data
	public static class Videos {
		// 비디오 목록
		private List<Video> results;
	}

	@Data
	public static class Video {
		// 비디오 ID
		private String id;
		// 언어 코드 (ISO 639-1)
		@JsonProperty("iso_639_1")
		private String iso639_1;
		// 국가 코드 (ISO 3166-1)
		@JsonProperty("iso_3166_1")
		private String iso3166_1;
		// 비디오 키 영상경로!
		private String key;
		// 비디오 이름
		private String name;
		// 비디오 사이트 (예: YouTube)
		private String site;
		// 비디오 크기
		private int size;
		// 비디오 유형
		private String type;
	}

	// 이미지 정보
	@JsonProperty("images")
	private Images images;

	@Data
	public static class Images {
		// 배경 이미지 목록
		private List<Image> backdrops;
		// 포스터 이미지 목록
		private List<Image> posters;
	}

	// 영화 개봉 날짜 정보를 포함하는 클래스
	@JsonProperty("release_dates")
	private ReleaseDates releaseDates;

	@Data
	public static class ReleaseDates {
		// 각 국가별 개봉 정보 리스트
		private List<ReleaseDateInfo> results;
	}

	@Data
	public static class ReleaseDateInfo {
		// 국가 코드 (ISO 3166-1 형식, 예: "KR" for 한국)
		@JsonProperty("iso_3166_1")
		private String iso31661;

		// 해당 국가의 개봉 날짜 정보 리스트
		@JsonProperty("release_dates")
		private List<ReleaseDate> releaseDates;
	}

	@Data
	public static class ReleaseDate {
		// 영화 관람 등급 (예: "15세 관람가")
		private String certification;

		// 개봉 유형 (예: "Theatrical", "Digital")
		private String type;

		// 실제 개봉 날짜
		@JsonProperty("release_date")
		private String releaseDate;
		// 기타 필요한 필드들...
	}

	// 출연진 및 제작진 정보
	@JsonProperty("credits")
	private Credits credits;

	@Data
	public static class Credits {
		// 출연진 목록
		private List<Cast> cast;
		// 제작진 목록
		private List<Crew> crew;
	}

	// 리뷰 목록
	private List<Review> reviews;
	// 유사한 영화 목록
	@JsonProperty("similar_movies")
	private List<MovieBrief> similarMovies;
	// 추천 영화 목록
	@JsonProperty("recommended_movies")
	private List<MovieBrief> recommendedMovies;

	@Data
	public static class CollectionInfo {
		// 컬렉션 ID
		private Long id;
		// 컬렉션 이름
		private String name;
		// 컬렉션 포스터 경로
		@JsonProperty("poster_path")
		private String posterPath;
		// 컬렉션 배경 이미지 경로
		@JsonProperty("backdrop_path")
		private String backdropPath;
	}

	@Data
	public static class Genre {
		// 장르 ID
		private Long id;
		// 장르 이름
		private String name;
	}

	@Data
	public static class ProductionCompany {
		// 제작사 ID
		private Long id;
		// 제작사 로고 경로
		@JsonProperty("logo_path")
		private String logoPath;
		// 제작사 이름
		private String name;
		// 제작사 국가
		@JsonProperty("origin_country")
		private String originCountry;
	}

	@Data
	public static class ProductionCountry {
		// 국가 코드 (ISO 3166-1)
		@JsonProperty("iso_3166_1")
		private String iso3166_1;
		// 국가 이름
		private String name;
	}

	@Data
	public static class SpokenLanguage {
		// 언어 영어 이름
		@JsonProperty("english_name")
		private String englishName;
		// 언어 코드 (ISO 639-1)
		@JsonProperty("iso_639_1")
		private String iso639_1;
		// 언어 이름
		private String name;
	}

	@Data
	public static class Image {
		// 이미지 종횡비
		@JsonProperty("aspect_ratio")
		private double aspectRatio;
		// 이미지 파일 경로
		@JsonProperty("file_path")
		private String filePath;
		// 이미지 높이
		private int height;
		// 언어 코드 (ISO 639-1)
		@JsonProperty("iso_639_1")
		private String iso639_1;
		// 이미지 평균 평점
		@JsonProperty("vote_average")
		private double voteAverage;
		// 이미지 투표 수
		@JsonProperty("vote_count")
		private int voteCount;
		// 이미지 너비
		private int width;
	}

	@Data
	public static class Cast {
		// 배우 ID
		private Long id;
		// 배우 이름
		private String name;
		// 배역 이름
		@JsonProperty("character")
		private String characterName;
		// 배우 프로필 이미지 경로
		@JsonProperty("profile_path")
		private String profilePath;
		// 출연 순서
		private int order;
	}

	@Data
	public static class Crew {
		// 제작진 ID
		private Long id;
		// 제작진 이름
		private String name;
		// 직책
		@JsonProperty("job")
		private String jobTitle;
		// 부서
		private String department;
		// 제작진 프로필 이미지 경로
		@JsonProperty("profile_path")
		private String profilePath;
	}

	@Data
	public static class Review {
		// 리뷰 ID
		private String id;
		// 작성자
		private String author;
		// 리뷰 내용
		private String content;
		// 리뷰 URL
		private String url;
	}

	@Data
	public static class MovieBrief {
		// 영화 ID
		private Long id;
		// 영화 제목
		private String title;
		// 포스터 경로
		@JsonProperty("poster_path")
		private String posterPath;
		// 개봉일
		@JsonProperty("release_date")
		private LocalDate releaseDate;
	}
	
	

}