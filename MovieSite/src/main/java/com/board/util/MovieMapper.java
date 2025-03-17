package com.board.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.board.dto.movie.MovieDTO;
import com.board.entity.movie.CastEntity;
import com.board.entity.movie.CompanyEntity;
import com.board.entity.movie.CountryEntity;
import com.board.entity.movie.CrewEntity;
import com.board.entity.movie.GenreEntity;
import com.board.entity.movie.ImageEntity;
import com.board.entity.movie.LanguageEntity;
import com.board.entity.movie.MovieEntity;
import com.board.entity.movie.VideoEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MovieMapper {
	
	public Map<String,Object> getMovieSimpleInfo(MovieEntity movie){
		Map<String,Object> data= new HashMap<>();
		
		data.put("id", movie.getMovieId());
		data.put("title", movie.getTitle());
		data.put("overview", movie.getOverview());
		data.put("runtime", movie.getRuntime());
		data.put("posterPath", movie.getPosterPath());
		data.put("releaseDate", movie.getReleaseDate());
		data.put("certification", movie.getCertification());
		data.put("backdropPath", movie.getBackdropPath());
		
		return data;
	}
	
	public List<Map<String, Object>> getCompanyList(List<CompanyEntity> companies) {
	    if (companies == null || companies.isEmpty()) return new ArrayList<>();
	    return companies.stream()
	            .map(this::toMapEntity)
	            .collect(Collectors.toList());
	}

	private Map<String, Object> toMapEntity(CompanyEntity company) {
	    Map<String, Object> c = new HashMap<>();
	    c.put("name", company.getCompanyNm());
	    c.put("logoPath", company.getLogoPath());
	    c.put("originCountry", company.getOriginCountry());
	    return c;
	}
	
	public List<Map<String,Object>> getCountryList(List<CountryEntity> countries){
		if(countries == null || countries.isEmpty())return new ArrayList<>();
		return countries.stream()
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(CountryEntity country){
		Map<String,Object> data = new HashMap<>();
		
		data.put("iso3166_1", country.getIso3166_1());
		data.put("name", country.getCountryNm());
		
		return data;
	}
	
	public List<Map<String,Object>> getGenreList(List<GenreEntity> genres){
		if(genres == null || genres.isEmpty())return new ArrayList<>();
		return genres.stream()
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(GenreEntity genre){
		Map<String,Object> data = new HashMap<>();
		
		data.put("id", genre.getMovieId());
		data.put("name", genre.getGenreNm());
		
		return data;
	}
	
	public List<Map<String,Object>> getCrewList(List<CrewEntity> crews){
		if(crews == null || crews.isEmpty())return new ArrayList<>();
		return crews.stream()
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(CrewEntity crew){
		Map<String,Object> data = new HashMap<>();
		
		data.put("id",crew.getCrewId());
		data.put("name", crew.getCrewNm());
		data.put("jobTitle", crew.getJobTitle());
		data.put("department", crew.getDepartment());
		data.put("profilePath", crew.getProfilePath());
		
		return data;
	}
	
	public List<Map<String,Object>> getCastList(List<CastEntity> casts){
		if(casts == null || casts.isEmpty())return new ArrayList<>();
		return casts.stream()
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(CastEntity cast){
		Map<String,Object> data = new HashMap<>();
		
		data.put("id", cast.getCastId());
		data.put("name", cast.getCastNm());
		data.put("characterName", cast.getCharacterNm());
		data.put("profilePath", cast.getProfilePath());
		data.put("order", cast.getCastOrd());
		
		return data;
	}
	
	public List<Map<String,Object>> getLanguageList(List<LanguageEntity> languages){
		if(languages == null || languages.isEmpty())return new ArrayList<>();
		return languages.stream()
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(LanguageEntity language){
		Map<String,Object> data = new HashMap<>();
		
		data.put("englishNm", language.getEnglishNm());
		data.put("iso639_1", language.getIso639_1());
		data.put("languageNm", language.getLanguageNm());
		
		return data;
	}
	
	public List<Map<String,Object>> getVideoList(List<VideoEntity> videos){
		if(videos == null || videos.isEmpty())return new ArrayList<>();
		return videos.stream()
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(VideoEntity video){
		Map<String,Object> data = new HashMap<>();
		
		data.put("id", video.getVideoId());
		data.put("iso_639_1", video.getIso639_1());
		data.put("iso_3166_1", video.getIso3166_1());
		data.put("key", video.getVideoKey());
		data.put("name", video.getVideoNm());
		data.put("site", video.getVideoSite());
		data.put("size", video.getVideoSize());
		data.put("type", video.getVideoType());
		
		return data;
	}
	
	public List<Map<String,Object>> getBackdropList(List<ImageEntity> backdrops){
		if(backdrops == null || backdrops.isEmpty())return new ArrayList<>();
		return backdrops.stream()
				.filter(backdrop -> "backdrop".equals(backdrop.getImgType()))
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	public List<Map<String,Object>> getPosterList(List<ImageEntity> posters){
		if(posters == null || posters.isEmpty())return new ArrayList<>();
		return posters.stream()
				.filter(backdrop -> "poster".equals(backdrop.getImgType()))
				.map(this::toMapEntity)
				.collect(Collectors.toList());
	}
	
	private Map<String,Object> toMapEntity(ImageEntity images){
		Map<String,Object> data = new HashMap<>();

		data.put("aspect_ratio", images.getAspectRatio());
		data.put("filePath", images.getFilePath());
		data.put("iso_639_1", images.getIso639_1());
		data.put("voteAverage", images.getVoteAverage());
		data.put("voteCount", images.getVoteAverage());
		data.put("width", images.getImgWidth());
		data.put("height", images.getImgHeight());
		data.put("type", images.getImgType());
		
		return data;
	}
	
    public List<MovieEntity> toEntityList(List<MovieDTO> dtoList) {
        return dtoList.stream()
        		.filter(movie -> "released".equals(movie.getStatus()))
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public MovieEntity toEntity(MovieDTO dto) {
        if (dto == null) {
            return null;
        }
        MovieEntity entity = MovieEntity.builder()
                .movieId(dto.getId())
                .isAdult(dto.isAdult())
                .title(dto.getTitle())
                .originalTitle(dto.getOriginalTitle())
                .tagline(dto.getTagline())
                .overview(dto.getOverview())
                .runtime(dto.getRuntime())
                .backdropPath(dto.getBackdropPath())
                .posterPath(dto.getPosterPath())
                .originalLanguage(dto.getOriginalLanguage())
                .imdbId(dto.getImdbId())
                .popularity(dto.getPopularity())
                .releaseDate(dto.getReleaseDate())
                .voteAverage(dto.getVoteAverage())
                .voteCount(dto.getVoteCount())
                .status(dto.getStatus())
                .uploadCheck("N")
                .uploadDate(LocalDateTime.now())
                .certification(getCertification(dto.getReleaseDates()))
                .price(DataCalculate.calcRentPrice(dto.getReleaseDate()))
                .build();
        
        return entity;
    }
    
   
    public List<CompanyEntity> mapProductionCompanies(MovieDTO dto) {
        if (dto.getProductionCompanies() == null) return new ArrayList<>();
        
        return dto.getProductionCompanies().stream()
                .map(company -> CompanyEntity.builder()
                    .movieId(dto.getEntity())
                    .companyId(company.getId())
                    .logoPath(company.getLogoPath())
                    .companyNm(company.getName())
                    .originCountry(company.getOriginCountry())
                    .build())
                .collect(Collectors.toList());
        	
    }
    
//    private List<CompanyEntity> mapProductionCompanies(List<MovieDTO.ProductionCompany> dtos) {
//        if (dtos == null) return null;
//        return dtos.stream().map(dto -> {
//        	return CompanyEntity.builder()
//        			.companyId(dto.getId())
//        			.logoPath(dto.getLogoPath())
//        			.companyNm(dto.getName())
//        			.build();
//        	
//        }).collect(Collectors.toList());
//    }

    public List<CountryEntity> mapProductionCountries(MovieDTO dto) {
        if (dto.getProductionCountries() == null) return new ArrayList<>();
        return dto.getProductionCountries().stream().map(country -> {
        	return CountryEntity.builder()
        			.movieId(dto.getEntity())
        			.iso3166_1(country.getIso3166_1())
        			.countryNm(country.getName())
        			.build();
        	
        }).collect(Collectors.toList());
    }

    public List<LanguageEntity> mapSpokenLanguages(MovieDTO dto) {
        if (dto.getSpokenLanguages() == null) return new ArrayList<>();
        return dto.getSpokenLanguages().stream().map(language -> {
        	return LanguageEntity.builder()
        			.movieId(dto.getEntity())
        			.englishNm(language.getEnglishName())
        			.iso639_1(language.getIso639_1())
        			.languageNm(language.getName())
        			.build();
        }).collect(Collectors.toList());
    }


    public List<GenreEntity> mapGenres(MovieDTO dto) {
        if (dto.getGenres() == null) return new ArrayList<>();
        return dto.getGenres().stream().map(genre -> {
        	return GenreEntity.builder()
        			.movieId(dto.getEntity())
        			.genreid(genre.getId())
        			.genreNm(genre.getName())
        			.build();
        }).collect(Collectors.toList());
    }

    public List<VideoEntity> mapVideos(MovieDTO dto) {
        if (dto.getVideos() == null) return null;
        return dto.getVideos().getResults().stream().limit(1).
        		map(video->{
        	return VideoEntity.builder()
        			.movieId(dto.getEntity())
        			.videoId(video.getId())
        			.iso3166_1(video.getIso3166_1())
        			.iso639_1(video.getIso639_1())
        			.videoKey(video.getKey())
        			.videoNm(video.getName())
        			.videoSite(video.getSite())
        			.videoSize(video.getSize())
        			.videoType(video.getType())
        			.build();
        }).collect(Collectors.toList());
    }

   

    private String getCertification(MovieDTO.ReleaseDates dto) {
        if (dto == null) return "";
        String certi = "";
        
        for (MovieDTO.ReleaseDateInfo info : dto.getResults()) {
            if (info.getIso31661().equals("KR")) {
                if (!info.getReleaseDates().isEmpty()) {
                    certi = info.getReleaseDates().get(0).getCertification();
                    break;
                }
            }
        }
        return certi;
    }
    
//    public CreditEntity mapCredits(MovieDTO dto) {
//        if (dto.getCredits() == null) return null;
//        CreditEntity entity = new CreditEntity();
//        entity.setMovieId(dto.getEntity());
//        
//        dto.getCredits().setCredit(entity);
//        
//        return entity;
//    }
    
    public List<CrewEntity> mapCrews(MovieDTO dto) {
    	if(dto.getCredits().getCrew() == null)return new ArrayList<>();
    	
    	return dto.getCredits().getCrew().stream()
    	        .filter(crew -> "Director".equals(crew.getJobTitle()))
    	        .map(crew -> CrewEntity.builder()
    	            .movieId(dto.getEntity())
    	            .crewId(crew.getId())
    	            .crewNm(crew.getName())
    	            .jobTitle(crew.getJobTitle())
    	            .department(crew.getDepartment())
    	            .profilePath(crew.getProfilePath())
    	            .build())
    	        .collect(Collectors.toList());
    }
    
    public List<CastEntity> mapCasts(MovieDTO dto) {
        if (dto.getCredits().getCast() == null)return new ArrayList<>();
        
        return dto.getCredits().getCast().stream()
            .filter(cast -> cast.getOrder() < 10)
            .map(cast -> CastEntity.builder()
                .movieId(dto.getEntity())
                .castId(cast.getId())
                .castNm(cast.getName())
                .characterNm(cast.getCharacterName())
                .profilePath(cast.getProfilePath())
                .castOrd(cast.getOrder())
                .build())
            .collect(Collectors.toList());
    }

    public List<ImageEntity> mapBackdropImage(MovieDTO dto){
    	
    	if(dto.getImages() == null)return new ArrayList<>();
    	
    	return dto.getImages().getBackdrops().stream().limit(10)
    			.map(backdrop -> { return ImageEntity.builder()
    				.movieId(dto.getEntity())
    				.aspectRatio(backdrop.getAspectRatio())
    				.filePath(backdrop.getFilePath())
    				.iso639_1(backdrop.getIso639_1())
    				.voteAverage(backdrop.getVoteAverage())
    				.voteCount(backdrop.getVoteCount())
    				.imgWidth(backdrop.getWidth())
    				.imgHeight(backdrop.getHeight())
    				.imgType("backdrop")
    				.build();
    			}).collect(Collectors.toList());
    }
    public List<ImageEntity> mapPosterImage(MovieDTO dto){
    	if(dto.getImages() == null)return new ArrayList<>();
    	
    	return dto.getImages().getPosters().stream().limit(10)
    			.map(poster -> { return ImageEntity.builder()
    				.movieId(dto.getEntity())
    				.aspectRatio(poster.getAspectRatio())
    				.filePath(poster.getFilePath())
    				.iso639_1(poster.getIso639_1())
    				.voteAverage(poster.getVoteAverage())
    				.voteCount(poster.getVoteCount())
    				.imgWidth(poster.getWidth())
    				.imgHeight(poster.getHeight())
    				.imgType("poster")
    				.build();
    			}).collect(Collectors.toList());
    }
}