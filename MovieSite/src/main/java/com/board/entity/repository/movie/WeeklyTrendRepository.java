package com.board.entity.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.movie.MovieEntity;
import com.board.entity.movie.WeeklyTrendEntity;

public interface WeeklyTrendRepository extends JpaRepository<WeeklyTrendEntity, Long> {

    List<WeeklyTrendEntity> findByYearAndMonthAndMonthOfWeek(int year, int monthValue, int weekOfMonth);

	WeeklyTrendEntity findByYearAndMonthAndMonthOfWeekAndMovieId(int year, int monthValue, int weekOfMonth,
			MovieEntity movie);

}
