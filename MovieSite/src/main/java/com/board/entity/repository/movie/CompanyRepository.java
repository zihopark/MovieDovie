package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.CompanyEntity;
import com.board.entity.movie.MovieEntity;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
	List<CompanyEntity> findByMovieId(MovieEntity entity);

    List<CompanyEntity> findByCompanyNmContainingIgnoreCase(String keyword);
}
