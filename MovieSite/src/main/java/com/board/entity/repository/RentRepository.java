package com.board.entity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.entity.MemberEntity;
import com.board.entity.RentEntity;
import com.board.entity.movie.MovieEntity;

public interface RentRepository extends JpaRepository<RentEntity, Long>{

	public List<RentEntity> findAllByEmailAndStatus(MemberEntity member, String status);
	public Optional<RentEntity> findById(Long seqno);
	public RentEntity findByEmailAndMovieId(MemberEntity member, MovieEntity movie);
	public RentEntity findByEmailAndMovieIdAndStatus(MemberEntity member, MovieEntity movie,String status);
	//모든 회원 자동 반납 확인
	public List<RentEntity> findAllByReturndateBeforeAndStatus(LocalDateTime returndate, String status);
	
	//오늘부터 7일전까지의 대여 현황
	@Query(value = "SELECT r.movie_id AS movieId, COUNT(*) AS count " +
            "FROM rent r " +
            "WHERE r.rentdate BETWEEN :startDate AND :endDate " +
            "GROUP BY r.movie_id " +
            "ORDER BY COUNT(*) DESC", 
     nativeQuery = true)
	List<Map<String, Object>> findByRecentWeekList(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
	
	//관리자 페이지 - 통계 : 대여 수 확인 (startDate에 원하는 날짜, endDate에 원하는 날짜 넣으면 그 사이의 값들이 나옴)
	@Query(value = "SELECT TRUNC(r.rentdate) AS rent_date, COUNT(*)" +
            "FROM rent r " +
            "WHERE r.rentdate BETWEEN :startDate AND :endDate " +
            "GROUP BY TRUNC(r.rentdate)" +
            "ORDER BY TRUNC(r.rentdate), COUNT(*) DESC", 
    nativeQuery = true)
	public List<Object[]> RentCount(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	
	//관리자 페이지 - 통계 : 매출 확인 (startDate에 원하는 날짜, endDate에 원하는 날짜 넣으면 그 사이의 값들이 나옴)
	@Query(value = "SELECT TRUNC(r.rentdate) AS rent_date, SUM(r.sales)" +
            "FROM rent r " +
            "WHERE r.rentdate BETWEEN :startDate AND :endDate " +
            "AND (r.payment_type IS NULL OR r.payment_type = 'cash') " +
            "GROUP BY TRUNC(r.rentdate)" +
            "ORDER BY TRUNC(r.rentdate)", nativeQuery = true)
	public List<Object[]> Sales(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
	

	//관리자 페이지 - 통계 : 월별 대여 수 확인
	@Query(value = "SELECT TO_CHAR(r.rentdate, 'YYYY-MM') AS month, "
			+ "COUNT(*) AS count "
			+ "FROM rent r "
			+ "WHERE EXTRACT(YEAR FROM r.rentdate) = :year "
			+ "GROUP BY TO_CHAR(r.rentdate, 'YYYY-MM')", nativeQuery = true)
	public List<Object[]> RentCountByMonth(@Param("year") int year);
	
	
	//관리자 페이지 - 통계 : 월별 매출 확인
	@Query(value = "SELECT TO_CHAR(r.rentdate, 'YYYY-MM') AS month, "
			+ "SUM(r.sales) "
			+ "FROM rent r "
			+ "WHERE EXTRACT(YEAR FROM r.rentdate) = :year "
			+ "AND (r.payment_type IS NULL OR r.payment_type = 'cash') "
			+ "GROUP BY TO_CHAR(r.rentdate, 'YYYY-MM')", nativeQuery = true)
	public List<Object[]> SalesByMonth(@Param("year") int year);


	//관리자 페이지 - 통계 : 올해 대여 status 수 확인
	@Query(value = "SELECT r.status, COUNT(r) FROM rent r WHERE YEAR(r.rentdate) = YEAR(CURRENT_DATE) GROUP BY r.status")
	public List<Object[]> RentStatus();


	//관리자 페이지 - 통계 : 올해 대여 TOP 회원 닉네임 확인
	@Query("SELECT m.nickname, COUNT(r.seqno) AS rentCount, SUM(r.sales) AS sales " +
		       "FROM rent r " +
		       "JOIN r.email m " +
		       "WHERE EXTRACT(YEAR FROM r.rentdate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
		       "AND (r.paymentType IS NULL OR r.paymentType = 'cash') " +
		       "GROUP BY m.nickname " +
		       "ORDER BY sales DESC")
	public List<Object[]> findTopRenterNickname();
	
	
	//총 누적 대여 금액
	@Query("SELECT COALESCE(SUM(r.sales), 0) " +
			"FROM rent r " +
			"WHERE r.email.email = :email " +
			"AND (r.paymentType IS NULL OR r.paymentType = 'cash')")
    Long getTotalSalesByEmail(@Param("email") String email);
	
	//월 누적 대여 금액
	@Query("SELECT COALESCE(SUM(r.sales), 0) " +
	"FROM rent r " +
	"WHERE r.email.email = :email " +
	"AND (r.paymentType IS NULL OR r.paymentType = 'cash') " +
	"AND EXTRACT(YEAR FROM r.rentdate) = EXTRACT(YEAR FROM SYSDATE) " +
	"AND EXTRACT(MONTH FROM r.rentdate) = EXTRACT(MONTH FROM SYSDATE)")
	int getMonthlySalesByEmail(@Param("email") String email);

	//관리자 페이지 - 통계 : 전체 회원의 대여 수 확인
	@Query(value = "SELECT COUNT(r.seqno) FROM rent r")
	public int getTotalRent();
	
	//관리자 페이지 - 통계 : 월별 영화발매일 별 대여 건수 확인
	@Query(value = "SELECT TO_CHAR(r.rentdate, 'YYYY-MM') AS month, "
	    + "COUNT(CASE WHEN r.sales = 10000 THEN r.seqno END) AS count_10000, "
	    + "COUNT(CASE WHEN r.sales = 6000 THEN r.seqno END) AS count_6000, "
	    + "COUNT(CASE WHEN r.sales = 5000 THEN r.seqno END) AS count_5000, "
	    + "COUNT(CASE WHEN r.sales = 4000 THEN r.seqno END) AS count_4000, "
	    + "COUNT(CASE WHEN r.sales = 1000 THEN r.seqno END) AS count_1000 "
	    + "FROM rent r "
	    + "WHERE EXTRACT(YEAR FROM r.rentdate) = :year "
	    + "GROUP BY TO_CHAR(r.rentdate, 'YYYY-MM') "
	    + "ORDER BY month", nativeQuery = true)
	public List<Object[]> getRentCountByReleaseDateAndMonth(@Param("year") int year);

	/*
	//관리자 페이지 - 통계 : 분기별 영화발매일 별 매출 확인
	@Query(value = "SELECT TO_CHAR(r.rentdate, 'YYYY-Q') AS quarter, "
		    + "SUM(CASE WHEN r.sales = 10000 THEN r.sales ELSE 0 END) AS sales_10000, "
		    + "SUM(CASE WHEN r.sales = 6000 THEN r.sales ELSE 0 END) AS sales_6000, "
		    + "SUM(CASE WHEN r.sales = 5000 THEN r.sales ELSE 0 END) AS sales_5000, "
		    + "SUM(CASE WHEN r.sales = 4000 THEN r.sales ELSE 0 END) AS sales_4000, "
		    + "SUM(CASE WHEN r.sales = 1000 THEN r.sales ELSE 0 END) AS sales_1000 "
		    + "FROM rent r "
		    + "WHERE EXTRACT(YEAR FROM r.rentdate) = :year "
		    + "GROUP BY TO_CHAR(r.rentdate, 'YYYY-Q') "
		    + "ORDER BY quarter", nativeQuery = true)
	public List<Object[]> SalesByReleaseDateByQuarter(@Param("year") int year);
	*/
	
	//관리자 페이지 - 통계 : 최근 일주일 간 영화발매일 별 대여 건수 확인
    @Query("SELECT " +
            "COUNT(CASE WHEN r.sales = 10000 THEN r.seqno ELSE NULL END) AS count10000, " +
            "COUNT(CASE WHEN r.sales = 6000 THEN r.seqno ELSE NULL END) AS count6000, " +
            "COUNT(CASE WHEN r.sales = 5000 THEN r.seqno ELSE NULL END) AS count5000, " +
            "COUNT(CASE WHEN r.sales = 4000 THEN r.seqno ELSE NULL END) AS count4000, " +
            "COUNT(CASE WHEN r.sales = 1000 THEN r.seqno ELSE NULL END) AS count1000 " +
            "FROM rent r " +
            "WHERE r.rentdate >= :sevenDaysAgo")
     public List<Object[]> getRentCountByReleaseDateLastSevenDays(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
     
     
     //관리자 페이지 - 통계 : 영화 장르별 매출 현황 YTD 
     @Query(value = "SELECT g.genre_name AS genreName, SUM(r.sales) AS totalSales " +
    	       "FROM rent r " +
    	       "JOIN (SELECT g.movie_id, MIN(g.genre_name) AS genre_name " +
    	       "      FROM genre g " +
    	       "      GROUP BY g.movie_id) g " +
    	       "ON r.movie_id = g.movie_id " +
    	       "WHERE EXTRACT(YEAR FROM r.rentdate) = EXTRACT(YEAR FROM SYSDATE) " +
    	       "AND (r.payment_type IS NULL OR r.payment_type = 'cash') " +
    	       "GROUP BY g.genre_name " +
    	       "ORDER BY SUM(r.sales) DESC",
    	       nativeQuery = true)
    public List<Object[]> getYTDSalesByGenre();
	
    //반납일이 현재 시간과 24시간 후 사이인 대여 항목
    public List<RentEntity> findAllByStatusAndReturndateBetween(String string, LocalDateTime now, LocalDateTime tomorrow);
     
    
    //다음달 회원 등급 확인 위한 이번달 합계
    @Query("SELECT COALESCE(SUM(r.sales), 0) FROM rent r WHERE r.email.email = :email " +
    		   "AND (r.paymentType IS NULL OR r.paymentType = 'cash') " +
    	       "AND EXTRACT(YEAR FROM r.rentdate) = EXTRACT(YEAR FROM CURRENT_DATE) " +
    	       "AND EXTRACT(MONTH FROM r.rentdate) = EXTRACT(MONTH FROM CURRENT_DATE)")
    public int getThisMonthSales(@Param("email") String email);
    
    
    //cash로 결제했는지 point로 결제했는지 금액 및 비율 확인
    @Query("SELECT " +
    	       "SUM(CASE WHEN r.paymentType IS NULL OR r.paymentType = 'cash' THEN r.sales ELSE 0 END) AS cashSales, " +
    	       "SUM(CASE WHEN r.paymentType = 'mileage' THEN r.sales ELSE 0 END) AS pointSales, " +
    	       "SUM(r.sales) AS totalSales " +
    	       "FROM rent r " +
    	       "WHERE r.rentdate BETWEEN :startDate AND :endDate")
    public List<Object[]> getSalesDataForLastSevenDays(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
     
    
}
