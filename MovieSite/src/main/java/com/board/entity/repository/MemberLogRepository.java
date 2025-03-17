package com.board.entity.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.entity.MemberLogEntity;

public interface MemberLogRepository extends JpaRepository<MemberLogEntity, Long>{

	//관리자페이지 - 통계 : 방문 수 확인
	@Query(value = """
		    SELECT 
	        COALESCE(SUM(CASE WHEN m.visit_date = TRUNC(SYSDATE) - 1 THEN 1 ELSE 0 END), 0) AS yesterdayVisitors,
	        COALESCE(SUM(CASE WHEN m.visit_date = TRUNC(SYSDATE) THEN 1 ELSE 0 END), 0) AS todayVisitors,
	        COUNT(*) AS totalVisitors
		    FROM (
		        SELECT DISTINCT email, TRUNC(CAST(inouttime AS DATE)) AS visit_date
		        FROM member_log
		        WHERE status = 'login'
		          AND TRUNC(CAST(inouttime AS DATE)) >= TRUNC(SYSDATE) - 1
		    ) m
		    WHERE m.visit_date IN (TRUNC(SYSDATE), TRUNC(SYSDATE) - 1)
			""", nativeQuery = true)
	public List<Object[]> visitResult();
	
	//관리자페이지 - 통계 : 최근 7일간 방문자 수 확인
	@Query("SELECT COUNT(DISTINCT CONCAT(ml.email, FUNCTION('TO_CHAR', ml.inouttime, 'YYYY-MM-DD'))) " +
		       "FROM member_log ml " +
		       "WHERE ml.status = 'login' " +
		       "AND ml.inouttime >= :sevenDaysAgo " +
		       "AND ml.inouttime < :now")
	public int visitorsInLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, @Param("now") LocalDateTime now);
	
}
 