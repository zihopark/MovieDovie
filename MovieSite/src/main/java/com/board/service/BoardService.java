package com.board.service;

import java.util.List;

import com.board.dto.ReplyInterface;
import com.board.dto.movie.MovieDTO;
import com.board.entity.LikeEntity;

public interface BoardService {

	//상품 상세 정보 보기
	public MovieDTO view(Long seqno) throws Exception;
	
	//게시물 조회수 증가
	public void hitno(Long seqno) throws Exception;
	
	//게시물 좋아요/싫어요 갯수 수정
	public void boardLikeUpdate(MovieDTO board) throws Exception;
	
	//좋아요/싫어요 등록여부 확인
	public LikeEntity likeCheckView(Long seqno,String email) throws Exception;
	
	//좋아요/싫어요 신규 등록
	public void likeCheckRegistry(Long seqno, String email, String likeCheck,
			String dislikeCheck) throws Exception;
	
	//좋아요/싫어요 수정
	public void likeCheckUpdate(Long seqno, String email, String likeCheck,
			String dislikeCheck) throws Exception;
	
	//댓글 목록 보기
	public List<ReplyInterface> replyView(ReplyInterface reply) throws Exception;
	
	//댓글 등록 
	public void replyRegistry(ReplyInterface reply) throws Exception;
	
	//댓글 수정
	public void replyUpdate(ReplyInterface reply) throws Exception;
	
	//댓글 삭제 
	public void replyDelete(ReplyInterface reply) throws Exception;	
	

}
