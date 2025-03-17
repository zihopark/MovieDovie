package com.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.board.dto.MemberDTO;
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
import com.board.entity.movie.WeeklyTrendEntity;
import com.board.entity.repository.movie.CastRepository;
import com.board.entity.repository.movie.CompanyRepository;
import com.board.entity.repository.movie.CountryRepository;
import com.board.entity.repository.movie.CrewRepository;
import com.board.entity.repository.movie.GenreRepository;
import com.board.entity.repository.movie.ImageRepository;
import com.board.entity.repository.movie.LanguageRepository;
import com.board.entity.repository.movie.MovieRepository;
import com.board.entity.repository.movie.VideoRepository;
import com.board.entity.repository.movie.WeeklyTrendRepository;
import com.board.util.DataCalculate;
import com.board.util.MovieMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Service
public class MovieAPIServiceImpl implements MovieAPIService {
	private final TmdbAPIServiceImpl tmdb;
	private final MovieRepository movieRepository;
	private final CompanyRepository companyRepository;
	private final CountryRepository countryRepository;
	private final GenreRepository genreRepository;
	private final CrewRepository crewRepository;
	private final CastRepository castRepository;
	private final LanguageRepository languageRepository;
	private final VideoRepository videoRepository;
	private final ImageRepository imageRepository;
	private final WeeklyTrendRepository weeklyTrendRepository;
	private final MovieMapper mapper;
	
	//매주 월요일 0시 0분 0초에 트렌드 영화 업데이트 및 저장
	@Scheduled(cron = "0 0 0 * * MON")
	@Transactional
	public void autoTrendListSave() {
		
		List<MovieDTO> trendDataList = tmdb.getTrendingMovies("week", 1, "ko-KR").block();
		List<MovieDTO> trend = new ArrayList<>();
		for(MovieDTO dto : trendDataList) {
			MovieDTO movie = tmdb.getMovieDetails(dto.getId(), "ko-KR").block();
			trend.add(movie);
		}
		
		log.info(trend.size() + "개의 트렌드 영화가 저장 및 업데이트 되었습니다.");
		saveWeeklyTrend(trend);
	}

	@Transactional
	public void saveWeeklyTrend(List<MovieDTO> list) {
		LocalDateTime now = LocalDateTime.now();
		saveTheDataBases(list);
		
		for(MovieDTO dto : list) {
			MovieEntity movie = movieRepository.findById(dto.getId()).get();
			WeeklyTrendEntity weekly = 
			weeklyTrendRepository.findByYearAndMonthAndMonthOfWeekAndMovieId(
				now.getYear(), 
				now.getMonthValue(), 
				DataCalculate.getWeekOfMonth(now), 
				movie);
			
			if(weekly == null) {
				weekly = WeeklyTrendEntity.builder()
				.year(now.getYear())
				.month(now.getMonthValue())
				.monthOfWeek(DataCalculate.getWeekOfMonth(now))
				.movieId(movie)
				.build();
			
				weeklyTrendRepository.save(weekly);
			}
		}
	}

	public List<Map<String, Object>> getWeeklyTrend(MemberDTO memberDTO) {
		LocalDateTime now = LocalDateTime.now();
		List<WeeklyTrendEntity> list = 
		weeklyTrendRepository.findByYearAndMonthAndMonthOfWeek(now.getYear(), now.getMonthValue(), DataCalculate.getWeekOfMonth(now));
		
		List<Map<String, Object>> data = new ArrayList<>();
		for(WeeklyTrendEntity w : list) {
			Map<String, Object> map = mapper.getMovieSimpleInfo(w.getMovieId());
			int age = DataCalculate.calcAge(memberDTO.getBirthdate());
			if(DataCalculate.calcIsAdult((String)map.get("certification"), age)) {
				data.add(map);
			}
		}
		return data;
	}

