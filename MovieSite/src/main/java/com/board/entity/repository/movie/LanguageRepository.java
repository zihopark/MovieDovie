package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.LanguageEntity;
import com.board.entity.movie.MovieEntity;

public interface LanguageRepository extends JpaRepository<LanguageEntity, Long> {
	public List<LanguageEntity> findByMovieId(MovieEntity entity);
}
