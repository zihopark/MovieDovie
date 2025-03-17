package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.MemberEntity;
import com.board.entity.WatchEntity;
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
public class WatchDTO {
	private Long seqno;
	private String status;
	private LocalDateTime watchdate;
	private String email;
	private Long movieseqno;
	
	//Entity --> DTO 이동
	public WatchDTO(WatchEntity watchEntity) {
		this.seqno = watchEntity.getSeqno();
		this.status = watchEntity.getStatus();
		this.watchdate = watchEntity.getWatchdate();
		this.email = watchEntity.getEmail().getEmail();
		this.movieseqno = watchEntity.getMovieId().getMovieId();
	}
		
	//DTO --> Entity 이동
	public WatchEntity dtoToEntity(WatchDTO dto,MemberEntity member, MovieEntity movie) {
		
		WatchEntity watchEntity = WatchEntity.builder()
											.seqno(dto.getSeqno())
											.status(dto.getStatus())
											.watchdate(dto.getWatchdate())
											.email(member)
											.movieId(movie)
											.build();
		return watchEntity;
	}

	
}
