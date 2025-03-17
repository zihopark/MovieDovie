package com.board.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.board.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>{

	//각자 회원별 알람 리스트
	@Query("select n from notification n where n.memberEntity.email = :email order by n.seqno desc")
	public List<NotificationEntity> notiForUser(@Param("email") String email);
	
	//알람 확인 시 알람 읽음 처리
	@Modifying
	@Query("update notification n set n.is_read = 'Y' where n.seqno = :seqno")
	public void updateIsRead(@Param("seqno") Long seqno);
	
	//알람 삭제하기
	@Modifying
	@Transactional
	@Query("delete from notification n where n.seqno = :seqno")
	public void deleteBySeqno(@Param("seqno") Long seqno);
		
}
