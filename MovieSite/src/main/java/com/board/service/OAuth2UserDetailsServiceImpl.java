package com.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.board.dto.MemberOAuth2DTO;
import com.board.entity.MemberEntity;
import com.board.entity.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@AllArgsConstructor
@Log4j2
public class OAuth2UserDetailsServiceImpl extends DefaultOAuth2UserService {

    private final PasswordEncoder pwdEncoder;
    private final MemberRepository memberRepository;
    private final HttpSession session;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = null;
        String email = null;
        String phoneNumber = null;
        String gender = null;
        String birthdate = null;


        // 제공자별 사용자 정보 매핑
        switch (provider) {
            case "naver":
                Map<String, Object> naverResponse = oAuth2User.getAttribute("response");
                providerId = (String) naverResponse.get("id");
                email = (String) naverResponse.get("email");
                phoneNumber = (String) naverResponse.get("mobile");
                gender = (String) naverResponse.get("gender");
                birthdate = (String) naverResponse.get("birthyear") + "-" + (String) naverResponse.get("birthday");
                break;

            default: // 기본은 구글
                providerId = oAuth2User.getAttribute("sub");
                email = oAuth2User.getAttribute("email");
                gender = oAuth2User.getAttribute("gender");
                birthdate = oAuth2User.getAttribute("birthdate");
                break;
        }

        log.info("=========================================== OAuth2 로그인 단계: Provider 정보 ({})", provider);
        log.info("=========================================== OAuth2 로그인 단계: ProviderID 정보 ({})", providerId);
        log.info("=========================================== OAuth2 로그인 단계: Email 정보 ({})", email);
        log.info("=========================================== OAuth2 로그인 단계: phoneNum 정보 ({})", phoneNumber);
        log.info("=========================================== OAuth2 로그인 단계: gender 정보 ({})", gender);
        log.info("=========================================== OAuth2 로그인 단계: birthdate 정보 ({})", birthdate);

        // 세션에 사용자 정보 저장
        session.setAttribute("email", email);
        session.setAttribute("phoneNumber", phoneNumber);
        session.setAttribute("gender", gender);
        session.setAttribute("birthdate", birthdate);

        // 회원 정보 저장 또는 업데이트
        MemberEntity member = saveOrUpdateMember(email, provider, gender, phoneNumber, birthdate);

        // 권한 설정
        List<SimpleGrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(member.getRole()));

        // OAuth2User DTO 생성 및 세션 설정
        MemberOAuth2DTO memberOAuth2DTO = new MemberOAuth2DTO();
        memberOAuth2DTO.setAttribute(oAuth2User.getAttributes());
        memberOAuth2DTO.setAuthorities(grantedAuthorities);
        memberOAuth2DTO.setName(member.getUsername());

        session.setAttribute("username", member.getUsername());
        session.setAttribute("role", member.getRole());


        log.info("세션 정보 설정 완료: {}", session.getAttribute("email"));

        return memberOAuth2DTO;
    }
    private MemberEntity saveOrUpdateMember(String email, String provider, String gender, String phoneNumber, String birthdate) {
        return memberRepository.findById(email).orElseGet(() -> {
            MemberEntity member = MemberEntity.builder()
                    .email(email)
                    .username(provider.toUpperCase() + "회원")
                    .password(pwdEncoder.encode("12345")) // 임시 비밀번호 설정
                    .role("USER")
                    .regdate(LocalDateTime.now())
                    .nickname("oauth")
                    .point(0L)
                    .grade("BRONZE")
                    .gender(gender != null ? gender : "") // 사용자가 지정한 값 또는 기본값 설정
                    .telno(phoneNumber != null ? phoneNumber : "01012341234") // 사용자가 지정한 값 또는 기본값 설정
                    .birthdate(birthdate != null ? birthdate : "")
                    .build();
            
            return memberRepository.save(member);
        });
    }
}
