package com.board.entity.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.entity.MemberEntity;
import com.board.entity.WatchEntity;
import com.board.entity.movie.MovieEntity;

public interface WatchReopository extends JpaRepository<WatchEntity, Long> {
	@Query(value = "SELECT * FROM (" +
            "  SELECT w.movie_id AS movieId, COUNT(w.seqno) AS count " +
            "  FROM watch w " +
            "  WHERE w.status = 'Y' " +
            "  GROUP BY w.movie_id " +
            "  ORDER BY COUNT(w.seqno) DESC" +
            ") WHERE ROWNUM <= :topCount",
 nativeQuery = true)
	List<Map<String,Object>> findTopMoviesByWatchedCount(@Param("topCount") int topCount);
	
	
	public WatchEntity findByEmailAndMovieId(MemberEntity email, MovieEntity movieId);

	public List<WatchEntity> findAllByMovieId(MovieEntity movieId);
	
	public List<WatchEntity> findAllByEmail(MemberEntity member);
}
