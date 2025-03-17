package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.CrewEntity;
import com.board.entity.movie.MovieEntity;

public interface CrewRepository extends JpaRepository<CrewEntity, Long> {
	public List<CrewEntity> findByMovieId(MovieEntity entity);

    public List<CrewEntity> findByCrewNmContainingIgnoreCase(String keyword);
}