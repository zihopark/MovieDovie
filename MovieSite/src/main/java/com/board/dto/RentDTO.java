package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.RentEntity;
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
public class RentDTO {
	private Long seqno;
	private LocalDateTime rentdate;
	private LocalDateTime returndate;
	private String status;
	private String email;
	private Long movieseqno;
	

	//Entity --> DTO 이동
	public RentDTO(RentEntity rentEntity) {
		this.seqno = rentEntity.getSeqno();
		this.rentdate = rentEntity.getRentdate();
		this.returndate = rentEntity.getReturndate();
		this.status = rentEntity.getStatus();
		this.movieseqno = rentEntity.getMovieId().getMovieId();
	}
		
	//DTO --> Entity 이동
	public RentEntity dtoToEntity(RentDTO dto,MovieEntity movie) {
		
		RentEntity rentEntity = RentEntity.builder()
											.seqno(dto.getSeqno())
											.rentdate(dto.getRentdate())
											.returndate(dto.getReturndate())
											.status(dto.getStatus())
											.movieId(movie)
											.build();									
		return rentEntity;
	}
	
}
