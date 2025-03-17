package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;

public interface MemberLogService {

	//관리자페이지 - 통계 : 방문 수 확인
	public List<Object[]> visitResult();
	
	//관리자페이지 - 통계 : 최근 7일간 방문자 수 확인
	public int visitorsInLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, @Param("now") LocalDateTime now);
}

