package com.board.dto;

import com.board.entity.FavGenreEntity;

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
public class FavGenreDTO {
	private Long seqno;
	private String genre1;
	private String genre2;
	private String genre3;
	private String email;
	
	//Entity --> DTO 이동
	public FavGenreDTO(FavGenreEntity favGenreEntity) {
		this.seqno = favGenreEntity.getSeqno();
		this.genre1 = favGenreEntity.getGenre1();
		this.genre2 = favGenreEntity.getGenre2();
		this.genre3 = favGenreEntity.getGenre3();
		this.email = favGenreEntity.getEmail();
	}
	
	//DTO --> Entity 이동
	public FavGenreEntity dtoToEntity() {
		
		FavGenreEntity favGenreEntity = FavGenreEntity.builder()
													.seqno(this.getSeqno())
													.genre1(this.getGenre1())
													.genre2(this.getGenre2())
													.genre3(this.getGenre3())
													.email(this.getEmail())
													.build();
		return favGenreEntity;
	}
}
