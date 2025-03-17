package com.board.entity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.entity.MemberEntity;

//각 Repository에 extends JpaRepository<entity, ID(String)> 상속 받아서 사용할 것
public interface MemberRepository extends JpaRepository<MemberEntity, String>{

	Optional<MemberEntity> findByEmail(String email);
	//쿼리문 구현
	//JPA 구문(ORM) , native sql 등
	
    // 이메일 중복 확인을 위한 메서드 (이메일이 존재하는지 확인)
    boolean existsByEmail(String email);

    // 닉네임 중복 확인을 위한 메서드 (닉네임이 존재하는지 확인)
    boolean existsByNickname(String nickname);
    

    public List<MemberEntity> findAll();
	
	public Optional<MemberEntity> findByUsernameAndTelno(String username, String telno);
	public Optional<MemberEntity> findByEmailAndUsername(String email, String username);

	
	//역할 찾기
	@Query("select m from member m where m.role = 'MASTER'")
	public List<MemberEntity> findByRole();
	
	//관리자페이지 - 통계 : 회원 가입한 날 확인
	@Query(value = "SELECT " +
	        "  COUNT(CASE WHEN TRUNC(m.regdate) = TRUNC(SYSDATE) - INTERVAL '1' DAY THEN 1 END) AS yesterdaySignup, " +
	        "  COUNT(CASE WHEN TRUNC(m.regdate) = TRUNC(SYSDATE) THEN 1 END) AS todaySignup, " +
	        "  COUNT(m.email) AS totalMember FROM member m", nativeQuery = true)
	public List<Object[]> signupResult();
	
	//관리자페이지 - 통계 : 최근 7일간 회원가입한 회원 수 확인
	@Query("SELECT COUNT(m) FROM member m WHERE m.regdate >= :sevenDaysAgo")
	public int newMembersInLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
	
	//관리자페이지 - 통계 : 각 등급 별 회원 수 확인
	@Query("SELECT m.grade as grade, COUNT(grade) FROM member m GROUP BY m.grade")
	public List<Object[]> gradeMemberCounts();
	
}
