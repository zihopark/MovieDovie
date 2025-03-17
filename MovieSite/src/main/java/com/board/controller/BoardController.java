package com.board.controller;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.board.dto.ReplyInterface;
import com.board.dto.RequestDTO;
import com.board.dto.RequestReplyDTO;
import com.board.entity.MileageLogEntity;
import com.board.entity.NotificationEntity;
import com.board.entity.RequestEntity;
import com.board.entity.RequestReplyEntity;
import com.board.entity.movie.GenreEntity;
import com.board.entity.movie.MovieEntity;
import com.board.service.BoardService;
import com.board.service.BookmarkService;
import com.board.service.FavGenreService;
import com.board.service.LikeService;
import com.board.service.MasterService;
import com.board.service.MemberService;
import com.board.service.MileageService;
import com.board.service.MovieAPIServiceImpl;
import com.board.service.NotificationService;
import com.board.service.RentService;
import com.board.service.ReplyService;
import com.board.service.RequestReplyService;
import com.board.service.RequestService;
import com.board.service.WatchedService;
import com.board.util.DataCalculate;
import com.board.util.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@AllArgsConstructor
@Log4j2
@RequestMapping("/board")
public class BoardController {
	private final MovieAPIServiceImpl movie;
	private final BoardService service;
	private final MemberService memberService;
	private final MasterService masterService;
	private final RequestService requestService;
	private final RequestReplyService requestReplyService;
	private final FavGenreService favGenre;
	private final LikeService like;
	private final ReplyService reply;
	private final WatchedService watch;
	private final BookmarkService bookmark;
	private final RentService rent;
	private final NotificationService notificationService;
	private final MileageService mileageService;

	@GetMapping("/test")
	public void getTest() {
	}

	@GetMapping("/pointPage")
	public void getMileagePage(Model model, HttpSession session) {
		String email = (String) session.getAttribute("email");
		String nickname = (String) session.getAttribute("nickname");
		MemberDTO member = memberService.memberInfo(email);

		List<MileageLogEntity> mileageLogs = mileageService.getMileageLogs(email);

		model.addAttribute("nickname", nickname);
		model.addAttribute("member", member);
		model.addAttribute("mileageLogs", mileageLogs);

		// 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		model.addAttribute("point", numberFormat.format(member.getPoint()));

		/////////////////// 알람 처리 ///////////////////
		model.addAttribute("email", email);

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

		if (noti == null) {
			noti = new ArrayList<>();
		}

		model.addAttribute("noti", noti); // 알림 목록을 모델에 추가

	}

