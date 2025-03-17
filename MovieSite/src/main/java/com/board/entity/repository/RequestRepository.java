package com.board.entity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.board.dto.RequestDTO;
import com.board.entity.RequestEntity;

public interface RequestRepository extends JpaRepository<RequestEntity, Long>{ // JpaRepository<xxxEntity, 기본기타입>{
	//게시물 목록 보기
	public Page<RequestEntity> findByTitleContainingOrContentContaining
		(String keyword1, String keyword2, Pageable pageable);
	
	//게시물 이전 보기 --> JPQL
	@Query("select max(r.seqno) from request r " 
			+ "where r.seqno < :seqno and "
			+ "(r.title like %:keyword1% or r.content like %:keyword2%)")
	public Long pre_seqno(@Param("seqno") Long seqno, 
			@Param("keyword1") String keyword1, 
			@Param("keyword2") String keyword2);

	//게시물 다음 보기 --> JPQL
	@Query("select min(r.seqno) from request r "
			+ "where r.seqno > :seqno and "
			+ "(r.title like %:keyword1% or r.content like %:keyword2%)")
	public Long next_seqno(@Param("seqno") Long seqno,
			@Param("keyword1") String keyword1, 
			@Param("keyword2") String keyword2);
	
	//max Seqno 구하기 --> Native SQL
	@Query(value="select max(seqno) from request where email = :email",
			nativeQuery = true)
	public Long getMaxSeqno(@Param("email") String email);

	//처리 완료 여부 확인하기
	@Query("select r.status from request r where r.seqno = :seqno")
	public String findStatusBySeqno(@Param("seqno") Long seqno);
	
	//처리 상태 업데이트하기
	@Modifying
	@Query("UPDATE request r SET r.status = :status WHERE r.seqno = :seqno")
	public void updateStatusBySeqno(@Param("seqno") Long seqno, @Param("status") String status);
	
	//요청 게시물 삭제하기
	@Modifying
	@Transactional
	@Query("delete from request r where r.seqno = :seqno")
	public void deleteBySeqno(@Param("seqno") Long seqno);
	
	//seqno 로 값 가져오기
	@Query("select r from request r where r.seqno = :seqno")
	public List<RequestDTO> findBySeqno(@Param("seqno") Long seqno);
	
	//관리자페이지 : 통계 - 처리 상태 확인하기
	@Query("select r.status, count(r) from request r group by status")
	public List<Object[]> findStatus();
	
	//내가 작성한 게시물 목록 보기
	@Query("select r from request r where r.email = :email and "
			+ "(r.title like %:keyword1% or r.content like %:keyword2%)")
	public Page<RequestEntity> findByEmailAndTitleContainingOrContentContaining
		(@Param("email") String email, @Param("keyword1") String keyword1, 
				@Param("keyword2") String keyword2, Pageable pageable);
	
	//내가 작성한 게시물 이전 보기 --> JPQL
	@Query("select max(r.seqno) from request r " 
			+ "where r.seqno < :seqno and r.email = :email and "
			+ "(r.title like %:keyword1% or r.content like %:keyword2%)")
	public Long my_pre_seqno(@Param("seqno") Long seqno, @Param("email") String email,
			@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);

	//내가 작성한 게시물 다음 보기 --> JPQL
	@Query("select min(r.seqno) from request r "
			+ "where r.seqno > :seqno and r.email = :email and "
			+ "(r.title like %:keyword1% or r.content like %:keyword2%)")
	public Long my_next_seqno(@Param("seqno") Long seqno, @Param("email") String email,
			@Param("keyword1") String keyword1, @Param("keyword2") String keyword2);

}
