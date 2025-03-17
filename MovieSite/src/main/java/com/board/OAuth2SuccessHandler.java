package com.board;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.board.dto.MemberDTO;
import com.board.service.FavGenreService;
import com.board.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	private final MemberService memberService;
	private final FavGenreService favGenreService;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	Authentication authentication) throws IOException, ServletException {

		HttpSession session = request.getSession(true);//세션 생성
		String email = (String) session.getAttribute("email");
		MemberDTO memberDTO = memberService.memberInfo(email);
		
		// 새로운 세션 생성
		session.setMaxInactiveInterval(7200);
		session.setAttribute("email", email);
		
		
		log.debug("------------------------ OAuth2 로그인 성공 ------------------------");
		
		if(memberDTO.getLastpwdate() == null) {
			setDefaultTargetUrl("/member/modifyMemberInfoOauth"); //로그인 성공 후 이동할 경로
		}
		else{
			if(favGenreService.getFavGenre(memberDTO.getEmail()) == null || favGenreService.getFavGenre(memberDTO.getEmail()).isEmpty()){
				setDefaultTargetUrl("/member/selectGenre");
			}
			else{
				setDefaultTargetUrl("/board/list");
			}
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
