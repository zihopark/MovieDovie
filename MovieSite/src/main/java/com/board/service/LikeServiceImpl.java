package com.board.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.board.dto.MemberDTO;
import com.board.entity.LikeEntity;
import com.board.entity.MemberEntity;
import com.board.entity.movie.MovieEntity;
import com.board.entity.repository.LikeRepository;
import com.board.entity.repository.MemberRepository;
import com.board.entity.repository.movie.MovieRepository;
import com.board.util.DataCalculate;
import com.board.util.MovieMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@AllArgsConstructor
@Log4j2
class LikeServiceImpl implements LikeService {

	private final LikeRepository likeRepository;
	private final MemberRepository memberRepository;
	private final MovieRepository movieRepository;
	private final MovieMapper mapper;

	// 좋아요 % 높은 영화 추출
	@Override
	public List<Object[]> topLikeMovie() {
		return likeRepository.likeResult();
	}

	// 싫어요 % 높은 영화 추출
	@Override
	public List<Object[]> topDislikeMovie() {
		return likeRepository.dislikeResult();
	}

	// 호불호 % 높은 영화 추출
	@Override
	public List<Object[]> topDivisiveMovie() {
		return likeRepository.divisiveResult();
	}

	@Override
	public void saveOrUpdate(String rating, Long movieId, String email, boolean isAdd) {
		MemberEntity member = memberRepository.findById(email).get();
		MovieEntity movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new RuntimeException("Movie not found"));

		String likeCheck = "N";
		String dislikeCheck = "N";
		// 새로운 값
		if (isAdd) {
			likeCheck = "like".equals(rating) ? "Y" : "N";
			dislikeCheck = "dislike".equals(rating) ? "Y" : "N";
		}

		LikeEntity likeEntity = likeRepository.findByEmailAndMovieId(member, movie);
		// 저장
		if (likeEntity == null) {
			likeEntity = LikeEntity.builder().likecheck(likeCheck).dislikecheck(dislikeCheck).email(member)
					.movieId(movie).build();
		}
		// 업데이트
		else {
			likeEntity.setLikecheck(likeCheck);
			likeEntity.setDislikecheck(dislikeCheck);
		}

		likeRepository.save(likeEntity);
		
		//todo : 삭제할지말지 정해야함
		//if(!isAdd)likeRepository.delete(likeEntity);
	}

	@Override
	public Map<String, Object> getLikeInfo(String email, Long movieId) {
		Map<String, Object> data = new HashMap<>();

		MemberEntity member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Member not found " + email));
		MovieEntity movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new RuntimeException("Movie not found " + movieId));
		LikeEntity like = likeRepository.findByEmailAndMovieId(member, movie);
		if (like == null) {
			return data;
		} else {
			data.put("like", like.getLikecheck());
			data.put("dislike", like.getDislikecheck());
			return data;
		}
	}

	@Override
	public Map<String, Object> getAllLikeInfo(Long movieId) {
		Map<String, Object> data = new HashMap<>();
		MovieEntity movie = movieRepository.findById(movieId)
				.orElseThrow(() -> new RuntimeException("Movie not found " + movieId));
		List<LikeEntity> allLike = likeRepository.findAllByMovieId(movie);

		// 좋아요 개수 계산
		long likeCount = allLike.stream().filter(like -> "Y".equals(like.getLikecheck())).count();

		// 싫어요 개수 계산
		long dislikeCount = allLike.stream().filter(like -> "Y".equals(like.getDislikecheck())).count();

		// 결과를 Map에 저장
		data.put("likeCount", likeCount);
		data.put("dislikeCount", dislikeCount);
		data.put("totalCount", allLike.size());

		return data;
	}

	@Override
	public List<Map<String, Object>> getAllLikeByEmail(String email) {
		List<Map<String, Object>> data = new ArrayList<>();

		MemberEntity memE = memberRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Member not found " + email));
		List<LikeEntity> likes = likeRepository.findAllByEmail(memE);
		for (LikeEntity like : likes) {
			if ("Y".equals(like.getLikecheck())) {
				MovieEntity movie = like.getMovieId();
				data.add(mapper.getMovieSimpleInfo(movie));
			}
		}
		return data;
	}

	@Override
	public List<Map<String, Object>> getTopList(int topCount,MemberDTO memberDTO) {
		List<Map<String,Object>> data = new ArrayList<>();
		List<Map<String,Object>> likes = likeRepository.findAllByTotal();

		int age = DataCalculate.calcAge(memberDTO.getBirthdate());
		int idx = topCount;
		for(Map<String,Object> d : likes) {
			if(idx == 0)break;

			//총회원들의 10퍼가 참여 and 평점이 70퍼 이상인 영화만 추출
			Number totalNumber = (Number) d.get("totalCount");
			int totalCount = totalNumber.intValue();
			Number likePerNumber = (Number) d.get("likePercentage");
			double likePer = likePerNumber.doubleValue();

			if(totalCount >= memberRepository.count() * 0.1 && likePer >= 70) {
				Long movieId = (Long)d.get("movieId");
				
				MovieEntity movie = movieRepository.findById(movieId).get();
				if(DataCalculate.calcIsAdult(movie.getCertification(), age)) {
					Map<String,Object> map = new HashMap<>();
					map.put("info", movie);
					map.put("totalCount", d.get("totalCount"));
					map.put("likeCount", d.get("likeCount"));
					map.put("dislikeCount", d.get("dislikeCount"));
					map.put("likePer", d.get("likePercentage"));
					map.put("dislikePer", d.get("dislikePercentage"));
					data.add(map);
					idx--;
				}
			}

		}
		
		return data;
	}
}
