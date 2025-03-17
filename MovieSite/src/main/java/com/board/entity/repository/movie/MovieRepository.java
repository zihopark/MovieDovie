package com.board.entity.repository.movie;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.entity.movie.MovieEntity;

public interface MovieRepository extends JpaRepository<MovieEntity, Long>{
	
	//게시물 목록 보기...배우이름?
	public Page<MovieEntity> findByTitleContainingOrOriginalTitleContaining(String keyword, String keyword2, Pageable pageable);
	public List<MovieEntity> findByTitleContaining(String keyword);

	public List<MovieEntity> findByTitleContainingIgnoreCase(String keyword);
	public MovieEntity findByTitle(String title);
	
	//start~endDate 까지의 회원 요청 업로드 영화리스트 가져오기
	@Query(value = "SELECT * FROM movie m " +
            "WHERE m.upload_date BETWEEN :startDate AND :endDate " +
            "AND m.upload_check = 'Y'",
     nativeQuery = true)
	List<MovieEntity> findAllByDirectUpload(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
	
	//관리자페이지 : 영화 수정 - Certification 이 null 인 영화 리스트 뽑아내기 (북마크 높은 순으로)
	@Query("SELECT m " +
		       "FROM MovieEntity m " +
		       "WHERE m.certification IS NULL " +
		       "ORDER BY m.hitno DESC")
	public List<Object[]> findByCert();


	//관리자 페이지 - 통계 : 전체 회원의 hitno 수 확인
	@Query(value = "SELECT SUM(m.hitno) FROM MovieEntity m")
	public int getTotalHitno();
	
}