	@GetMapping("/list")
	public void getList(Model model, HttpSession session) throws JsonMappingException, JsonProcessingException {

		// 세션정보
		String email = (String) session.getAttribute("email");
		String nickname = (String) session.getAttribute("nickname");

		MemberDTO memberDTO = memberService.memberInfo(email);

		model.addAttribute("member", memberDTO);
		model.addAttribute("nickname", nickname);
		model.addAttribute("nickname", session.getAttribute("nickname"));
		model.addAttribute("role", memberDTO.getRole());

		// 대여 현황
		List<Map<String, Object>> rents = rent.getAllMyRent(email, "Y");
		model.addAttribute("rentStatus", rents);

		// 주간 트렌드 영화
		List<Map<String, Object>> weeklyTrend = movie.getWeeklyTrend(memberDTO);
		model.addAttribute("weeklyTrend", weeklyTrend);

		// 선호 장르별 영화 리스트
		Map<String, Object> favGenreList = new HashMap<>();
		Optional<Map<String, Object>> genre = favGenre.getFavGenre(memberDTO.getEmail());
		if (genre.isPresent()) {
			for (int i = 0; i < genre.get().size(); i++) {
				// 장르 컬럼 이름
				String genreNm = (String) genre.get().get("genre" + (i + 1));
				// 인자로 받은 장르이름을 가진 모든 장르엔티티를 가져온다.
				List<GenreEntity> genres = movie.getGenreList(genreNm);
				// 해당 장르를 포함하고 있는 영화리스트를 가져온다.
				
				List<Map<String,Object>> genreList = movie.findByGenreGetMovieList(genres,DataCalculate.calcAge(memberDTO.getBirthdate()),memberDTO);
				favGenreList.put(genreNm, genreList);
			}
		}
		model.addAttribute("favGenre", genre.isPresent() ? genre.get() : null);
		model.addAttribute("favGenreList", favGenreList);
		
		//만장일치! 다들 이 영화 좋아해요
		List<Map<String,Object>> topLikeList = like.getTopList(20,memberDTO);
		model.addAttribute("likes",topLikeList);
		//요즘 인기 폭팔! 댓글 많은 영화
		List<Map<String,Object>> topReplyList = reply.getTopList(20,memberDTO);
		model.addAttribute("replies",topReplyList);
		//이건 꼭 봐야해! 회원들이 가장 많이 본 영화
		List<Map<String,Object>> topWatchList = watch.getTopList(20,memberDTO);
		model.addAttribute("watches",topWatchList);
		//오늘 대여 TOP 영화
		List<Map<String,Object>> topRentList = rent.getTopList(20,memberDTO);
		model.addAttribute("rents",topRentList);
		//무비두비에 새로 올라온 영화
		List<Map<String,Object>> directList = movie.getDirectUploadList(6,memberDTO);
		model.addAttribute("directUploadList",directList);
		
		
		/////////////////// 알람 처리 ///////////////////
		model.addAttribute("email", email);

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

		if (noti == null) {
			noti = new ArrayList<>();
		}

		model.addAttribute("noti", noti); // 알림 목록을 모델에 추가

		// 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		String formattedPoint = numberFormat.format(memberDTO.getPoint());
		model.addAttribute("point", formattedPoint);

	}

	@GetMapping("/mypage")
	public void getMyPage(Model model, HttpSession session) {

		// 세션정보
		String email = (String) session.getAttribute("email");
		String nickname = (String) session.getAttribute("nickname");

		MemberDTO memberInfo = memberService.memberInfo(email);
		model.addAttribute("nickname",nickname);
		model.addAttribute("member",memberInfo);
		
		//대여
		List<Map<String,Object>> rents = rent.getAllMyRent(email, "Y");
		model.addAttribute("rents",rents);
		//북마크
		List<Map<String,Object>> bookmarks = bookmark.getAllBookmarkByEmail(email);
		model.addAttribute("bookmarks",bookmarks);
		//시청
		List<Map<String,Object>> watches = watch.getAllWatchedByEmail(email);
		model.addAttribute("watches",watches);
		//좋아요
		List<Map<String,Object>> likes = like.getAllLikeByEmail(email);
		model.addAttribute("likes",likes);
		//과거 대여
		List<Map<String,Object>> pastRents = rent.getAllMyRent(email, "N");
		model.addAttribute("pastRents",pastRents);
		
		// 숫자 포맷터 설정 (쉼표 추가)
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

		// 누적 결제 금액
		Long accumulatedPayment = rent.getMyAccumulatedPayment(email);
		model.addAttribute("accumulatedPayment", numberFormat.format(accumulatedPayment));

		// 등급 변경일까지 얼마 남았는지?
		LocalDate today = LocalDate.now();
		LocalDate nextMonth = today.plusMonths(1).withDayOfMonth(1); // 다음 달의 1일 계산
		long daysUntilFirstOfMonth = ChronoUnit.DAYS.between(today, nextMonth); // 현재 날짜와 다음 달 1일 사이의 일수 계산
		model.addAttribute("daysUntilFirstOfMonth", daysUntilFirstOfMonth);
		model.addAttribute("nextMonth", nextMonth);

		// 등급 배열 (순서 중요)
		String[] TIERS = { "BRONZE", "SILVER", "GOLD", "PLATINUM", "DIAMOND" };
		int[] SALES_THRESHOLDS = { 0, 5000, 10000, 30000, 50000 };

		int thisMonthSales = rent.getThisMonthSales(email);

		// 등급 및 업그레이드 조건 정의
		String nextTier = TIERS[0]; // 기본 등급: BRONZE
		int additionalAmount = 0;
		String upgradeMessage = "";

		// 현재 매출에 따른 등급 계산
		for (int i = SALES_THRESHOLDS.length - 1; i >= 0; i--) {
			if (thisMonthSales >= SALES_THRESHOLDS[i]) {
				nextTier = TIERS[i];
				if (i < TIERS.length - 1) { // 한 단계 더 높은 등급 확인
					int nextTierThreshold = SALES_THRESHOLDS[i + 1];
					additionalAmount = nextTierThreshold - thisMonthSales;
					upgradeMessage = String.format("%s원 결제 시, %s 등급이 됩니다!", numberFormat.format(additionalAmount),
							TIERS[i + 1]);
				}
				break;
			}
		}

		model.addAttribute("currentSales", numberFormat.format(thisMonthSales));
		model.addAttribute("nextTier", nextTier);
		model.addAttribute("additionalAmount", numberFormat.format(additionalAmount));
		model.addAttribute("upgradeMessage", upgradeMessage);
		model.addAttribute("point", numberFormat.format(memberInfo.getPoint()));

		/////////////////// 알람 처리 ///////////////////
		model.addAttribute("email", email);

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

		if (noti == null) {
			noti = new ArrayList<>();
		}

		model.addAttribute("noti", noti); // 알림 목록을 모델에 추가

	}

