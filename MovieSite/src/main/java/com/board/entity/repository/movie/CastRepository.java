package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.CastEntity;
import com.board.entity.movie.MovieEntity;

public interface CastRepository extends JpaRepository<CastEntity, Long> {
	public List<CastEntity> findByMovieId(MovieEntity entity);

    public List<CastEntity> findByCastNmContainingIgnoreCase(String keyword);
}
