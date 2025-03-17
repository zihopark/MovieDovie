package com.board.service;

import java.util.List;

import com.board.entity.NotificationEntity;

public interface NotificationService {

	//각 회원들의 알람 리스트
	public List<NotificationEntity> notiForUser(String email);

	//관리자가 댓글 등록 혹은 상태변경 시 (type으로 구분), 회원에게 새로운 알람 리스트 보내기
	public List<NotificationEntity> newNotiForUser(String email, String content, String type, Long seqno);
	
	//대여 시 혹은 대여 하루 전날, 회원에게 새로운 알람 리스트 보내기
	public List<NotificationEntity> newRentNotiForUser(String email, String content, String type, Long seqno);
	
	//매달 1일 등급 관련 알람 보내기
	public List<NotificationEntity> newGradeNotiForUser(String email, String content, String type);
	
	//알람 확인 시 알람 읽음 처리
	public void updateIsRead(Long seqno);
	
	//알람 삭제하기
	public void deleteNoti(Long seqno);

}