	// 장르 필터
	@PostMapping("/genreCheck")
	public String postGenreCheck() {
		return null;
	}

	// 영화상세
	@GetMapping("/movieDetail")
	public void getView(Model model, @RequestParam("movieId") Long movieId, HttpSession session)
			throws JsonMappingException, JsonProcessingException {

		String email = (String) session.getAttribute("email");
		String nickname = (String) session.getAttribute("nickname");
		MemberDTO member = memberService.memberInfo(email);
		model.addAttribute("member", member);

		// 영화 반납일 확인
		rent.updateRent(email, movieId);

		// DB에서 영화 정보 가져오기
		Map<String, Object> movieDetail = movie.getMovieDetails(movieId, member);
		model.addAttribute("movie", movieDetail);
		movie.clickToMovie(movieId);

		// 사용자가 로그인한 경우에만 회원 정보와 좋아요 정보를 처리
		if (email != null && !email.isEmpty()) {
			MemberDTO memberDTO = memberService.memberInfo(email);
			model.addAttribute("nickname", nickname);
			model.addAttribute("role", memberDTO.getRole());

		} else {
			// 로그인하지 않은 사용자를 위한 기본값 설정
			model.addAttribute("nickname", "Guest");
			model.addAttribute("role", "GUEST");
		}

		// 나의 movieStat 정보
		Map<String, Object> mylike = like.getLikeInfo(email, movieId);
		if (mylike == null || mylike.isEmpty()) {
			model.addAttribute("like", null);
		} else {
			model.addAttribute("like", mylike);
		}
		Map<String, Object> myWatch = watch.getWatchedInfo(email, movieId);
		if (myWatch == null || myWatch.isEmpty()) {
			model.addAttribute("watch", null);
		} else {
			model.addAttribute("watch", myWatch);
		}
		Map<String, Object> myBookmark = bookmark.getBookmarkInfo(email, movieId);
		if (myBookmark == null || myBookmark.isEmpty()) {
			model.addAttribute("bookmark", null);
		} else {
			model.addAttribute("bookmark", myBookmark);
		}
		Map<String, Object> myRent = rent.getMyRentInfo(email, movieId);
		if (myRent != null) {
			model.addAttribute("rent", myRent);
		}

		List<Map<String, Object>> replies = reply.getList(movieId);
		model.addAttribute("replies", replies);

		// movieStat 정보
		Map<String, Object> likes = like.getAllLikeInfo(movieId);
		Map<String, Object> watches = watch.getAllWatchedInfo(movieId);
		Map<String, Object> bookmarks = bookmark.getAllBookmarkInfo(movieId);

		model.addAttribute("likes", likes);
		model.addAttribute("watches", watches);
		model.addAttribute("bookmarks", bookmarks);

		/////////////////// 알람 처리 ///////////////////
		model.addAttribute("email", email);

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

       // 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
       NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
       model.addAttribute("point", numberFormat.format(member.getPoint()));
       
       
      // 추천 영화 리스트
       List<Map<String,Object>> genres = (List)movieDetail.get("genre");
       List<MovieEntity> recommandList = new ArrayList<>();
       for(Map<String,Object> genre :genres) {
          String genreNm = (String)genre.get("name");
          List<GenreEntity> list = movie.getGenreList(genreNm);
          int age = DataCalculate.calcAge(member.getBirthdate());
          List<MovieEntity> movies = list.stream()
          .filter(s->DataCalculate.calcIsAdult(s.getMovieId().getCertification(), age))
          .map(s->s.getMovieId()).collect(Collectors.toList());
          recommandList.addAll(movies);
          break;
       }
       model.addAttribute("recommandList",recommandList);
   }

