package com.board.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.dto.RequestDTO;
import com.board.entity.RequestEntity;
import com.board.entity.repository.RentRepository;
import com.board.entity.repository.RequestRepository;
import com.board.entity.repository.movie.MovieRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MasterServiceImpl implements MasterService{

	private final RequestRepository requestRepository;
	private final RentRepository rentRepository;
	private final MovieRepository movieRepository;
    //구매 전환율 구하기
  	@Override
    public double conversionRate() {
  		
  		int rentCount = rentRepository.getTotalRent();
  		int hitnoCount = movieRepository.getTotalHitno();
  		
  		double rate = (double)rentCount / hitnoCount * 100;
  		return Math.round(rate * 10.0) / 10.0;
  	}
    
	//요청 게시물 목록 보기
	@Override
	public Page<RequestEntity> list(int pageNum, int postNum, String keyword) throws Exception {	
		//PageRequest는 Page와 함께 사용되는 JPA의 객체로, 페이징 기준을 설정 -> 시작점(0부터 시작), 증가분(한 화면에 보이는 행의 수), 정렬 방식(seqno를 기준으로 역순으로 정렬)
		PageRequest pageRequest = PageRequest.of(pageNum - 1, postNum, Sort.by(Direction.DESC, "seqno")); //pageNum은 1부터 시작하기 때문에 -1해줌
		return requestRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageRequest); //마지막에 pageRequest 넣어주면 됨
	}

	//요청 게시물 내용 상세 보기
	@Override
	public RequestDTO view(Long seqno) {
		//Entity --> DTO, xxxRepository의 리턴값은 Optional type 이기 때문에, List 타입이 아닌 최종 값을 얻기 위해선 뒤에 get()을 붙여줘야 함.
		return requestRepository.findById(seqno)
	            .map(view -> new RequestDTO(view))
	            .orElseThrow(() -> new NoSuchElementException("요청 ID " + seqno + "에 해당하는 게시물을 찾을 수 없습니다."));
	}

	//요청 게시물 이전 보기
	@Override
	public Long pre_seqno(Long seqno, String keyword) {
		//null 이 들어올 수도 있음. 그렇기 때문에 아래처럼 써줘야해.
		//3항 연산식 > null값이 들어올 경우 0으로. 아니면 그대로.
		return requestRepository.pre_seqno(seqno, keyword, keyword)==null?0:requestRepository.pre_seqno(seqno, keyword, keyword);
	}
	
	//요청 게시물 다음 보기
	@Override
	public Long next_seqno(Long seqno, String keyword) {
		return requestRepository.next_seqno(seqno, keyword, keyword)==null?0:requestRepository.next_seqno(seqno, keyword, keyword);
	}
	
	//max seqno 구하기
	@Override
	public Long getMaxSeqno(String email) {
		return requestRepository.getMaxSeqno(email);
	}
	
	//회원 요청 게시물 삭제
	@Override
	public void delete(Long seqno) {
		requestRepository.deleteBySeqno(seqno);
	}
	
	//처리 완료 여부 확인하기
	@Override
	public String statusCheck(Long seqno) {
	    return requestRepository.findStatusBySeqno(seqno);
	}
	
	//처리 상태 업데이트하기
	@Transactional
	@Override
	public void updateStatus(Long seqno, String status){
		requestRepository.updateStatusBySeqno(seqno, status);
	}	
	
	
	//영화 중복 체크
	@Override
	public void movieCheck(String movieNm) {
		//return movieRepository.findById(movieNm).isEmpty()?0:1;
	}
	
