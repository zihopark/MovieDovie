package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.board.dto.RequestDTO;
import com.board.entity.RequestEntity;
import com.board.entity.repository.RequestRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

	private final RequestRepository requestRepository;
	
	//관리자페이지 : 통계 - 처리 상태 확인하기
	@Override	
	public List<Object[]> findStatus(){
		return requestRepository.findStatus();
	}
	
	
	//요청사항 등록하기
	@Override	
	public void registerRequest(RequestDTO dto) {
		
		RequestEntity requestEntity = RequestEntity.builder()
												.title(dto.getTitle())
												.reqdate(LocalDateTime.now())
												.status(dto.getStatus())
												.content(dto.getContent())
												.email(dto.getEmail())
												.build();
		requestRepository.save(requestEntity);
	}
	
	//나의 요청 게시물 목록 보기 > BoardController에 사용
	@Override
	public Page<RequestEntity> mylist(int pageNum, int postNum, String keyword, String email) throws Exception {	
		
		//PageRequest는 Page와 함께 사용되는 JPA의 객체로, 페이징 기준을 설정 -> 시작점(0부터 시작), 증가분(한 화면에 보이는 행의 수), 정렬 방식(seqno를 기준으로 역순으로 정렬)
		PageRequest pageRequest = PageRequest.of(pageNum - 1, postNum, Sort.by(Direction.DESC, "seqno")); //pageNum은 1부터 시작하기 때문에 -1해줌
		return requestRepository.findByEmailAndTitleContainingOrContentContaining(email, keyword, keyword, pageRequest); //마지막에 pageRequest 넣어주면 됨
	}
	

	//나의 요청 게시물 이전 보기 > BoardController에 사용
	@Override
	public Long my_pre_seqno(Long seqno, String email, String keyword) {
		//null 이 들어올 수도 있음. 그렇기 때문에 아래처럼 써줘야해.
		//3항 연산식 > null값이 들어올 경우 0으로. 아니면 그대로.
		return requestRepository.my_pre_seqno(seqno, email, keyword, keyword)==null?0:requestRepository.my_pre_seqno(seqno, email, keyword, keyword);
	}
	
	//나의 요청 게시물 다음 보기 > BoardController에 사용
	@Override
	public Long my_next_seqno(Long seqno, String email, String keyword) {
		return requestRepository.my_next_seqno(seqno, email, keyword, keyword)==null?0:requestRepository.my_next_seqno(seqno, email, keyword, keyword);
	}
	
	//요청 사항 값 확인하기
	@Override	
	public List<RequestDTO> findBySeqno(Long seqno){
		return requestRepository.findBySeqno(seqno);
	}
	
}
