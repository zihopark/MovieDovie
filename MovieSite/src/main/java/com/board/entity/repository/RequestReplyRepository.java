package com.board.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.entity.RequestReplyEntity;

public interface RequestReplyRepository extends JpaRepository<RequestReplyEntity, Long>{

	//댓글 목록 가져오기
	@Query("select r from req_reply r where r.reqseqno = :reqseqno order by r.seqno")
	public List<RequestReplyEntity> replyList(@Param("reqseqno") Long reqseqno);
	
}
