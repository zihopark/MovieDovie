package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.entity.MemberEntity;
import com.board.entity.NotificationEntity;
import com.board.entity.RentEntity;
import com.board.entity.RequestEntity;
import com.board.entity.repository.MemberRepository;
import com.board.entity.repository.NotificationRepository;
import com.board.entity.repository.RentRepository;
import com.board.entity.repository.RequestRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final MemberRepository memberRepository;
	private final RequestRepository requestRepository;
	private final RentRepository rentRepository;
	
	
	//각 회원들의 알람 리스트
	@Override
	public List<NotificationEntity> notiForUser(String email){
		return notificationRepository.notiForUser(email);
	}
	
	
	//관리자가 댓글 등록 혹은 상태변경 시 (type으로 구분), 회원에게 새로운 알람 리스트 보내기
	@Transactional
	@Override
	public List<NotificationEntity> newNotiForUser(String email, String content, String type, Long seqno){
		
		MemberEntity memberEntity = memberRepository.findByEmail(email)
				 .orElseThrow(() -> {
				        System.out.println("회원이 존재하지 않음: " + email);  // 디버깅용 출력
				        return new IllegalArgumentException("해당 이메일을 가진 회원이 존재하지 않습니다.");
				    });
		
		RequestEntity requestEntity = requestRepository.findById(seqno)
				.orElseThrow(() -> {
			        return new IllegalArgumentException("해당 요청사항이 존재하지 않습니다.");
			    });
	
		
		
		NotificationEntity notificationEntity = NotificationEntity.builder()
																.memberEntity(memberEntity)
																.requestEntity(requestEntity)
																.rentEntity(null)
																.content(content)
																.is_read("N")
																.created_at(LocalDateTime.now())
																.notitype(type)
																.build();
		notificationRepository.save(notificationEntity);
		
		
		return notificationRepository.notiForUser(email);
	}
	
	//관리자가 댓글 등록 혹은 상태변경 시 (type으로 구분), 회원에게 새로운 알람 리스트 보내기
	@Transactional
	@Override
	public List<NotificationEntity> newRentNotiForUser(String email, String content, String type, Long seqno){
		
		MemberEntity memberEntity = memberRepository.findByEmail(email)
				 .orElseThrow(() -> {
				        System.out.println("회원이 존재하지 않음: " + email);  // 디버깅용 출력
				        return new IllegalArgumentException("해당 이메일을 가진 회원이 존재하지 않습니다.");
				    });
		
		RentEntity rentEntity = rentRepository.findById(seqno)
				.orElseThrow(() -> {
			        return new IllegalArgumentException("해당 대여가 존재하지 않습니다.");
			    });
	
		NotificationEntity notificationEntity = NotificationEntity.builder()
																.memberEntity(memberEntity)
																.rentEntity(rentEntity)
																.requestEntity(null)
																.content(content)
																.is_read("N")
																.created_at(LocalDateTime.now())
																.notitype(type)
																.build();
		notificationRepository.save(notificationEntity);
		
		return notificationRepository.notiForUser(email);
	}

	
	//매달 1일 등급 관련 알람 보내기
	@Override	
	public List<NotificationEntity> newGradeNotiForUser(String email, String content, String type){
		
		MemberEntity memberEntity = memberRepository.findByEmail(email)
				 .orElseThrow(() -> {
				        System.out.println("회원이 존재하지 않음: " + email);  // 디버깅용 출력
				        return new IllegalArgumentException("해당 이메일을 가진 회원이 존재하지 않습니다.");
				    });
		
		NotificationEntity notificationEntity = NotificationEntity.builder()
																.memberEntity(memberEntity)
																.rentEntity(null)
																.requestEntity(null)
																.content(content)
																.is_read("N")
																.created_at(LocalDateTime.now())
																.notitype(type)
																.build();
		notificationRepository.save(notificationEntity);
		
		return notificationRepository.notiForUser(email);
	}
	
	
	//알람 확인 시 알람 읽음 처리
	@Override
	public void updateIsRead(Long seqno) {
		notificationRepository.updateIsRead(seqno);
	}
	
	
	//알람 삭제하기
	@Override
	public void deleteNoti(Long seqno) {
		notificationRepository.deleteBySeqno(seqno);
	}
	
}
