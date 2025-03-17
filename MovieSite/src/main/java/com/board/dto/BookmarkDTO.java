package com.board.dto;

import com.board.entity.BookmarkEntity;
import com.board.entity.MemberEntity;
import com.board.entity.movie.MovieEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookmarkDTO {
	private Long seqno;
	private String status;
	private String email;
	private Long movieseqno;
	
	//Entity --> DTO 이동
	public BookmarkDTO(BookmarkEntity bookmarkEntity) {
		this.seqno = bookmarkEntity.getSeqno();
		this.status = bookmarkEntity.getStatus();
		this.email = bookmarkEntity.getEmail().getEmail();
		this.movieseqno = bookmarkEntity.getMovieId().getMovieId();
	}
	
	//DTO --> Entity 이동
	public BookmarkEntity dtoToEntity(BookmarkDTO dto, MemberEntity member, MovieEntity movie) {
		
		BookmarkEntity bookmarkEntity = BookmarkEntity.builder()
										.seqno(dto.getSeqno())
										.status(dto.getStatus())
										.email(member)
										.movieId(movie)
										.build();
		return bookmarkEntity;
	}

	
}
