package com.board.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.board.entity.BookmarkEntity;
import com.board.entity.MemberEntity;
import com.board.entity.movie.MovieEntity;
import com.board.entity.repository.BookmarkRepository;
import com.board.entity.repository.MemberRepository;
import com.board.entity.repository.movie.MovieRepository;
import com.board.util.MovieMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
	private final MemberRepository member;
	private final MovieRepository movie;
	private final BookmarkRepository bookmark;
	private final MovieMapper mapper;
	
	@Override
	public void saveOrUpdate(String action, Long movieId, String email) {
		MemberEntity memE = member.findById(email).get();
		MovieEntity movieE = movie.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found"));

		BookmarkEntity bookmarkEntity = bookmark.findByEmailAndMovieId(memE, movieE);
		// 저장
		if (bookmarkEntity == null) {
			bookmarkEntity = BookmarkEntity.builder()
					.status("add".equals(action) ? "Y" : "N")
					.email(memE)
					.movieId(movieE).build();
		}
		// 업데이트
		else {
			bookmarkEntity.setStatus("add".equals(action) ? "Y" : "N");
		}

		bookmark.save(bookmarkEntity);
		
		if("remove".equals(action))bookmark.delete(bookmarkEntity);
	}

	@Override
	public Map<String, Object> getBookmarkInfo(String email, Long movieId) {
		Map<String, Object> data = new HashMap<>();

		MemberEntity memE = member.findByEmail(email)
				.orElseThrow(()->new RuntimeException("Member not found"));
		MovieEntity movieE = movie.findById(movieId)
				.orElseThrow(() -> new RuntimeException("=======Movie not found" + movieId ));
		BookmarkEntity bookmarkE = bookmark.findByEmailAndMovieId(memE, movieE);
		if (bookmarkE == null) {
			return data;
		} else {
			data.put("status", bookmarkE.getStatus());
			return data;
		}
	}

	@Override
	public Map<String, Object> getAllBookmarkInfo(Long movieId) {
		Map<String, Object> data = new HashMap<>();
		MovieEntity movieE = movie.findById(movieId)
				.orElseThrow(() -> new RuntimeException("Movie not found"));
		List<BookmarkEntity> bookmarks = bookmark.findAllByMovieId(movieE);

		//총 시청개수
		long count = bookmarks.stream().filter(status -> "Y".equals(status.getStatus())).count();

		// 결과를 Map에 저장
		data.put("count", count);

		return data;
	}

	@Override
	public List<Map<String, Object>> getAllBookmarkByEmail(String email) {
		List<Map<String,Object>> data = new ArrayList<>();
		
		MemberEntity memE = member.findByEmail(email)
				.orElseThrow(()->new RuntimeException("Member not found"));
		
		List<BookmarkEntity> bookmarks = bookmark.findAllByEmail(memE);
		for(BookmarkEntity bookmark : bookmarks) {
			MovieEntity movie = bookmark.getMovieId();
			data.add(mapper.getMovieSimpleInfo(movie));
		}
		
		return data;
	}

	
}
