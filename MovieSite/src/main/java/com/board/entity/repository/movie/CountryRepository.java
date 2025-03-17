package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.CountryEntity;
import com.board.entity.movie.MovieEntity;

public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
	public List<CountryEntity> findByMovieId(MovieEntity entity);
}