	@RequestMapping("/movieDetail/getMileage")
	@ResponseBody
	public Map<String, Object> postMovieDetailGetMileage(HttpSession session) {
		// 세션에서 이메일이 없는 경우 처리 추가
		String email = Optional.ofNullable(session.getAttribute("email")).map(Object::toString)
				.orElseThrow(() -> new IllegalStateException("로그인이 필요합니다"));

		MemberDTO member = memberService.memberInfo(email);

		return Map.of("mileage", member.getPoint());
	}

	@PostMapping("/movieDetail/reviews")
	@ResponseBody
	public Map<String, Object> postMovieDetailReview(@RequestParam("movieId") Long movieId,
			@RequestParam("content") String content, HttpSession session) {

		String email = (String) session.getAttribute("email");

		Map<String, Object> response = new HashMap<>();

		// 리뷰 업데이트 및 저장
		String result = reply.saveOrUpdate(content, email, movieId);

		response.put("message", "good");
		response.put("action", result);
		return response;
	}

	// 좋아요 싫어요 처리
	@PostMapping("/movieDetail/likes")
	@ResponseBody
	public Map<String, Object> postMovieDetailLike(@RequestParam("movieId") Long movieId,
			@RequestParam("isLike") String isLike, @RequestParam("action") String action, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		String rating = "Y".equals(isLike) ? "like" : "dislike";
		String email = (String) session.getAttribute("email");

		boolean isAdd = "add".equals(action);
		// 좋아요 싫어요 업데이트 및 저장
		try {
			like.saveOrUpdate(rating, movieId, email, isAdd);
			response.put("message", "good");
			// 모든 like 정보
			Map<String, Object> likes = like.getAllLikeInfo(movieId);
			response.put("likeCount", likes.get("likeCount"));
			response.put("dislikeCount", likes.get("dislikeCount"));
			response.put("userLike", isLike);
		} catch (Exception e) {
			log.error("likedislike 오류 : ", e);
			response.put("message", "error");
			response.put("error", e.getMessage());
		}

		return response;
	}

	// 시청 여부 처리
	@PostMapping("/movieDetail/watched")
	@ResponseBody
	public Map<String, Object> postMovieDetailWatched(@RequestParam("movieId") Long movieId,
			@RequestParam("action") String action, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		String email = (String) session.getAttribute("email");

		// 시청여부 업데이트 및 저장
		try {
			watch.saveOrUpdate(action, movieId, email);
			response.put("message", "good");
			// 모든 like 정보
			Map<String, Object> watches = watch.getAllWatchedInfo(movieId);
			response.put("count", watches.get("count"));
		} catch (Exception e) {
			log.error("wathed 오류 : ", e);
			response.put("message", "error");
			response.put("error", e.getMessage());
		}

		return response;
	}

	// 북마크 관련 처리
	@PostMapping("/movieDetail/bookmark")
	@ResponseBody
	public Map<String, Object> postMovieDetailBookmark(@RequestParam("movieId") Long movieId,
			@RequestParam("action") String action, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		String email = (String) session.getAttribute("email");

		// 북마크 업데이트 및 저장
		try {
			bookmark.saveOrUpdate(action, movieId, email);
			response.put("message", "good");
			// 모든 bookmarks 정보
			Map<String, Object> bookmarks = bookmark.getAllBookmarkInfo(movieId);
			response.put("count", bookmarks.get("count"));
		} catch (Exception e) {
			log.error("bookmark 오류 : ", e);
			response.put("message", "error");
			response.put("error", e.getMessage());
		}

		return response;
	}

