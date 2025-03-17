package com.board.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.board.entity.FavGenreEntity;

public interface FavGenreRepository extends JpaRepository<FavGenreEntity, Long>{

	//관리자페이지 - 통계 : 회원들이 좋아하는 장르 인기 순으로 뽑아내기
	@Query(value = "SELECT genre, COUNT(genre) AS count FROM (" +
                 "SELECT genre1 AS genre FROM fav_genre " +
                 "UNION ALL " +
                 "SELECT genre2 FROM fav_genre " +
                 "UNION ALL " +
                 "SELECT genre3 FROM fav_genre" +
                 ") genres " +
                 "GROUP BY genre " +
                 "ORDER BY count DESC", nativeQuery = true)
	public List<Object[]> favGenreResult();

	public FavGenreEntity findByEmail(String email);
	
	//관리자페이지 - 통계 : 여성이 좋아하는 장르 인기 순으로 뽑아내기
	@Query(value = "SELECT genre, COUNT(genre) AS count FROM (" +
            "SELECT fg.genre1 AS genre " +
            "FROM fav_genre fg " +
            "JOIN member m ON fg.email = m.email " +
            "WHERE m.gender = '여성' " +  // 여성만 선택
            "UNION ALL " +
            "SELECT fg.genre2 " +
            "FROM fav_genre fg " +
            "JOIN member m ON fg.email = m.email " +
            "WHERE m.gender = '여성' " +
            "UNION ALL " +
            "SELECT fg.genre3 " +
            "FROM fav_genre fg " +
            "JOIN member m ON fg.email = m.email " +
            "WHERE m.gender = '여성' " +
            ") genres " +
            "GROUP BY genre " +
            "ORDER BY count DESC", nativeQuery = true)
	public List<Object[]> findTopGenresForFemale();
	
	//관리자페이지 - 통계 : 남성이 좋아하는 장르 인기 순으로 뽑아내기
	@Query(value = "SELECT genre, COUNT(genre) AS count FROM (" +
            "SELECT fg.genre1 AS genre " +
            "FROM fav_genre fg " +
            "JOIN member m ON fg.email = m.email " +
            "WHERE m.gender = '남성' " +  // 남성만 선택
            "UNION ALL " +
            "SELECT fg.genre2 " +
            "FROM fav_genre fg " +
            "JOIN member m ON fg.email = m.email " +
            "WHERE m.gender = '남성' " +
            "UNION ALL " +
            "SELECT fg.genre3 " +
            "FROM fav_genre fg " +
            "JOIN member m ON fg.email = m.email " +
            "WHERE m.gender = '남성' " +
            ") genres " +
            "GROUP BY genre " +
            "ORDER BY count DESC", nativeQuery = true)
	public List<Object[]> findTopGenresForMale();
	
}
