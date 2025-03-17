package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.board.entity.repository.MemberLogRepository;


import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberLogServiceImpl implements MemberLogService{

	private final MemberLogRepository memberLogRepository;
	
	//관리자페이지 - 통계 : 방문 수 확인
	@Override
	public List<Object[]> visitResult(){
		return memberLogRepository.visitResult();
	}
	
	//관리자페이지 - 통계 : 최근 7일간 방문자 수 확인
	@Override	
	public int visitorsInLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, @Param("now") LocalDateTime now) {
		return memberLogRepository.visitorsInLastSevenDays(sevenDaysAgo, now);
	}
}