	//매일 대여 가격 업데이트
	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void autoUpdatePrice() {
		List<MovieEntity> list = movieRepository.findAll();
		int idx = 0;
		for(MovieEntity movie : list) {
			int price = DataCalculate.calcRentPrice(movie.getReleaseDate());
			if(price != movie.getPrice()) {
				movie.setPrice(price);
				idx++;
			}
		}
		
		log.info(idx + "개의 영화가 업데이트 되었습니다.");
		movieRepository.saveAll(list);
	}
	
	
	//DB에 저장되있는거 가져오기
	@Override
	public Map<String, Object> getMovieDetails(Long id,MemberDTO memberDTO) {
		return movieRepository.findById(id)
			.filter(movie -> DataCalculate.calcIsAdult(movie.getCertification(), DataCalculate.calcAge(memberDTO.getBirthdate())))
			.map(movie -> {
				Map<String, Object> data = new HashMap<>();
				data.put("info", movie);
				data.put("company", mapper.getCompanyList(companyRepository.findByMovieId(movie)));
				data.put("country", mapper.getCountryList(countryRepository.findByMovieId(movie)));
				data.put("cast", mapper.getCastList(castRepository.findByMovieId(movie)));
				data.put("crew", mapper.getCrewList(crewRepository.findByMovieId(movie)));
				data.put("genre", mapper.getGenreList(genreRepository.findByMovieId(movie)));
				data.put("video", mapper.getVideoList(videoRepository.findByMovieId(movie)));
				data.put("language", mapper.getLanguageList(languageRepository.findByMovieId(movie)));
				data.put("backdrops", mapper.getBackdropList(imageRepository.findByMovieId(movie)));
				data.put("posters", mapper.getPosterList(imageRepository.findByMovieId(movie)));
				return data;
			})
			.orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
	}

	@Override
	public List<GenreEntity> getGenreList(String genre){
		List<GenreEntity> genres = genreRepository.findByGenreNm(genre).stream().limit(20).toList();
		if(genres == null) return new ArrayList<>();
		
		return genres;
	}
	
	//같은 장르의 영화리스트 가져오기
	@Override
	public List<Map<String,Object>> findByGenreGetMovieList(List<GenreEntity> genres,int age,MemberDTO memberDTO){
		List<Map<String,Object>> data = new ArrayList<>();
		
		for(GenreEntity genre : genres) {
			String certification = genre.getMovieId().getCertification();
			
			if(!DataCalculate.calcIsAdult(certification, age)) {
				continue;
			}
			Map<String,Object> map = mapper.getMovieSimpleInfo(genre.getMovieId());
			map.put("info", mapper.getMovieSimpleInfo(genre.getMovieId()));
			data.add(map);
		}
		
		return data;
	}
	
	@Override
	public List<Map<String,Object>> getDirectUploadList(int minusDay,MemberDTO memberDTO){
		List<Map<String,Object>> data = new ArrayList<>();
		
		//7일전까지의 업로드 리스트 가져오기
		LocalDateTime start = LocalDateTime.now().minusDays(minusDay);
		LocalDateTime end = LocalDateTime.now();
		
		List<MovieEntity> list = movieRepository.findAllByDirectUpload(start, end);
		
		for(MovieEntity m : list) {
			Map<String,Object> map = mapper.getMovieSimpleInfo(m);
			int age = DataCalculate.calcAge(memberDTO.getBirthdate());
			if(DataCalculate.calcIsAdult((String)map.get("certification"), age)) {
				map.put("info", map);
				data.add(map);
			}
		}
		
		
		return data;
	}
	
	@Override
	@Transactional
	public void saveTheDataBases(List<MovieDTO> list) {
		list.forEach(dto -> {
			try {
				Optional<MovieEntity> existingMovie = movieRepository.findById(dto.getId());
				
				if (existingMovie.isEmpty()) {
					log.info("새로운 영화 저장: {}", dto.getTitle());
					saveTheDataBase(dto);
				} else {
					MovieEntity entity = existingMovie.get();
					entity.setOverview(dto.getOverview());
					entity.setIsAdult(dto.isAdult());
					movieRepository.save(entity);
					dto.setEntity(entity);
				}
				mapperListSave(dto);
			} catch (Exception e) {
				log.error("영화 저장 중 오류 발생: {} - {}", dto.getTitle(), e.getMessage());
			}
		});
	}

