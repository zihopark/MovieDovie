package com.board.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.board.dto.FavGenreDTO;
import com.board.entity.FavGenreEntity;
import com.board.entity.repository.FavGenreRepository;
import com.board.entity.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@AllArgsConstructor
@Log4j2
public class FavGenreServiceImpl implements FavGenreService {

	public final MemberRepository memberRepository;
	public final FavGenreRepository favGenreRepository;
	public final HttpSession session;
	
	//회원들이 좋아하는 장르 인기 순으로 뽑아내기
	@Override	
	public List<Object[]> favGenreResult(){
		
		List<Object[]> result = favGenreRepository.favGenreResult();
		    
	    if (result == null || result.isEmpty()) {
	        // 데이터가 없으면 빈 리스트를 반환하거나 적절한 처리를 할 수 있습니다.
	        return Collections.emptyList();  // 또는 다른 처리가 필요하면 여기에서 수행
	    }
	    
	    return result;
	    
	}

	//관리자페이지 - 통계 : 남성이 좋아하는 장르 인기 순으로 뽑아내기
	@Override
	public List<Object[]> findTopGenresForMale(){
		
		List<Object[]> result = favGenreRepository.findTopGenresForMale();
	    
	    if (result == null || result.isEmpty()) {
	        // 데이터가 없으면 빈 리스트를 반환하거나 적절한 처리를 할 수 있습니다.
	        return Collections.emptyList();  // 또는 다른 처리가 필요하면 여기에서 수행
	    }
	    
	    return result;
	}
		
	//관리자페이지 - 통계 : 여성이 좋아하는 장르 인기 순으로 뽑아내기
	@Override
	public List<Object[]> findTopGenresForFemale(){
		
		List<Object[]> result = favGenreRepository.findTopGenresForFemale();
	    
	    if (result == null || result.isEmpty()) {
	        // 데이터가 없으면 빈 리스트를 반환하거나 적절한 처리를 할 수 있습니다.
	        return Collections.emptyList();  // 또는 다른 처리가 필요하면 여기에서 수행
	    }
	    
	    return result;
	}
	
	
	@Override
	public void saveTheFavGenre(FavGenreDTO favGenre) {
		FavGenreEntity favGenreEntity = favGenreRepository.findByEmail(favGenre.getEmail());
		if(favGenreEntity == null) {
			FavGenreEntity entity = FavGenreEntity.builder()
				.email(favGenre.getEmail())
				.genre1(favGenre.getGenre1())
				.genre2(favGenre.getGenre2())
				.genre3(favGenre.getGenre3())
				.build();
			
			favGenreRepository.save(entity);
			
		}
		else {
			favGenreEntity.setGenre1(favGenre.getGenre1());
			favGenreEntity.setGenre2(favGenre.getGenre2());
			favGenreEntity.setGenre3(favGenre.getGenre3());
			
			favGenreRepository.save(favGenreEntity);
		}
		
		
	}

	@Override
	public Optional<Map<String, Object>> getFavGenre(String email) {
		Map<String,Object> data = new HashMap<>();
		FavGenreEntity genre = favGenreRepository.findByEmail(email);
		if(genre == null)return Optional.empty();
		
		data.put("genre1",genre.getGenre1());
		data.put("genre2",genre.getGenre2());
		data.put("genre3",genre.getGenre3());
		
		return Optional.of(data);
	}

}
