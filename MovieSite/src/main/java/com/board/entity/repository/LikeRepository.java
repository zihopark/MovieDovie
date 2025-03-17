package com.board.entity.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.board.entity.LikeEntity;
import com.board.entity.MemberEntity;
import com.board.entity.movie.MovieEntity;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long>{

	@Query(value = "SELECT * FROM (" +
            "  SELECT r.movie_id AS movieId, COUNT(r.seqno) AS count " +
            "  FROM reply r " +
            "  GROUP BY r.movie_id " +
            "  ORDER BY COUNT(r.seqno) DESC" +
            ") WHERE ROWNUM <= :topCount",
    nativeQuery = true)
	List<Map<String,Object>> findTopMoviesByReplyCount(@Param("topCount") int topCount);
	
	
	//관리자페이지 - 통계 : 각 영화에 대해 좋아요 %가 높은 영화 순으로 나열 (좋아요 수 10개 이상부터 보여주기.)
	@Query(value = "select m.title, "
	        + "ld.movie_id, "
	        + "count(*) as totalCount, "
	        + "count(CASE WHEN ld.likecheck = 'Y' THEN 1 END) as likeCount, "
	        + "round(count(case when ld.likecheck = 'Y' THEN 1 END) * 100.0 / count(*),2) as likePercentage "
	        + "from likedislike ld "
	        + "join movie m on ld.movie_id = m.movie_id "
	        + "group by m.title, ld.movie_id "
	        + "having count(case when ld.likecheck = 'Y' then 1 end) >= 10 "
	        + "order by likePercentage DESC", nativeQuery = true)
	public List<Object[]> likeResult();
	
	
	//관리자페이지 - 통계 : 각 영화에 대해 싫어요 %가 높은 영화 순으로 나열 (싫어요 수 10개 이상부터 보여주기.)
	@Query(value = "select m.title, "
	        + "ld.movie_id, "
	        + "count(*) as totalCount, "
	        + "count(CASE WHEN ld.dislikecheck = 'Y' THEN 1 END) as dislikeCount, "
	        + "round(count(case when ld.dislikecheck = 'Y' THEN 1 END) * 100.0 / count(*),2) as dislikePercentage "
	        + "from likedislike ld "
	        + "join movie m on ld.movie_id = m.movie_id "
	        + "group by m.title, ld.movie_id "
	        + "having count(case when ld.dislikecheck = 'Y' then 1 end) >= 10 "
	        + "order by dislikePercentage DESC", nativeQuery = true)
	public List<Object[]> dislikeResult();
	
	
	//관리자페이지 - 통계 : 호불호가 높은 영화 순으로 나열 (totalCount 수 20개 이상부터 보여주기.)
	@Query(value = "select m.title, "
	        + "ld.movie_id, "
	        + "count(*) as totalCount, "
	        + "count(CASE WHEN ld.likecheck = 'Y' THEN 1 END) as likeCount, "
	        + "count(CASE WHEN ld.dislikecheck = 'Y' THEN 1 END) as dislikeCount, "
	        + "round(count(case when ld.likecheck = 'Y' THEN 1 END) * 100.0 / count(*), 0) as likePercentage, "
	        + "round(count(case when ld.dislikecheck = 'Y' THEN 1 END) * 100.0 / count(*), 0) as dislikePercentage "
	        + "from likedislike ld "
	        + "join movie m on ld.movie_id = m.movie_id "
	        + "group by m.title, ld.movie_id "
	        + "having (count(*) >= 20) "
	        + "and (count(case when ld.likecheck = 'Y' THEN 1 END) * 100.0 / count(*) <= 60) "
	        + "and (count(case when ld.dislikecheck = 'Y' THEN 1 END) * 100.0 / count(*) <= 60) "
	        + "order by totalCount DESC", nativeQuery = true)
	public List<Object[]> divisiveResult();

	@Query(value = "select  "
	        + "ld.movie_id AS movieId, "
	        + "count(*) as totalCount, "
	        + "count(CASE WHEN ld.likecheck = 'Y' THEN 1 END) as likeCount, "
	        + "count(CASE WHEN ld.dislikecheck = 'Y' THEN 1 END) as dislikeCount, "
	        + "round(count(case when ld.likecheck = 'Y' THEN 1 END) * 100.0 / count(*), 1) as likePercentage, "
	        + "round(count(case when ld.dislikecheck = 'Y' THEN 1 END) * 100.0 / count(*), 1) as dislikePercentage "
	        + "from likedislike ld "
	        + "group by ld.movie_id order by likePercentage DESC",nativeQuery=true)
	//매개변수에 topCount(int) 들어가야함
	List<Map<String,Object>> findAllByTotal();
	
	//나의 like 정보 가져오기
	public LikeEntity findByEmailAndMovieId(MemberEntity email,MovieEntity movieId);

	// movieId를 이용해 해당 영화의 모든 LikeEntity를 조회
    List<LikeEntity> findAllByMovieId(MovieEntity movieId);
    
    List<LikeEntity> findAllByEmail(MemberEntity member);

}