	// 대여 관련 처리
	@PostMapping("/movieDetail/rent")
	@ResponseBody
	public Map<String, Object> postMovieDetailRent(@RequestParam("movieId") Long movieId,
			@RequestParam("paymentMethod") String paymentMethod, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		String email = (String) session.getAttribute("email");

		// 대여 등록 및 삭제
		try {
			String status = rent.rentMovie(email, movieId, paymentMethod);
			response.put("message", "good");
			response.put("status", status);
		} catch (Exception e) {
			log.error("rent 오류 : ", e);
		}

		return response;
	}

	// 댓글 처리
	@ResponseBody
	@PostMapping("/reply")
	public List<ReplyInterface> postReply(ReplyInterface reply, @RequestParam("option") String option)
			throws Exception {

		switch (option) {

		case "I":
			service.replyRegistry(reply); // 댓글 등록
			break;
		case "U":
			service.replyUpdate(reply); // 댓글 수정
			break;
		case "D":
			service.replyDelete(reply); // 댓글 삭제
			break;
		}

		return service.replyView(reply);
	}

	// 요청사항 작성하기
	@GetMapping("/requestWrite")
	public void getRequest(Model model, HttpSession session) {

		// 세션 이메일 값 가져오기
		String email = (String) session.getAttribute("email"); // 현재 로그인 중인 사람의 이메일
		model.addAttribute("email", email);

		// 이메일로 회원 정보 가져오기
		MemberDTO memberDTO = memberService.memberInfo(email);
		model.addAttribute("member", memberDTO);

		model.addAttribute("nickname", memberDTO.getNickname());
		model.addAttribute("member", memberDTO);

		model.addAttribute("maxSeqno",
				(masterService.getMaxSeqno(email) != null ? masterService.getMaxSeqno(email) + 1 : "FIRST_REQ"));

		// 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		model.addAttribute("point", numberFormat.format(memberDTO.getPoint()));

		/////////////////// 알람 처리 ///////////////////

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

		if (noti == null) {
			noti = new ArrayList<>();
		}

		model.addAttribute("noti", noti); // 알림 목록을 모델에 추가

	}

	// 요청사항 DB 업로드
	@ResponseBody
	@PostMapping("/requestWritePost")
	public Map<String, String> postRequest(@RequestBody RequestDTO dto, HttpSession session) throws Exception {

		Map<String, String> response = new HashMap<>();

		try {
			requestService.registerRequest(dto);
			response.put("message", "good");
		} catch (Exception e) {
			response.put("message", "bad");
			e.printStackTrace();
		}
		return response;
	}

