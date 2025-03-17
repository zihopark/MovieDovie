package com.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.board.service.NotificationService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class NotificationController {

	private final NotificationService service;
	
	/*
	//알람리스트 띄우기
	@GetMapping("/board/notification")
	public void getNotiList(HttpSession session, Model model) throws Exception{
		
		String email = (String)session.getAttribute("email");
		model.addAttribute("email", email);
		
		 // 알림 목록 가져오기 (빈 리스트일 경우 처리)
	    List<NotificationEntity> noti = service.notiForUser(email);
	    
	    // 알림이 없을 경우 빈 리스트로 설정
	    if (noti == null) {
	        noti = new ArrayList<>();
	    }
	    
	    // 알림이 있을 경우 첫 번째 알림의 seqno 가져오기
	    Long seqno = null;
	    if (!noti.isEmpty()) {
	        seqno = noti.get(0).getRequestEntity().getSeqno();
	    }

	    model.addAttribute("seqno", seqno);
	    model.addAttribute("noti", noti);  // 알림 목록을 모델에 추가

	}
	*/
	
	//알람 읽음 처리하기
	@ResponseBody
	@Transactional
	@PostMapping("/notiIsRead")
	public Map<String, String> postNotiIsRead(@RequestParam("seqno") Long seqno, HttpSession session) {
		
		Map<String, String> response = new HashMap<>();
		
		try {
	        service.updateIsRead(seqno);
	        response.put("message", "good");
			
	    } catch (Exception e) {
	        response.put("message", "bad");
	        e.printStackTrace(); 
	    }
		return response;
	}
	
	
	//알람 삭제
	@Transactional
	@ResponseBody
    @PostMapping("/deleteNoti")
    public String postDeleteNoti(@RequestParam("seqno") Long seqno) throws Exception {
        
        // 알람 삭제 처리
        service.deleteNoti(seqno);
        
        // 삭제 후 리다이렉트 혹은 적절한 화면으로 이동
        return "redirect:/board/list";  // 원래 페이지로 리다이렉트인데, 나중에 바꿀 필요가 있음.
    } 
	
	
	
}