	@Override
	@Transactional
	public void saveTheDataBase(MovieDTO movie) {
		MovieEntity movieEntity = mapper.toEntity(movie);
		movie.setEntity(movieEntity);
		movieRepository.save(movieEntity);
	}

	//저장 및 업데이트
	@Transactional
	private void mapperListSave(MovieDTO dto) {
		MovieEntity movie = movieRepository.findById(dto.getId())
			.orElseThrow(() -> new RuntimeException("영화를 찾을 수 없습니다: " + dto.getId()));

		// 각 엔티티 저장 로직을 별도 메소드로 분리
		saveCompanies(dto, movie);
		saveCountries(dto, movie);
		saveGenres(dto, movie);
		saveCrew(dto, movie);
		saveCast(dto, movie);
		saveLanguages(dto, movie);
		saveVideos(dto, movie);
		saveImages(dto, movie, "poster");
		saveImages(dto, movie, "backdrop");
	}

	private void saveCompanies(MovieDTO dto, MovieEntity movie) {
		List<CompanyEntity> companies = companyRepository.findByMovieId(movie);
		if (companies.isEmpty()) {
			companies = mapper.mapProductionCompanies(dto);
			companies.forEach(company -> company.setMovieId(movie));
			companyRepository.saveAll(companies);
		}
	}

	private void saveCountries(MovieDTO dto, MovieEntity movie) {
		List<CountryEntity> countries = countryRepository.findByMovieId(movie);
		if (countries.isEmpty()) {
			countries = mapper.mapProductionCountries(dto);
			countries.forEach(country -> country.setMovieId(movie));
			countryRepository.saveAll(countries);
		}
	}

	private void saveGenres(MovieDTO dto, MovieEntity movie) {
		List<GenreEntity> genres = genreRepository.findByMovieId(movie);
		if (genres.isEmpty()) {
			genres = mapper.mapGenres(dto);
			genres.forEach(genre->genre.setMovieId(movie));
			genreRepository.saveAll(genres);
		}
	}	
	
	private void saveCrew(MovieDTO dto, MovieEntity movie) {
		List<CrewEntity> crews = crewRepository.findByMovieId(movie);
		if (crews.isEmpty()) {
			crews = mapper.mapCrews(dto);
			crews.forEach(crew -> crew.setMovieId(movie));
			crewRepository.saveAll(crews);
		}
	}	

	private void saveCast(MovieDTO dto, MovieEntity movie) {
		List<CastEntity> casts = castRepository.findByMovieId(movie);
		if (casts.isEmpty()) {
			casts = mapper.mapCasts(dto);
			casts.forEach(cast -> cast.setMovieId(movie));
			castRepository.saveAll(casts);
		}
	}		
	
	private void saveLanguages(MovieDTO dto, MovieEntity movie) {
		List<LanguageEntity> languages = languageRepository.findByMovieId(movie);
		if (languages.isEmpty()) {
			languages = mapper.mapSpokenLanguages(dto);
			languages.forEach(language -> language.setMovieId(movie));
			languageRepository.saveAll(languages);
		}
	}	
	
	private void saveVideos(MovieDTO dto, MovieEntity movie) {
		List<VideoEntity> videos = videoRepository.findByMovieId(movie);
		if (videos.isEmpty()) {
			videos = mapper.mapVideos(dto);
			videos.forEach(video -> video.setMovieId(movie));
			videoRepository.saveAll(videos);
		}
	}	

	private void saveImages(MovieDTO dto, MovieEntity movie, String type) {
		List<ImageEntity> images = imageRepository.findByMovieIdAndImgType(movie, type);
		if (images.isEmpty()) {
			images = type.equals("poster") ? 
				mapper.mapPosterImage(dto) : 
				mapper.mapBackdropImage(dto);
				images.forEach(image -> image.setMovieId(movie));
			imageRepository.saveAll(images);
		}
	}

