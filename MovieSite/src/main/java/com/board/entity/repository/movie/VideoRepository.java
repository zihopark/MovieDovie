package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.MovieEntity;
import com.board.entity.movie.VideoEntity;

public interface VideoRepository extends JpaRepository<VideoEntity, Long>{
	public List<VideoEntity> findByMovieId(MovieEntity entity);
}
