package com.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.board.dto.MemberDTO;
import com.board.entity.MemberEntity;
import com.board.entity.WatchEntity;
import com.board.entity.movie.MovieEntity;
import com.board.entity.repository.MemberRepository;
import com.board.entity.repository.WatchReopository;
import com.board.entity.repository.movie.MovieRepository;
import com.board.util.DataCalculate;
import com.board.util.MovieMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WatchedServiceImpl implements WatchedService {
	private final MemberRepository member;
	private final MovieRepository movie;
	private final WatchReopository watch;
	private final MovieMapper mapper;

	@Override
	public void saveOrUpdate(String action, Long movieId, String email) {
		
		MemberEntity memE = member.findById(email).get();
		MovieEntity movieE = movie.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

		WatchEntity watchEntity = watch.findByEmailAndMovieId(memE, movieE);
		// 저장
		if (watchEntity == null) {
			watchEntity = WatchEntity.builder().status("add".equals(action) ? "Y" : "N").watchdate(LocalDateTime.now())
					.email(memE).movieId(movieE).build();
		}
		// 업데이트
		else {
			if("remove".equals(action)) {
				watch.delete(watchEntity);
				return;
			}
			watchEntity.setStatus("add".equals(action) ? "Y" : "N");
			watchEntity.setWatchdate(LocalDateTime.now());
		}
		watch.save(watchEntity);
	}

	@Override
	public Map<String, Object> getWatchedInfo(String email, Long movieId) {
		Map<String, Object> data = new HashMap<>();

		MemberEntity memE = member.findByEmail(email)
				.orElseThrow(()->new RuntimeException("Member not found"));
		MovieEntity movieE = movie.findById(movieId)
				.orElseThrow(() -> new RuntimeException("Movie not found"));
		WatchEntity watched = watch.findByEmailAndMovieId(memE, movieE);
		if (watched == null) {
			return data;
		} else {
			data.put("status", watched.getStatus());
			data.put("date", watched.getWatchdate());
			return data;
		}
	}

	@Override
	public Map<String, Object> getAllWatchedInfo(Long movieId) {
		Map<String, Object> data = new HashMap<>();
		MovieEntity movieE = movie.findById(movieId)
				.orElseThrow(() -> new RuntimeException("Movie not found"));
		List<WatchEntity> watches = watch.findAllByMovieId(movieE);

		//총 시청개수
		long count = watches.stream().filter(watched -> "Y".equals(watched.getStatus())).count();

		// 결과를 Map에 저장
		data.put("count", count);

		return data;
	}

	@Override
	public List<Map<String, Object>> getAllWatchedByEmail(String email) {
		List<Map<String,Object>> data = new ArrayList<>();
		MemberEntity memE = member.findByEmail(email)
				.orElseThrow(()->new RuntimeException("Member not found"));

		List<WatchEntity> watches = watch.findAllByEmail(memE);
		for(WatchEntity watch : watches) {
			MovieEntity movie = watch.getMovieId();
			data.add(mapper.getMovieSimpleInfo(movie));
		}
		
		return data;
	}

	@Override
	public List<Map<String, Object>> getTopList(int topCount,MemberDTO memberDTO) {
		List<Map<String,Object>> data = new ArrayList<>();
		int age = DataCalculate.calcAge(memberDTO.getBirthdate());
		List<Map<String,Object>> topList = watch.findTopMoviesByWatchedCount(topCount);
		for(Map<String,Object> d : topList) {
			Long movieId = (Long)d.get("movieId");

			MovieEntity movieE = movie.findById(movieId).get();
			if(DataCalculate.calcIsAdult(movieE.getCertification(), age)) {
				Map<String,Object> map = new HashMap<>();
				map.put("info", movieE);
				map.put("count", d.get("count"));
				data.add(map);
			}
		}
		return data;
	}

}
