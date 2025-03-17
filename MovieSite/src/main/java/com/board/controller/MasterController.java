package com.board.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.board.dto.MemberDTO;
import com.board.dto.RequestDTO;
import com.board.dto.RequestReplyDTO;
import com.board.dto.movie.MovieDTO;
import com.board.entity.RequestEntity;
import com.board.entity.RequestReplyEntity;
import com.board.entity.movie.MovieEntity;
import com.board.entity.repository.RentRepository;
import com.board.entity.repository.movie.MovieRepository;
import com.board.service.FavGenreService;
import com.board.service.LikeService;
import com.board.service.MasterService;
import com.board.service.MemberLogService;
import com.board.service.MemberService;
import com.board.service.NotificationService;
import com.board.service.RentService;
import com.board.service.RequestReplyService;
import com.board.service.RequestService;
import com.board.util.PageUtil;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/master")
public class MasterController {

	private final MasterService service;
	private final LikeService likeService;
	private final MemberLogService memberLogService;
	private final MemberService memberService;
	private final RentService rentService;
	private final FavGenreService favGenreService;
	private final RequestService requestService;
	private final RequestReplyService requestReplyService;
	private final MovieRepository movieRepository;
	private final NotificationService notificationService;
	private final RentRepository rentRepository;

	// 회원의 요청 목록 보기
	@GetMapping("/memberRequest")
	public void getMemberRequest(Model model, @RequestParam(name = "page", defaultValue = "1") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "", required = false) String keyword) throws Exception {

		int postNum = 15; // 한 화면에 보여지는 게시물 행의 갯수
		int pageListCount = 10; // 화면 하단에 보여지는 페이지리스트의 페이지 갯수

		PageUtil page = new PageUtil();
		Page<RequestEntity> list = service.list(pageNum, postNum, keyword);
		int totalCount = (int) list.getTotalElements();

		model.addAttribute("list", list);
		model.addAttribute("listIsEmpty", list.hasContent() ? "N" : "Y");
		model.addAttribute("totalElement", totalCount);
		model.addAttribute("postNum", postNum);
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList", page.getRequestPageList(pageNum, postNum, pageListCount, totalCount, keyword));
	}

	// 회원 요청 내용 상세 보기
	@GetMapping("/memberRequestView")
	public void getMemberRequestView(Model model, @RequestParam(name = "seqno", required = false) Long seqno,
			@RequestParam("page") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "", required = false) String keyword, HttpSession session)
			throws Exception {

		// 세션 이메일 값 가져오기
		String SessionEmail = (String) session.getAttribute("email"); // 현재 로그인 중인 사람의 이메일
		model.addAttribute("email", SessionEmail);

		// 이메일로 회원 정보 가져오기
		MemberDTO memberDTO = memberService.memberInfo(SessionEmail);
		model.addAttribute("nickname", memberDTO.getNickname());

		model.addAttribute("view", service.view(seqno));
		model.addAttribute("status", service.statusCheck(seqno));
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pre_seqno", service.pre_seqno(seqno, keyword));
		model.addAttribute("next_seqno", service.next_seqno(seqno, keyword));
	}
	
	//회원 요청사항 상태 관리 -> 상태 처리 완료로 바꾸기 혹은 처리중으로 바꾸기
	@ResponseBody
	@PostMapping("/updateStatus")
	public void postUpdateStatus(@RequestBody Map<String,Object> statusCheckData, HttpSession session) throws Exception {
		
		Long seqno = ((Number) statusCheckData.get("seqno")).longValue();
		String status = (String)statusCheckData.get("status");
		
		List<RequestDTO> requestList = requestService.findBySeqno(seqno);
		
		RequestDTO requestDTO = requestList.get(0);
	    String title = requestDTO.getTitle();
	    String recieverEmail = requestDTO.getEmail();
		
		//tbl_request 테이블에 status 업데이트
		service.updateStatus(seqno, status);
		
		
		//회원에게 알람 보내기
		String content = "'" + title + "'상태가 변경되었습니다.";
		String type = "completed";

		notificationService.newNotiForUser(recieverEmail, content, type, seqno);
	}

	// 회원 닉네임 찾기
	@GetMapping("/findNickname")
	@ResponseBody
	public Map<String, String> getFindNickname(@RequestParam("email") String email) throws Exception {

		// 이메일로 회원 정보 가져오기
		MemberDTO memberDTO = memberService.memberInfo(email);

		// requestNickname을 JSON 응답으로 반환
		Map<String, String> response = new HashMap<>();
		response.put("requestNickname", memberDTO.getNickname());
		return response;
	}

	// 회원 요청 게시물 삭제
	@Transactional
	@GetMapping("/memberRequestDelete")
	public String getDelete(@RequestParam("seqno") Long seqno) throws Exception {

		// tbl_request에서 게시물을 삭제
		service.delete(seqno);
		return "redirect:/master/memberRequest?page=1";
	}

	// 댓글 처리
	@ResponseBody
	@PostMapping("/requestReply")
	public List<Map<String, Object>> postReply(@RequestBody RequestReplyDTO dto, @RequestParam("kind") String kind, HttpSession session) 
			throws Exception{
		
		List<RequestReplyEntity> replyDataList;
		
		 switch(kind) {
	        case "L":
	            replyDataList = requestReplyService.replyList(dto.getReqseqno()); // 댓글 목록 보기
	            break;
	        case "I":
	            requestReplyService.replyRegister(dto); // 댓글 등록
	            replyDataList = requestReplyService.replyList(dto.getReqseqno()); // 등록 후 댓글 목록 반환
	            
	            //댓글 등록 알람 전달
	            String masterNickname = (String)session.getAttribute("nickname");
	            String masterEmail= (String)session.getAttribute("email");
	            List<RequestDTO> requestList = requestService.findBySeqno(dto.getReqseqno());
	           
                RequestDTO requestDTO = requestList.get(0);
                String title = requestDTO.getTitle();
                String recieverEmail = requestDTO.getEmail();
                Long seqno = requestDTO.getSeqno();
            
	    		String content = masterNickname + "님이 '" + title + "'에 댓글을 달았습니다.";
	    		String type = "reply";
	    		if(!masterEmail.equals(recieverEmail)) {
	    			notificationService.newNotiForUser(recieverEmail, content, type, seqno);
	    		}
	    		
	            break;
	        case "U":
	            requestReplyService.replyUpdate(dto); // 댓글 수정
	            replyDataList = requestReplyService.replyList(dto.getReqseqno()); // 수정 후 댓글 목록 반환
	            break;
	        case "D":
	            requestReplyService.replyDelete(dto.getSeqno()); // 댓글 삭제
	            replyDataList = requestReplyService.replyList(dto.getReqseqno()); // 삭제 후 댓글 목록 반환
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid kind value: " + kind); // 잘못된 kind 값 처리
	    }
		
		 
		// RequestReplyEntity 리스트를 Map<String, Object> 리스트로 변환
		List<Map<String, Object>> result = new ArrayList<>();
		for (RequestReplyEntity replyData : replyDataList) {
			Map<String, Object> map = new HashMap<>();
			map.put("seqno", replyData.getSeqno()); // 댓글 seqno
			map.put("email", replyData.getEmail()); // 댓글 email
			map.put("reqseqno", replyData.getReqseqno()); // Request Seqno
			map.put("replywriter", replyData.getReplywriter());
			map.put("replycontent", replyData.getReplycontent());
			map.put("replyregdate", replyData.getReplyregdate());

			result.add(map);
		}

		return result;
	}

	@GetMapping("/memberStats")
	public void getMemberStats(Model model) {
		
		model.addAttribute("RentCount", rentRepository.getTotalRent());
		model.addAttribute("HitnoCount", movieRepository.getTotalHitno());
		
		Map<String, Integer> stats = rentService.calculateGradeChanges();
	    model.addAttribute("upgradeCount", stats.get("upgrade"));
	    model.addAttribute("downgradeCount", stats.get("downgrade"));
	    model.addAttribute("maintainCount", stats.get("maintain"));
	    
	    int currentYear = LocalDate.now().getYear();
		model.addAttribute("currentYear", currentYear);
	    
	}
	
	@GetMapping("/dashboard")
	public void getDashboard(Model model) {
		
		model.addAttribute("RentCount", rentRepository.getTotalRent());
		model.addAttribute("HitnoCount", movieRepository.getTotalHitno());
		
		LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
		int signup7days = memberService.newMembersInLastSevenDays(sevenDaysAgo); //최근 7일간 회원가입한 수
		model.addAttribute("signup7days", signup7days);
		
		LocalDateTime now = LocalDateTime.now();
		int visit7days = memberLogService.visitorsInLastSevenDays(sevenDaysAgo, now);
		model.addAttribute("visit7days", visit7days);
		
		model.addAttribute("sevenDaysAgo", sevenDaysAgo);
		model.addAttribute("now", now);
		
		
		List<Object[]> results = rentService.RentCount(sevenDaysAgo, now);
		int totalRentCount = 0;

		for (Object[] result : results) {
			int count = ((Number) result[1]).intValue(); // 대여 수
			totalRentCount += count;
		}
		model.addAttribute("totalRentCount", totalRentCount);

		
		List<Object[]> salesresults = rentService.Sales(sevenDaysAgo, now);
		int totalSales = 0;
		
		for (Object[] result : salesresults) {
			int sales = ((Number) result[1]).intValue(); // 매출
			totalSales += sales;
		}
		model.addAttribute("totalSales", totalSales);
		
		
		int currentYear = LocalDate.now().getYear();
		model.addAttribute("currentYear", currentYear);
	}

	// 관리자 페이지 - 통계 : 최근 일주일 간 대여 수 확인
	@GetMapping("/weeklyRent")
	@ResponseBody
	public List<Map<String, Object>> getWeeklyRent() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sevenDaysAgo = now.minusDays(7);

		List<Object[]> results = rentService.RentCount(sevenDaysAgo, now);
		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : results) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 대여 날짜
			map.put("count", result[1]); // 대여 수
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}
	
	
	// 관리자 페이지 - 통계 : 최근 일주일 간 cash 혹은 포인트 결제 금액 및 비율 확인
	@GetMapping("/weeklyPaymentType")
	@ResponseBody
	public Map<String, Double> getWeeklyPaymentType(){
		
		Calendar cal = Calendar.getInstance();
		Date endDate = cal.getTime();
		LocalDateTime endDateTime = endDate.toInstant()
		    .atZone(ZoneId.systemDefault())
		    .toLocalDateTime();

		cal.add(Calendar.DAY_OF_MONTH, -7);
		Date startDate = cal.getTime();
		LocalDateTime startDateTime = startDate.toInstant()
		    .atZone(ZoneId.systemDefault())
		    .toLocalDateTime();

		List<Object[]> result = rentService.getSalesDataForLastSevenDays(startDateTime, endDateTime);
		
	    if (result.isEmpty() || result.get(0) == null) {
	        return Collections.emptyMap();
	    }

	    Object[] data = result.get(0);
	    double cashOrNullSales = ((Number) data[0]).doubleValue();
	    double mileageSales = ((Number) data[1]).doubleValue();
	    double totalSales = ((Number) data[2]).doubleValue();

	    Map<String, Double> ratios = new HashMap<>();
	    if (totalSales > 0) {
	        ratios.put("cashSalesRatio", (cashOrNullSales / totalSales) * 100);
	        ratios.put("pointSalesRatio", (mileageSales / totalSales) * 100);
	        ratios.put("cashSales", cashOrNullSales);
	        ratios.put("pointSales", mileageSales);
	    } else {
	        ratios.put("cashSalesRatio", 0.0);
	        ratios.put("pointSalesRatio", 0.0);
	        ratios.put("cashSales", cashOrNullSales);
	        ratios.put("pointSales", mileageSales);
	    }
		    return ratios;
		
	}
	
	// 관리자 페이지 - 통계 : 최근 일주일 간 대여 수 확인
	@GetMapping("/weeklySales")
	@ResponseBody
	public List<Map<String, Object>> getWeeklySales() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sevenDaysAgo = now.minusDays(7);

		List<Object[]> results = rentService.Sales(sevenDaysAgo, now);
		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : results) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 대여 날짜
			map.put("sales", result[1]); // 매출
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}

	//관리자 페이지 - 통계 : 영화 장르별 매출 현황 YTD 
	@GetMapping("YTDSalesByGenre")
	@ResponseBody
	public List<Map<String, Object>> getYTDSalesByGenre() {
		
		List<Object[]> results = rentService.getYTDSalesByGenre();
		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : results) {
			Map<String, Object> map = new HashMap<>();
			map.put("genre", result[0]);
			map.put("sales", result[1]);
			response.add(map);
		}
		return response; // JSON 형태로 반환
	}
	

	// 관리자 페이지 - 통계 : 작년 월별 매출 확인
	@GetMapping("/MonthlySalesLastYear")
	@ResponseBody
	public List<Map<String, Object>> getMonthlySalesLastYear() {
		// 현재 연도를 가져와서 작년 연도를 계산
		int lastYear = LocalDate.now().getYear() - 1; // 작년 연도 계산

		// 작년 연도에 대한 월별 대여 수 조회
		List<Object[]> resultsLastYear = rentService.SalesByMonth(lastYear);

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : resultsLastYear) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", result[0]);
            map.put("sales", result[1] != null ? result[1] : 0);
            response.add(map);
	    }
	    

		return response; // JSON 형태로 반환
	}

	// 관리자 페이지 - 통계 : 올해 월별 매출 확인
	@GetMapping("/MonthlySalesCurrentYear")
	@ResponseBody
	public List<Map<String, Object>> getMonthlySalesCurrentYear() {

		int currentYear = LocalDate.now().getYear(); // 올해 연도 계산

		// 올해 연도에 대한 월별 대여 수 조회
		List<Object[]> resultsCurrentYear = rentService.SalesByMonth(currentYear);

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : resultsCurrentYear) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 대여 날짜
			map.put("sales", result[1] != null ? result[1] : 0); // Sales (null 처리)
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}
	
	
	
	// 관리자 페이지 - 통계 : 작년 월별 대여 수 확인
	@GetMapping("/MonthlyRentLastYear")
	@ResponseBody
	public List<Map<String, Object>> getMonthlyRentLastYear() {
		// 현재 연도를 가져와서 작년 연도를 계산
		int lastYear = LocalDate.now().getYear() - 1; // 작년 연도 계산

		// 작년 연도에 대한 월별 대여 수 조회
		List<Object[]> resultsLastYear = rentService.RentCountByMonth(lastYear);

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : resultsLastYear) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 대여 날짜
			map.put("count", result[1] != null ? result[1] : 0); // 대여 수 (null 처리)
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}

	// 관리자 페이지 - 통계 : 올해 월별 대여 수 확인
	@GetMapping("/MonthlyRentCurrentYear")
	@ResponseBody
	public List<Map<String, Object>> getMonthlyRentCurrentYear() {

		int currentYear = LocalDate.now().getYear(); // 올해 연도 계산

		// 올해 연도에 대한 월별 대여 수 조회
		List<Object[]> resultsCurrentYear = rentService.RentCountByMonth(currentYear);

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : resultsCurrentYear) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 대여 날짜
			map.put("count", result[1] != null ? result[1] : 0); // 대여 수 (null 처리)
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}

	// 관리자 페이지 - 통계 : 올해 대여 status 수 확인
	@GetMapping("/rentStatus")
	@ResponseBody
	public List<Map<String, Object>> GetRentStatus() {

		List<Object[]> rentStatus = rentService.RentStatus();
		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : rentStatus) {
			Map<String, Object> map = new HashMap<>();
			map.put("status", result[0]); // 대여 상태 (Y 혹은 N)
			map.put("count", result[1] != null ? result[1] : 0); // count
			response.add(map);
		}
		return response;
	}

	// 관리자 페이지 - 통계 : 올해 대여 TOP 회원 확인
	@GetMapping("/topRenters")
	@ResponseBody
	public List<Map<String, Object>> getTopRenters() {
		List<Object[]> list = rentRepository.findTopRenterNickname();

		List<Map<String, Object>> response = new ArrayList<>();
		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("nickname", item[0]);
			map.put("rentCount", item[1]);
			map.put("sales", item[2]);
			response.add(map);
		}
		return response; // JSON 형태로 반환
	}


	
	//관리자 페이지 - 통계 : 구매 전환율 확인
	@GetMapping("/conversionRate")
	@ResponseBody
	public Map<String, Object> getConversionRate(Model model) {
		
		Map<String, Object> map = new HashMap<>();

		model.addAttribute("conversionRate", service.conversionRate());

		map.put("conversionRate", service.conversionRate());		
		return map;
	}
	
	// 관리자 페이지 - 통계 : 회원 가입한 날 확인
	@GetMapping("/signupResult")
	@ResponseBody
	public Map<String, Integer> getSignupResult() {

		Map<String, Integer> response = new HashMap<>();
		List<Object[]> results = memberService.signupResult();

		if (!results.isEmpty()) {
			Object[] counts = results.get(0);
			response.put("yesterdaySignup", ((Number) counts[0]).intValue());
			response.put("todaySignup", ((Number) counts[1]).intValue());
			response.put("totalMember", ((Number) counts[2]).intValue());
		} else {
			response.put("yesterdaySignup", 0);
			response.put("todaySignup", 0);
			response.put("totalMember", 0);
		}
		return response;
	}

	// 관리자 페이지 - 통계 : 방문 수 확인
	@GetMapping("/visitResult")
	@ResponseBody
	public Map<String, Integer> getVisitResult() {

		Map<String, Integer> response = new HashMap<>();
		List<Object[]> results = memberLogService.visitResult();

		if (!results.isEmpty()) {
			Object[] counts = results.get(0);
			response.put("yesterdayVisitors", counts[0] != null ? ((Number) counts[0]).intValue() : 0);
			response.put("todayVisitors", counts[1] != null? ((Number) counts[1]).intValue() : 0);
			response.put("totalVisitors", counts[2] != null? ((Number) counts[2]).intValue() : 0);
		} else {
			response.put("yesterdayVisitors", 0);
			response.put("todayVisitors", 0);
			response.put("totalVisitors", 0);
		}
		return response;
	}
	
	

	// 관리자 페이지 - 통계 : 회원 요청사항 처리 상태 확인하기
	@GetMapping("/requestStatus")
	@ResponseBody
	public Map<String, Integer> getRequestStatus() {

		Map<String, Integer> response = new HashMap<>();
		List<Object[]> results = requestService.findStatus();

		if (!results.isEmpty()) {
			for (Object[] counts : results) {
				String status = (String) counts[0]; // 상태 (예: PENDING, COMPLETED)
				Integer count = ((Number) counts[1]).intValue(); // 해당 상태의 카운트

				// 상태별로 count를 Map에 추가
				response.put(status, count);
			}
		} else {
			// 상태별 카운트가 없는 경우 기본값 설정
			response.put("PENDING", 0);
			response.put("COMPLETED", 0);
		}
		return response;
	}
	

	//관리자페이지 - 통계 : 각 등급 별 회원 수 확인
	@GetMapping("/gradeCounts")
	@ResponseBody
	public List<Map<String, Object>> getGradeCounts(){
		
		List<Object[]> list = memberService.gradeMemberCounts();
		
		List<Map<String, Object>> response = new ArrayList<>();
		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("grade", item[0]);
			map.put("count", item[1]);
			response.add(map);
		}
		return response;
	}
	
	
	//관리자 페이지 - 통계 : 다음달에 승급/강등될 회원 확인
	@GetMapping("/grade-changes")
	public String showGradeChanges(Model model) {
	    Map<String, Integer> stats = rentService.calculateGradeChanges();
	    model.addAttribute("upgradeCount", stats.get("upgrade"));
	    model.addAttribute("downgradeCount", stats.get("downgrade"));
	    model.addAttribute("maintainCount", stats.get("maintain"));
	    return "grade-changes";
	}
	
	// 관리자 페이지 - 통계 : 회원들이 가장 좋아하는 장르 인기 순으로 보여주기
	@GetMapping("/topFavGenre")
	@ResponseBody
	public List<Map<String, Object>> getTopFavGenre() {

		List<Object[]> list = favGenreService.favGenreResult();

		// 만약 리스트가 비어있다면 빈 리스트를 반환하거나 기본 값을 넣어줄 수 있음
		if (list.isEmpty()) {
			Map<String, Object> defaultMap = new HashMap<>();
			defaultMap.put("genre", "없음");
			defaultMap.put("count", 0);

			List<Map<String, Object>> response = new ArrayList<>();
			response.add(defaultMap); // 기본 데이터 추가
			return response;
		}

		// 리스트에 데이터가 있으면 정상 처리
		List<Map<String, Object>> response = new ArrayList<>();
		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("genre", item[0]);
			map.put("count", item[1]);
			response.add(map);
		}
		return response;
	}
	
	// 관리자 페이지 - 통계 : 여성이 가장 좋아하는 장르 인기 순으로 보여주기
	@GetMapping("/topFavGenreForFemale")
	@ResponseBody
	public List<Map<String, Object>> getTopFavGenreForFemale() {

		List<Object[]> list = favGenreService.findTopGenresForFemale();

		// 만약 리스트가 비어있다면 빈 리스트를 반환하거나 기본 값을 넣어줄 수 있음
		if (list.isEmpty()) {
			Map<String, Object> defaultMap = new HashMap<>();
			defaultMap.put("genre", "없음");
			defaultMap.put("count", 0);

			List<Map<String, Object>> response = new ArrayList<>();
			response.add(defaultMap); // 기본 데이터 추가
			return response;
		}

		// 리스트에 데이터가 있으면 정상 처리
		List<Map<String, Object>> response = new ArrayList<>();
		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("genre", item[0]);
			map.put("count", item[1]);
			response.add(map);
		}
		return response;
	}
	
	// 관리자 페이지 - 통계 : 남성이 가장 좋아하는 장르 인기 순으로 보여주기
	@GetMapping("/topFavGenreForMale")
	@ResponseBody
	public List<Map<String, Object>> getTopFavGenreForMale() {

		List<Object[]> list = favGenreService.findTopGenresForMale();

		// 만약 리스트가 비어있다면 빈 리스트를 반환하거나 기본 값을 넣어줄 수 있음
		if (list.isEmpty()) {
			Map<String, Object> defaultMap = new HashMap<>();
			defaultMap.put("genre", "없음");
			defaultMap.put("count", 0);

			List<Map<String, Object>> response = new ArrayList<>();
			response.add(defaultMap); // 기본 데이터 추가
			return response;
		}

		// 리스트에 데이터가 있으면 정상 처리
		List<Map<String, Object>> response = new ArrayList<>();
		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("genre", item[0]);
			map.put("count", item[1]);
			response.add(map);
		}
		return response;
	}
	
	
	//관리자 페이지 - 통계 : 최근 일주일 간 영화발매일 별 대여 건수 확인
	@GetMapping("/RentByReleaseDateLast7days")
	@ResponseBody
	public List<Map<String, Object>> getRentByReleaseDateLast7days(){
		
		LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
		List<Object[]> resultLastSevenDays = rentService.getRentCountByReleaseDateLastSevenDays(sevenDaysAgo);
		
		List<Map<String, Object>> response = new ArrayList<>();
		
		for (Object[] result : resultLastSevenDays) {
			Map<String, Object> map = new HashMap<>();
			map.put("count_10000", result[0] != null ? result[0] : 0); // 발매일 1개월 이내 (null 처리)
			map.put("count_6000", result[1] != null ? result[1] : 0); // 발매된지 1-3개월 (null 처리)
			map.put("count_5000", result[2] != null ? result[2] : 0); // 발매된지 3-6개월 (null 처리)
			map.put("count_4000", result[3] != null ? result[3] : 0); // 발매된지 6개월-1년 (null 처리)
			map.put("count_1000", result[4] != null ? result[4] : 0); // 발매된지 1년 이상 (null 처리)
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}
	
	
	// 관리자 페이지 - 통계 : 올해 월별 영화발매일 별 매출 확인
	@GetMapping("/MonthlyRentByReleaseDateCurrentYear")
	@ResponseBody
	public List<Map<String, Object>> getMonthlySalesByReleaseDateCurrentYear() {

		int currentYear = LocalDate.now().getYear(); // 올해 연도 계산

		// 올해 연도에 대한 월별 대여 수 조회
		List<Object[]> resultsCurrentYear = rentService.RentCountByReleaseDateAndMonth(currentYear);

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : resultsCurrentYear) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 매출 날짜 (예 : 2023-11)
			map.put("count_10000", result[1] != null ? result[1] : 0); // 발매일 1개월 이내 (null 처리)
			map.put("count_6000", result[2] != null ? result[2] : 0); // 발매된지 1-3개월 (null 처리)
			map.put("count_5000", result[3] != null ? result[3] : 0); // 발매된지 3-6개월 (null 처리)
			map.put("count_4000", result[4] != null ? result[4] : 0); // 발매된지 6개월-1년 (null 처리)
			map.put("count_1000", result[5] != null ? result[5] : 0); // 발매된지 1년 이상 (null 처리)
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}
	
	// 관리자 페이지 - 통계 : 작년 월별 영화발매일 별 매출 확인
	@GetMapping("/MonthlyRentByReleaseDateLastYear")
	@ResponseBody
	public List<Map<String, Object>> getMonthlySalesByReleaseDateLastYear() {

		// 현재 연도를 가져와서 작년 연도를 계산
		int lastYear = LocalDate.now().getYear() - 1; // 작년 연도 계산

		// 작년 연도에 대한 월별 대여 수 조회
		List<Object[]> resultsLastYear = rentService.RentCountByReleaseDateAndMonth(lastYear);

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : resultsLastYear) {
			Map<String, Object> map = new HashMap<>();
			map.put("date", result[0]); // 매출 날짜 (예 : 2024-11)
			map.put("count_10000", result[1] != null ? result[1] : 0); // 발매일 1개월 이내 (null 처리)
			map.put("count_6000", result[2] != null ? result[2] : 0); // 발매된지 1-3개월 (null 처리)
			map.put("count_5000", result[3] != null ? result[3] : 0); // 발매된지 3-6개월 (null 처리)
			map.put("count_4000", result[4] != null ? result[4] : 0); // 발매된지 6개월-1년 (null 처리)
			map.put("count_1000", result[5] != null ? result[5] : 0); // 발매된지 1년 이상 (null 처리)
			response.add(map);
		}

		return response; // JSON 형태로 반환
	}
	

	// 관리자 페이지 - 통계 : 좋아요 % 높은 영화 순위 보여주기
	@GetMapping("/topLikeMovie")
	@ResponseBody
	public List<Map<String, Object>> getTopLikeMovie() {

		List<Object[]> list = likeService.topLikeMovie();

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			
			map.put("title", item[0]);
			map.put("movieseqno", item[1]);
			map.put("totalCount", item[2]);
			map.put("likeCount", item[3]);
			map.put("likePercentage", item[4]);
			response.add(map);
		}
		return response;
	}

	// 관리자 페이지 - 통계 : 싫어요 % 높은 영화 순위 보여주기
	@GetMapping("/topDislikeMovie")
	@ResponseBody
	public List<Map<String, Object>> getTopDislikeMovie() {

		List<Object[]> list = likeService.topDislikeMovie();

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			
			map.put("title", item[0]);
			map.put("movieseqno", item[1]);
			map.put("totalCount", item[2]);
			map.put("dislikeCount", item[3]);
			map.put("dislikePercentage", item[4]);
			
			response.add(map);
		}
		return response;
	}

	// 관리자 페이지 - 통계 : 호불호 높은 영화 순위 보여주기
	@GetMapping("/topDivisiveMovie")
	@ResponseBody
	public List<Map<String, Object>> getTopDivisiveMovie() {

		List<Object[]> list = likeService.topDivisiveMovie();

		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] item : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("title", item[0]);
			map.put("movieseqno", item[1]);
			map.put("totalCount", item[2]);
			map.put("likeCount", item[3]);
			map.put("dislikeCount", item[4]);
			map.put("likePercentage", item[5]);
			map.put("dislikePercentage", item[6]);

			// item[5]와 item[6]가 BigDecimal타입이라 BigDecimal을 String으로 변환 (소수점 아예 없게.)
			String likePercentage = ((BigDecimal) item[5]).setScale(0, RoundingMode.HALF_UP).toString();
			String dislikePercentage = ((BigDecimal) item[6]).setScale(0, RoundingMode.HALF_UP).toString();

			// 비율 합성
			String likeVsDislikeRatio = likePercentage + " : " + dislikePercentage;
			map.put("ratio", likeVsDislikeRatio);

			response.add(map);
		}
		return response;
	}

	@GetMapping("/movieStats")
	public void getMovieStats() {
	}

	@GetMapping("/movieUpload")
	public void getMovieUpload(Model model, @RequestParam(name="keyword", required = false) String keyword) {
		List<MovieEntity> list = movieRepository.findByTitleContainingIgnoreCase(keyword);
		if (!list.isEmpty()) {
			model.addAttribute("movie", list.get(0));
		}
	}
	
	//todo : movieUpload로 되어있는 곳 update로 다 변경
	@PostMapping("/movieUpdate")
	@ResponseBody
	public Map<String,String> postMovieUpdate(MovieDTO movie, @RequestParam("certification") String certi, @RequestParam("tagline") String tagline){
		Map<String,String> result = new HashMap<>();

		MovieEntity entity = movieRepository.findByTitle(movie.getTitle());

		entity.setTagline(tagline);
		entity.setOverview(movie.getOverview());
		entity.setCertification(certi);
		
		movieRepository.save(entity);
		result.put("message","good");	
		
		return result;
	}


	@PostMapping("/search")	
	@ResponseBody
	public List<MovieEntity> searchMovies(@RequestParam("keyword") String keyword) {
		List<MovieEntity> list = movieRepository.findByTitleContainingIgnoreCase(keyword);
//		List<MovieDTO> details = new ArrayList<>();
//		for(MovieEntity entity : list) {
//			MovieDTO detail = tmdb.getMovieDetails(entity.getMovieId(), "ko-KR").block();
//			details.add(detail);
//		}
		
		return list;
		
	}

	
	//관리자페이지 : 영화 수정 - Certification 이 null 인 영화 리스트 뽑아내기
	@GetMapping("/certIsNull")
	@ResponseBody
	public List<Map<String, Object>> getCertIsNull(){
		
		List<Object[]> list = movieRepository.findByCert();
		if (list == null) return new ArrayList<>();
		
		List<Map<String, Object>> response = new ArrayList<>();

		for (Object[] result : list) {
			
			Map<String, Object> map = new HashMap<>();
			
		    MovieEntity movie = (MovieEntity) result[0];  // 첫 번째 요소: MovieEntity 객체
		    
			map.put("movie", movie); // MovieEntity 객체
			
			response.add(map);			
		}
		return response;
		
	}	
	
	// 더미 데이터 만들기 페이지
