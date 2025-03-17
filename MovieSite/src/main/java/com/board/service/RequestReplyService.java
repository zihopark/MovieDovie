package com.board.service;

import java.util.List;

import com.board.dto.RequestReplyDTO;
import com.board.entity.RequestReplyEntity;

public interface RequestReplyService {

	//시작할 때 댓글 목록 가져오기
	public List<RequestReplyEntity> replyList(Long reqseqno);
	
	//댓글 등록
	public void replyRegister(RequestReplyDTO requestReplyDTO);
	
	//댓글 수정
	public void replyUpdate(RequestReplyDTO requestReplyDTO);

	//댓글 삭제
	public void replyDelete(Long seqno) ;
	
}