	@Override
	public List<Map<String, Object>> findByKeywordMovieList(String keyword,MemberDTO memberDTO) {
		List<Map<String,Object>> data = new ArrayList<>();
		
		List<MovieEntity> entityList = movieRepository.findByTitleContainingIgnoreCase(keyword);
		if(entityList == null) return data;
		
		for(MovieEntity entity : entityList) {
			Map<String,Object> movie = mapper.getMovieSimpleInfo(entity);
			int age = DataCalculate.calcAge(memberDTO.getBirthdate());
			if(DataCalculate.calcIsAdult((String)movie.get("certification"), age)) {
				movie.put("genre", genreRepository.findByMovieId(entity));
				data.add(movie);
			}
		}
		
		return data;
	}
	
	//관리자페이지 : 영화 수정 - Certification 이 null 인 영화 리스트 뽑아내기
	@Override	
	public List<Object[]> findByCert(){
		
		return movieRepository.findByCert();
	}

	@Override
	@Modifying
	@Transactional
	public int clickToMovie(Long movieId) {
		MovieEntity movie = movieRepository.findById(movieId).get();
		int hitno = movie.getHitno()+1;
		
		movie.setHitno(hitno);
		movieRepository.save(movie);
		return hitno;
	}

	@Override
	public void directSave(MovieDTO movie) {
		MovieEntity movieEntity = mapper.toEntity(movie);
		movieEntity.setUploadCheck("Y");
		movieRepository.save(movieEntity);

		this.mapperListSave(movie);
	}

	@Override
	public List<Map<String, Object>> findByKeywordCrewList(String keyword,MemberDTO memberDTO) {
		List<Map<String,Object>> data = new ArrayList<>();
		
		List<CrewEntity> entityList = crewRepository.findByCrewNmContainingIgnoreCase(keyword);
		if(entityList == null) return data;
		
		return searchEntities(entityList, entity -> {
			Map<String,Object> crew = new HashMap<>();
			crew.put("id", entity.getCrewId());
			crew.put("name",entity.getCrewNm());
			crew.put("profilePath", entity.getProfilePath());
			crew.put("certification", entity.getMovieId().getCertification());
			crew.put("movie",entity.getMovieId());
			return crew;
		}, DataCalculate.calcAge(memberDTO.getBirthdate()));
	}

	@Override
	public List<Map<String, Object>> findByKeywordProductionCompanyList(String keyword,MemberDTO memberDTO) {
		List<Map<String,Object>> data = new ArrayList<>();
		
		List<CompanyEntity> entityList = companyRepository.findByCompanyNmContainingIgnoreCase(keyword);
		if(entityList == null) return data;
		
		return searchEntities(entityList, entity -> {
			Map<String,Object> company = new HashMap<>();
			company.put("id", entity.getCompanyId());
			company.put("name", entity.getCompanyNm());
			company.put("logoPath", entity.getLogoPath());
			company.put("originCountry", entity.getOriginCountry());
			company.put("certification", entity.getMovieId().getCertification());
			company.put("movie",entity.getMovieId());
			return company;
		}, DataCalculate.calcAge(memberDTO.getBirthdate()));
	}

	private <T> List<Map<String, Object>> searchEntities(
		List<T> entities, 
		Function<T, Map<String, Object>> mapper,
		int age) {
		
		if (entities == null) return new ArrayList<>();
		
		return entities.stream()
			.map(mapper)
			.filter(map -> DataCalculate.calcIsAdult((String)map.get("certification"), age))
			.collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> findByKeywordCastList(String keyword,MemberDTO memberDTO) {
		List<CastEntity> entityList = castRepository.findByCastNmContainingIgnoreCase(keyword);
		return searchEntities(entityList, entity -> {
			Map<String, Object> cast = new HashMap<>();
			cast.put("id", entity.getCastId());
			cast.put("name", entity.getCastNm());
			cast.put("character", entity.getCharacterNm());
			cast.put("profilePath", entity.getProfilePath());
			cast.put("certification", entity.getMovieId().getCertification());
			cast.put("movie", entity.getMovieId());
			return cast;
		}, DataCalculate.calcAge(memberDTO.getBirthdate()));
	}
	
}