//	@GetMapping("/dummyMaker")
//	public void getDummyMaker() {
//	}
//
//	// Dummy members 만들기
//	@ResponseBody
//	@PostMapping("/createDummyMembers")
//	public String createDummyMembers() {
//		int count = 100; // 원하는 대로 수 변경 가능
//		service.createDummyMembers(count);
//		return " ▶▷▶ " + count + "명의 더미 회원이 생성되었습니다.";
//	}
//
//	// Dummy 대여현황 만들기
//	@ResponseBody
//	@PostMapping("/createDummyRents")
//	public String createDummyRents() {
//		int count = 700; // 원하는 대로 수 변경 가능
//		service.createDummyRents(count);
//		return " ▶▷▶ " + count + "개의 더미 대여 현황이 생성되었습니다.";
//	}
//
//	// Dummy 좋아요 싫어요 만들기
//	@ResponseBody
//	@PostMapping("/createDummyLikes")
//	public String createDummyLikes() {
//		int count = 500; // 원하는 대로 수 변경 가능
//		service.createDummyLikes(count);
//		return " ▶▷▶ " + count + "개의 더미 좋아요/싫어요 현황이 생성되었습니다.";
//	}
//
//	// Dummy 회원 로그 기록 만들기
//	@ResponseBody
//	@PostMapping("/createDummyLogs")
//	public String createDummyLogs() {
//		int count = 500; // 원하는 대로 수 변경 가능
//		service.createDummyLogs(count);
//		return " ▶▷▶ " + count + "개의 더미 LOGIN/LOGOUT 현황이 생성되었습니다.";
//	}
//
//	// Dummy 시청여부 만들기
//	@ResponseBody
//	@PostMapping("/createDummyWatch")
//	public String createDummyWatch() {
//		int count = 500; // 원하는 대로 수 변경 가능
//		service.createDummyWatch(count);
//		return " ▶▷▶ " + count + "개의 더미 시청여부가 생성되었습니다.";
//	}
//
//	// Dummy 즐겨찾기 만들기
//	@ResponseBody
//	@PostMapping("/createDummyBookmarks")
//	public String createDummyBookmarks() {
//		int count = 500; // 원하는 대로 수 변경 가능
//		service.createDummyBookmarks(count);
//		return " ▶▷▶ " + count + "개의 더미 즐겨찾기가 생성되었습니다.";
//	}
//
//	// Dummy 좋아하는 장르 만들기
//	@ResponseBody
//	@PostMapping("/createDummyFavGenre")
//	public String createDummyFavGenre() {
//		int count = 500; // 원하는 대로 수 변경 가능
//		service.createDummyFavGenre(count);
//		return " ▶▷▶ " + count + "개의 더미 좋아하는 장르가 생성되었습니다.";
//	}

}
