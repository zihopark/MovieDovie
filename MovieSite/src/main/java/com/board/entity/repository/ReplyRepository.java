package com.board.entity.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.board.entity.MemberEntity;
import com.board.entity.ReplyEntity;
import com.board.entity.movie.MovieEntity;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {

	@Query(value = "SELECT * FROM (" +
            "  SELECT r.movie_id AS movieId, COUNT(r.seqno) AS count " +
            "  FROM reply r " +
            "  GROUP BY r.movie_id " +
            "  ORDER BY COUNT(r.seqno) DESC" +
            ") WHERE ROWNUM <= :topCount",
    nativeQuery = true)
	List<Map<String,Object>> findTopMoviesByReplyCount(@Param("topCount") int topCount);
	// 댓글 목록 가져오기
	@Query("select r from req_reply r where r.reqseqno = :reqseqno order by r.seqno")
	public List<ReplyEntity> replyList(@Param("reqseqno") Long reqseqno);

	public ReplyEntity findByEmailAndMovieId(MemberEntity member, MovieEntity movie);

	public List<ReplyEntity> findAllByMovieId(MovieEntity movie);
}
