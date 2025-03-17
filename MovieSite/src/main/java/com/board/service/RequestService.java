package com.board.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.board.dto.RequestDTO;
import com.board.entity.RequestEntity;

public interface RequestService {

	//관리자페이지 : 통계 - 처리 상태 확인하기
	public List<Object[]> findStatus();
	
	//요청사항 등록하기
	public void registerRequest(RequestDTO dto);
	
	//나의 요청 게시물 목록 보기
	public Page<RequestEntity> mylist(int pageNum, int postNum, String keyword, String email) throws Exception;
	
	//나의 요청 게시물 이전 보기 > BoardController에 사용
	public Long my_pre_seqno(Long seqno, String email, String keyword);
	
	//나의 요청 게시물 다음 보기 > BoardController에 사용
	public Long my_next_seqno(Long seqno, String email, String keyword);
	
	//요청 사항 값 확인하기
	public List<RequestDTO> findBySeqno(Long seqno);
}
