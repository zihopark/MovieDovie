package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.board.dto.RequestReplyDTO;
import com.board.entity.RequestReplyEntity;
import com.board.entity.repository.RequestReplyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RequestReplyServiceImpl implements RequestReplyService{

	private final RequestReplyRepository requestReplyRepository;
	
	//시작할 때 댓글 목록 가져오기
	@Override	
	public List<RequestReplyEntity> replyList(Long reqseqno){
		return requestReplyRepository.replyList(reqseqno);
	}
	
	
	//댓글 등록
	@Override
	public void replyRegister(RequestReplyDTO requestReplyDTO) {
		
		
		RequestReplyEntity requestReplyEntity = RequestReplyEntity.builder()
																.reqseqno(requestReplyDTO.getReqseqno())
																.email(requestReplyDTO.getEmail())
																.replywriter(requestReplyDTO.getReplywriter())
																.replyregdate(LocalDateTime.now())
																.replycontent(requestReplyDTO.getReplycontent())
																.build();
		requestReplyRepository.save(requestReplyEntity);
	}
	
	
	//댓글 수정
	@Override
	public void replyUpdate(RequestReplyDTO requestReplyDTO) {
		RequestReplyEntity requestReplyEntity = requestReplyRepository.findById(requestReplyDTO.getSeqno())
																		.orElseThrow(() -> new EntityNotFoundException("Request not found"));
		requestReplyEntity.setReplycontent(requestReplyDTO.getReplycontent());
		requestReplyRepository.save(requestReplyEntity);
	}
	
	
	
	//댓글 삭제
	@Override
	public void replyDelete(Long seqno) {
		requestReplyRepository.deleteById(seqno);
	}
	
}
