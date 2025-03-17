package com.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.board.dto.ReplyInterface;
import com.board.dto.movie.MovieDTO;
import com.board.entity.LikeEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BoardServiceImpl implements BoardService {

	@Override
	public MovieDTO view(Long seqno) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hitno(Long seqno) throws Exception {
		// TODO Auto-generated method stub
	}


	@Override
	public LikeEntity likeCheckView(Long seqno, String email) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void likeCheckRegistry(Long seqno, String email, String likeCheck, String dislikeCheck) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void likeCheckUpdate(Long seqno, String email, String likeCheck, String dislikeCheck) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ReplyInterface> replyView(ReplyInterface reply) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replyRegistry(ReplyInterface reply) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replyUpdate(ReplyInterface reply) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replyDelete(ReplyInterface reply) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void boardLikeUpdate(MovieDTO board) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
