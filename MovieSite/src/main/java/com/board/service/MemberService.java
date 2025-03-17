package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;

import com.board.dto.MemberDTO;

public interface MemberService {
	
	//회원가입
	public MemberDTO signup(MemberDTO member);
	
	//회원 정보 가져오기
	public MemberDTO memberInfo(String email);
	
	//사용자 기본 정보 수정
	public void modifyMemberInfo(MemberDTO memberDTO);

	//패스워드 수정
	public void modifyMemberPassword(MemberDTO member);
	
	//프로필 업로드
	public void modifyMemberProfile(MemberDTO email);

	//이메일 중복 확인
	public int emailCheck(String email);
	
	//닉네임 중복 확인
	public int nicknameCheck(String nickname);


	//회원 로그인/로그아웃 로그 등록
	public void memberLogRegistry(String email, String status);
	
	//마지막 로그인/로그아웃/패스워드 변경 날짜 등록 하기
	public void lastdateUpdate(String email, String status);
	
	//이메일 찾기
	public String searchEmail(MemberDTO member);
	
	//관리자페이지 - 통계 : 회원 가입한 날 확인
	public List<Object[]> signupResult();
	
    //임시 패스워드 저장하기
	public void makeTempPW(String email, String newPW);
	
	//관리자페이지 - 통계 : 최근 7일간 회원가입한 회원 수 확인
	public int newMembersInLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

	//관리자페이지 - 통계 : 각 등급 별 회원 수 확인
	public List<Object[]> gradeMemberCounts();
}
