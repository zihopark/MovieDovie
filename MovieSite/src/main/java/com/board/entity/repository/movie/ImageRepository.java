package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.ImageEntity;
import com.board.entity.movie.MovieEntity;

public interface ImageRepository extends JpaRepository<ImageEntity, Long>{
	public List<ImageEntity> findByMovieId(MovieEntity entity);

    public List<ImageEntity> findByMovieIdAndImgType(MovieEntity movie, String type);
}