	// 회원이 자신의 요청 목록 보기
	@GetMapping("/requestList")
	public void getMemberRequest(Model model, @RequestParam(name = "page", defaultValue = "1") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "", required = false) String keyword, HttpSession session)
			throws Exception {

		int postNum = 10; // 한 화면에 보여지는 게시물 행의 갯수
		int pageListCount = 10; // 화면 하단에 보여지는 페이지리스트의 페이지 갯수

		String email = (String) session.getAttribute("email"); // 현재 로그인 중인 사람의 이메일
		model.addAttribute("email", email);

		// 이메일로 회원 정보 가져오기
		MemberDTO memberDTO = memberService.memberInfo(email);
		model.addAttribute("member", memberDTO);
		model.addAttribute("nickname", memberDTO.getNickname());

		model.addAttribute("member", memberDTO);

		PageUtil page = new PageUtil();

		Page<RequestEntity> list = requestService.mylist(pageNum, postNum, keyword, email);
		int totalCount = (int) list.getTotalElements();
		model.addAttribute("list", list);
		model.addAttribute("listIsEmpty", list.hasContent() ? "N" : "Y");
		model.addAttribute("totalElement", totalCount);
		model.addAttribute("postNum", postNum);
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList",
				page.getBoardRequestPageList(pageNum, postNum, pageListCount, totalCount, keyword));

		// 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		model.addAttribute("point", numberFormat.format(memberDTO.getPoint()));

		/////////////////// 알람 처리 ///////////////////

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

		if (noti == null) {
			noti = new ArrayList<>();
		}

		model.addAttribute("noti", noti); // 알림 목록을 모델에 추가

	}

	// 회원이 자신의 요청사항 상세보기
	@GetMapping("/requestView")
	public void getRequestView(Model model, @RequestParam(name = "seqno", required = false) Long seqno,
			@RequestParam("page") int pageNum,
			@RequestParam(name = "keyword", defaultValue = "", required = false) String keyword, HttpSession session) {

		// 세션 이메일 값 가져오기
		String email = (String) session.getAttribute("email"); // 현재 로그인 중인 사람의 이메일
		model.addAttribute("email", email);

		// 이메일로 회원 정보 가져오기
		MemberDTO memberDTO = memberService.memberInfo(email);
		model.addAttribute("nickname", memberDTO.getNickname());
		model.addAttribute("member", memberDTO);

		model.addAttribute("view", masterService.view(seqno));
		model.addAttribute("status", masterService.statusCheck(seqno));
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pre_seqno", requestService.my_pre_seqno(seqno, email, keyword));
		model.addAttribute("next_seqno", requestService.my_next_seqno(seqno, email, keyword));

		// 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		model.addAttribute("point", numberFormat.format(memberDTO.getPoint()));

		/////////////////// 알람 처리 ///////////////////

		// 알림 목록 가져오기 (빈 리스트일 경우 처리)
		List<NotificationEntity> noti = notificationService.notiForUser(email);

		if (noti == null) {
			noti = new ArrayList<>();
		}

		model.addAttribute("noti", noti); // 알림 목록을 모델에 추가

	}

	// 회원이 자신의 요청 게시물 삭제
	@Transactional
	@GetMapping("/requestDelete")
	public String getDelete(@RequestParam("seqno") Long seqno) throws Exception {

		// tbl_request에서 게시물을 삭제
		masterService.delete(seqno);
		return "redirect:/board/requestList"; // 삭제 후 목록 페이지로 리다이렉션
	}

	// 댓글 처리
	@ResponseBody
	@PostMapping("/requestReply")
	public List<Map<String, Object>> postReply(@RequestBody RequestReplyDTO dto, @RequestParam("kind") String kind)
			throws Exception {

		List<RequestReplyEntity> replyDataList;

		switch (kind) {
		case "L":
			replyDataList = requestReplyService.replyList(dto.getReqseqno()); // 댓글 목록 보기
			break;
		case "I":
			requestReplyService.replyRegister(dto); // 댓글 등록
			replyDataList = requestReplyService.replyList(dto.getReqseqno()); // 등록 후 댓글 목록 반환
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

//---------검색trying-------------------------
	@GetMapping("/search")
	public void searchPage(Model model, @RequestParam(name = "keyword", required = false) String keyword,
			HttpSession session) {
		String email = (String) session.getAttribute("email");
		String nickname = (String) session.getAttribute("nickname");

		MemberDTO member = memberService.memberInfo(email);

		model.addAttribute("member", member);
		model.addAttribute("nickname", nickname);

		if (keyword != null) {
			List<Map<String, Object>> movies = movie.findByKeywordMovieList(keyword, member);
			model.addAttribute("movies", movies);
			List<Map<String, Object>> crews = movie.findByKeywordCrewList(keyword, member);
			model.addAttribute("crews", crews);
			List<Map<String, Object>> casts = movie.findByKeywordCastList(keyword, member);
			model.addAttribute("casts", casts);
			List<Map<String, Object>> companies = movie.findByKeywordProductionCompanyList(keyword, member);
			model.addAttribute("companies", companies);
		}

		// 숫자 포맷터 설정 (쉼표 추가) 및 메뉴바에 들어갈 포인트 쉼표 추가
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		model.addAttribute("point", numberFormat.format(member.getPoint()));

	}
}