//	//dummy 회원 대량으로 만들기
//	@Override
//    @Transactional
//    public void createDummyMembers(int count) {
//		
//		Random random = new Random(); // Random 초기화
//		
//		List<MemberEntity> members = new ArrayList<>();
//        for (int i = 1; i <= count; i++) {
//        	MemberEntity member = new MemberEntity();
//            member.setEmail("user" + i + "@example.com");
//            member.setPassword("password" + i);
//            member.setUsername("User" + i);
//            member.setNickname("Nickname" + i);
//            member.setGender(GENDERS[random.nextInt(GENDERS.length)]);
//            
//            // 1980년 1월 1일부터 2010년 12월 31일 사이의 랜덤 생일 생성
//            LocalDate startDate = LocalDate.of(1980, 1, 1);
//            LocalDate endDate = LocalDate.of(2010, 12, 31);
//            long startEpochDay = startDate.toEpochDay(); // 1980-01-01의 epoch day
//            long endEpochDay = endDate.toEpochDay();     // 2010-12-31의 epoch day
//
//            // 랜덤한 epoch day 값 생성
//            long randomEpochDay = startEpochDay + (long) (random.nextDouble() * (endEpochDay - startEpochDay));
//            // 랜덤한 생일 날짜 생성
//            LocalDate randomBirthDate = LocalDate.ofEpochDay(randomEpochDay);
//            // 날짜 포맷터 생성 (yyyy-MM-dd 형식으로)
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            // 포맷에 맞춰 문자열로 변환
//            String birth = randomBirthDate.format(formatter);
//            member.setBirth(birth); // 생일 설정
//            
//            member.setTelno("010-" + (random.nextInt(9000) + 1000) + "-" + (random.nextInt(9000) + 1000));
//            
//            // 2023년 1월 1일부터 2024년 11월 15일 사이의 랜덤 회원 가입일 생성
//            long regminDay = LocalDateTime.of(2023, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long regmaxDay = LocalDateTime.of(2024, 11, 15, 23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long regrandomTime = regminDay + (long)(random.nextDouble() * (regmaxDay - regminDay));
//            LocalDateTime regdate = LocalDateTime.ofInstant(new Date(regrandomTime).toInstant(), ZoneId.systemDefault());
//            member.setRegdate(regdate); // 회원 가입일 설정
//            
//            member.setRole("USER");
//            members.add(member);
//        }
//        memberRepository.saveAll(members); //일괄 저장
//    }
//
//	
//	//Dummy Rents 만들기
//	@Override
//    @Transactional
//    public void createDummyRents(int count) {
//		LocalDate today = LocalDate.now(); // 오늘 날짜
//		
//        for (int i = 1; i <= count; i++) {
//        	RentEntity rent = new RentEntity();
//            rent.setSeqno((long) i);
//        	
//            // 2023년 10월 1일부터 2024년 11월 15일 사이의 랜덤 대여일 생성
//            long minDay = LocalDateTime.of(2023, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long maxDay = LocalDateTime.of(2024, 11, 15, 23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long randomTime = minDay + (long)(random.nextDouble() * (maxDay - minDay));
//            LocalDateTime rentdate = LocalDateTime.ofInstant(new Date(randomTime).toInstant(), ZoneId.systemDefault());
//            
//            rent.setRentdate(rentdate); // 대여일 설정
//            
//            // 대여 반납일은 대여일로부터 3일 후로 설정
//            LocalDateTime returndate = rentdate.plusDays(3);
//            rent.setReturndate(returndate); // 반납일 설정
//            
//            // 대여 상태 설정
//            String status = today.isBefore(returndate.toLocalDate()) ? "Y" : "N"; // 상태 설정
//            rent.setStatus(status); // 상태 설정
//            
//            // 1부터 30까지의 랜덤 숫자 생성
//            int randomNumber = random.nextInt(30) + 1; // 0~29 사이의 숫자를 생성하고 1을 더하여 1~30으로 변경
//            rent.setEmail("user" + randomNumber + "@example.com"); // 랜덤 숫자를 이메일에 포함
//            
//            rent.setMovieseqno((long) (random.nextInt(50) + 1)); // 1부터 50까지의 랜덤 숫자 설정
//            
//            rentRepository.save(rent);
//        }
//    }
//	
//	//Dummy 좋아요 싫어요 만들기
//	@Override
//    @Transactional
//	public void createDummyLikes(int count) {
//		
//		  for (int i = 1; i <= count; i++) {
//	        	LikeEntity like = new LikeEntity();
//	            like.setSeqno((long) i);
//	            
//	            String likecheck = YorN[random.nextInt(YorN.length)];
//	            
//	            like.setLikecheck(likecheck);
//	            if (likecheck.equals("Y")) {
//	            	like.setDislikecheck("N");
//	            } else if (likecheck.equals("N")){
//	            	like.setDislikecheck("Y");
//	            }
//
//	        	like.setEmail("user" + i + "@example.com");
//	        	like.setMovieId((Long)(random.nextLong(20) + 1)); // 1부터 20까지의 랜덤 숫자 설정
//	            
//	        	likeRepository.save(like);
//	      }
//	}
//	
//	//Dummy 멤버 로그인/로그아웃 현황 (member_log) 만들기
//	@Override
//    @Transactional
//	public void createDummyLogs(int count) {
//		
//		for (int i = 1; i <= count; i++) {
//        	MemberLogEntity log = new MemberLogEntity();
//            log.setSeqno((long) i);
//            
//            // 2024년 10월 1일부터 2024년 11월 15일 사이의 랜덤 로그인 로그아웃일 생성
//            long minDay = LocalDateTime.of(2024, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long maxDay = LocalDateTime.of(2024, 11, 15, 23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long randomTime = minDay + (long)(random.nextDouble() * (maxDay - minDay));
//            LocalDateTime inouttime = LocalDateTime.ofInstant(new Date(randomTime).toInstant(), ZoneId.systemDefault());
//            
//            log.setInouttime(inouttime); // 로그인 로그아웃일 설정
//            
//            log.setStatus(INOUT[random.nextInt(INOUT.length)]);
//
//        	log.setEmail("user" + i + "@example.com");
//
//        	memberLogRepository.save(log);
//		}
//	}
//	
//	//Dummy 시청여부 만들기
//	@Override
//    @Transactional
//	public void createDummyWatch(int count) {
//		
//		for (int i = 1; i <= count; i++) {
//        	WatchEntity watch = new WatchEntity();
//            watch.setSeqno((long) i);
//            
//            // 2022년 1월 1일부터 2024년 11월 15일 사이의 랜덤 로그인 로그아웃일 생성
//            long minDay = LocalDateTime.of(2022, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long maxDay = LocalDateTime.of(2024, 11, 15, 23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//            long randomTime = minDay + (long)(random.nextDouble() * (maxDay - minDay));
//            LocalDateTime watchdate = LocalDateTime.ofInstant(new Date(randomTime).toInstant(), ZoneId.systemDefault());
//            
//            watch.setWatchdate(watchdate); // 시청일 설정
//            
//            watch.setStatus(YorN[random.nextInt(YorN.length)]);
//        	watch.setEmail("user" + i + "@example.com");
//        	watch.setMovieseqno((long) (random.nextInt(20) + 1)); // 1부터 20까지의 랜덤 숫자 설정
//
//        	watchRepository.save(watch);
//		}
//	}
//	
//	
//	//Dummy 즐겨찾기 만들기
//	@Override
//    @Transactional	
//	public void createDummyBookmarks(int count) {
//		
//		for (int i = 1; i <= count; i++) {
//        	BookmarkEntity bookmark = new BookmarkEntity();
//            bookmark.setSeqno((long) i);
//            bookmark.setStatus(YorN[random.nextInt(YorN.length)]);
//            bookmark.setEmail("user" + i + "@example.com");
//            bookmark.setMovieseqno((long) (random.nextInt(20) + 1)); // 1부터 20까지의 랜덤 숫자 설정
//
//            bookmarkRepository.save(bookmark);
//		}
//	}
//	
//	
//	//Dummy 좋아하는 장르 만들기
//	@Override
//	@Transactional
//	public void createDummyFavGenre(int count) {
//
//	    // 장르 배열 (19개)
//	    String[] GENRES = {"드라마", "코미디", "로맨스", "스릴러", "미스터리", "공포", "모험", "범죄", "가족", "판타지", "SF", "서부", "역사", "애니메이션", "다큐멘터리", "전쟁", "음악", "액션", "TV영화"};
//
//	    for (int i = 1; i <= count; i++) {
//	        FavGenreEntity favGenre = new FavGenreEntity();
//	        favGenre.setSeqno((long) i);
////	        favGenre.setEmail("user" + i + "@example.com");
//
//	        // 장르 선택을 위한 배열 복사
//	        List<String> availableGenres = new ArrayList<>(Arrays.asList(GENRES));
//
//	        // 장르 목록을 무작위로 섞음
//	        Collections.shuffle(availableGenres);
//
//	        // genre1, genre2, genre3 설정 (섞인 목록에서 순차적으로 선택)
//	        favGenre.setGenre1(availableGenres.get(0));
//	        favGenre.setGenre2(availableGenres.get(1));
//	        favGenre.setGenre3(availableGenres.get(2));
//
//	        favGenreRepository.save(favGenre);
//	    }
//	}
//	
	

	
}
