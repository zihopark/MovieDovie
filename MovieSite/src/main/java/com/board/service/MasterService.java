package com.board.service;

import org.springframework.data.domain.Page;

import com.board.dto.RequestDTO;
import com.board.entity.RequestEntity;

public interface MasterService {
	
	//구매 전환율 구하기
	public double conversionRate();
	
	//요청 게시물 목록 보기
	public Page<RequestEntity> list(int pageNum, int postNum, String keyword) throws Exception;

	//요청 게시물 내용 상세 보기
	public RequestDTO view(Long seqno);

	//요청 게시물 이전 보기
	public Long pre_seqno(Long seqno, String keyword);
	
	//요청 게시물 다음 보기
	public Long next_seqno(Long seqno, String keyword);
	
	//max seqno 구하기
	public Long getMaxSeqno(String email);
	
	//회원 요청 게시물 삭제
	public void delete(Long seqno);
	
	//처리 완료 여부 확인하기
	public String statusCheck(Long seqno);
	
	//처리 상태 업데이트하기
	public void updateStatus(Long seqno, String status);
	
	//영화 중복 체크
	public void movieCheck(String movieNm);
	
//	//Dummy Members 만들기
//	public void createDummyMembers(int count);
//	
//	//Dummy 대여현황 만들기
//	public void createDummyRents(int count);
//	
//	//Dummy 좋아요 싫어요 만들기
//	public void createDummyLikes(int count);
//	
//	//Dummy 멤버 로그인/로그아웃 현황 (member_log) 만들기
//	public void createDummyLogs(int count);
//	
//	//Dummy 시청여부 만들기
//	public void createDummyWatch(int count);
//	
//	//Dummy 즐겨찾기 만들기
//	public void createDummyBookmarks(int count);
//	
//	//Dummy 좋아하는 장르 만들기
//	public void createDummyFavGenre(int count);
}
