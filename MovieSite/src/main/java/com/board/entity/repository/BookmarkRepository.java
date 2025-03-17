package com.board.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.BookmarkEntity;
import com.board.entity.MemberEntity;
import com.board.entity.movie.MovieEntity;


public interface BookmarkRepository  extends JpaRepository<BookmarkEntity, Long>{
	public BookmarkEntity findByEmailAndMovieId(MemberEntity email, MovieEntity movieId);

	public List<BookmarkEntity> findAllByMovieId(MovieEntity movieId);
	
	public List<BookmarkEntity> findAllByEmail(MemberEntity member);
	
}
