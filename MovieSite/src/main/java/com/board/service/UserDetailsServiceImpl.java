package com.board.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.board.entity.MemberEntity;
import com.board.entity.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

	private final MemberRepository memberRepository;
	private final HttpSession session;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		//username은 스프링 시큐리티가 필터로 작동하면서 로그인 요청에서 가로채온 userid임.
		MemberEntity memberInfo = memberRepository.findById(email)
						.orElseThrow(()-> new UsernameNotFoundException("이메일이 존재하지 않습니다."));
		
		
		//SimpleGrantedAuthority : 여러개의 사용자 Role값을 받는 객체
		List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
		SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(memberInfo.getRole());		
		grantedAuthorities.add(grantedAuthority);
		
		session.setAttribute("email",email);
		session.setAttribute("password",memberInfo.getPassword());
		
		User user = new User(email, memberInfo.getPassword(), grantedAuthorities); //Security에서 인증된 사용자 정보 사용
		
		return user;
	}

}
