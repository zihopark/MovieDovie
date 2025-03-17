package com.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.board.dto.MemberDTO;
import com.board.entity.MemberEntity;
import com.board.entity.MemberLogEntity;
import com.board.entity.repository.MemberLogRepository;
import com.board.entity.repository.MemberRepository;
import com.board.entity.repository.RentRepository;
import com.board.util.DataCalculate;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@AllArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {
	
	private final MemberRepository memberRepository;
	private final MemberLogRepository memberLogRepository;
	private final RentRepository rentRepository;
	private final NotificationService notificationService;

	//매달 1일 0시 0분 0초에 실행
	@Scheduled(cron = "0 0 0 1 * *")
	public void updateGrade() {

		List<MemberEntity> members = memberRepository.findAll();
		int idx = 0;
		for (MemberEntity member : members) {
			int totalSales = rentRepository.getMonthlySalesByEmail(member.getEmail());
			String grade = DataCalculate.calcGrade(totalSales);
			member.setGrade(grade);
			memberRepository.save(member);
			
			String content = "이번 달 등급은 " + grade + "입니다. 마이페이지에서 확인해보세요!";
			notificationService.newGradeNotiForUser(member.getEmail(), content, "grade");
			
			idx++;
			log.info("등급 업데이트 완료 : " + idx + "명");
		}

	}

	//회원 가입
	@Override
	public MemberDTO signup(MemberDTO member) {
		member.setRegdate(LocalDateTime.now());
		member.setLastpwdate(LocalDateTime.now());
		member.setPwcheck(1L);
		member.setRole("USER");
		member.setGrade(DataCalculate.calcGrade(0));	
		member.setPoint(0L);
		
		memberRepository.save(member.dtoToEntity(member));	
		return member;
	}
	
	//회원 정보 가져 오기
	@Override
	public MemberDTO memberInfo(String email) {
		return memberRepository.findById(email).map((member) -> new MemberDTO(member)).orElse(null);
	}
	
	//회원 기본 정보 수정
	public void modifyMemberInfo(MemberDTO member) {
		MemberEntity memberEntity = memberRepository.findById(member.getEmail()).get();
		
		memberEntity.updateMemberInfo(member);
		memberRepository.save(memberEntity);
	}

    // 이메일 중복 확인
    @Override
    public int emailCheck(String email) {
    	// 이메일이 존재하면 1 반환, 없으면 0 반환
        return memberRepository.existsByEmail(email) ? 1 : 0;
    }

    // 닉네임 중복 확인
    @Override
    public int nicknameCheck(String nickname) {
    	// 닉네임이 존재하면 1 반환, 없으면 0 반환
        return memberRepository.existsByNickname(nickname) ? 1 : 0;
    }

	
	//패스워드 수정
	public void modifyMemberPassword(MemberDTO member) {
		MemberEntity memberEntity = memberRepository.findById(member.getEmail()).get();
		memberEntity.setPassword(member.getPassword());
		memberRepository.save(memberEntity);	
	}
	
	
	//임시 패스워드 저장하기
	@Override	
	public void makeTempPW(String email, String newPW) {
		MemberEntity memberEntity = memberRepository.findById(email).get();
		memberEntity.setPassword(newPW);
		memberRepository.save(memberEntity);	
	}
	
	// 회원 로그인/로그아웃 로그 등록
	@Override
	public void memberLogRegistry(String email, String status) {
	    MemberEntity memberEntity = memberRepository.findById(email).orElseThrow(() -> 
	        new RuntimeException("회원 정보가 존재하지 않습니다.")
	    );

	    MemberLogEntity memberLogEntity = MemberLogEntity.builder()
	                                                     .email(memberEntity.getEmail())
	                                                     .inouttime(LocalDateTime.now())
	                                                     .status(status)
	                                                     .build();

	    memberLogRepository.save(memberLogEntity);
	}

	// 마지막 로그인/로그아웃/패스워드 변경일 등록
	@Override
	public void lastdateUpdate(String email, String status) {
	    MemberEntity memberEntity = memberRepository.findById(email).orElseThrow(() -> 
	        new RuntimeException("회원 정보가 존재하지 않습니다.")
	    );

	    MemberLogEntity memberLogEntity = null;

	    if ("login".equals(status) || "logout".equals(status)) {
	        memberLogEntity = MemberLogEntity.builder()
	                                         .email(memberEntity.getEmail())
	                                         .inouttime(LocalDateTime.now())
	                                         .status(status)
	                                         .build();
	    } else if ("password".equals(status)) {
	        memberEntity.setLastpwdate(LocalDateTime.now());
	    }

	    memberRepository.save(memberEntity);

	    if (memberLogEntity != null) {
	        memberLogRepository.save(memberLogEntity);
	    }
	}

	
	//이메일 찾기
	public String searchEmail(MemberDTO member) {
		return memberRepository.findByUsernameAndTelno(member.getUsername(),member.getTelno())
					.map(m-> m.getEmail()).orElse("ID_NOT_FOUND");
	}
	


	//관리자페이지 - 통계 : 회원 가입한 날 확인
	@Override
	public List<Object[]> signupResult(){
		return memberRepository.signupResult();
	}

	//관리자페이지 - 통계 : 최근 7일간 회원가입한 회원 수 확인
	@Override	
	public int newMembersInLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo) {
		return memberRepository.newMembersInLastSevenDays(sevenDaysAgo);
	}

	@Override
	public void modifyMemberProfile(MemberDTO member){
		MemberEntity memberEntity = memberRepository.findById(member.getEmail()).get();

		memberEntity.setFilesize(member.getFilesize());
		memberEntity.setOrg_filename(member.getOrgFilename());
		memberEntity.setStored_filename(member.getStoredFilename());

		memberRepository.save(memberEntity);
	}
	
	//관리자페이지 - 통계 : 각 등급 별 회원 수 확인
	@Override
	public List<Object[]> gradeMemberCounts(){
		return memberRepository.gradeMemberCounts();
	}
	

}
