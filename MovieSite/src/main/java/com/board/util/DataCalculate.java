package com.board.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class DataCalculate {
	
	//개봉날짜에 따른 대여 비용 계산
	public static int calcRentPrice(LocalDate release) {
        LocalDate today = LocalDate.now();
        long daysSinceRelease = ChronoUnit.DAYS.between(release, today);
        
        int basePrice = 5000; // 기본 대여 가격
        int result=0;

        if (daysSinceRelease < 30) {
            // 개봉 1개월 이내: 기본 가격의 150%
            result = (int)(basePrice * 2);
            // 개봉 1-3개월: 기본 가격의 120%
            result = (int)(basePrice * 1.2);
        } else if (daysSinceRelease < 180) {
            // 개봉 3-6개월: 기본 가격
            result = basePrice;
        } else if (daysSinceRelease < 365) {
            // 개봉 6개월-1년: 기본 가격의 80%
            result = (int)(basePrice * 0.8);
        } else {
            // 개봉 1년 이상: 기본 가격의 50%
            result = (int)(basePrice * 0.2);
        }

        return result;
    }

    //등급에 따른 마일리지 추가
    public static int calcMileage(String grade, int price) {
        int result = 0;

        switch (grade) {
            case "DIAMOND":
                result = (int)(price * 0.1);
            break;
            case "PLATINUM":
                result = (int)(price * 0.05);
            break;  
            case "GOLD":                    
                result = (int)(price * 0.03);
            break;
            case "SILVER":
                result = (int)(price * 0.02);
            break;
            default:
                result = (int)(price * 0.01);
                break;
        }
        return result;
    }

    //매출액에 따른 등급 계산
    //금액 필터링
    public static String calcGrade(int totalSales) {
        String grade = "BRONZE";

        //등급 계산
        if(totalSales >= 50000) {
            grade = "DIAMOND";
        } else if(totalSales >= 30000) {
            grade = "PLATINUM";
        } else if(totalSales >= 10000) {
            grade = "GOLD";
        } else if(totalSales >= 5000) {
            grade = "SILVER";
        }   else{
            grade = "BRONZE";
        }

        return grade;
    }   

    //나이 계산
    public static int calcAge(String birthdate) {
        int year = Integer.parseInt(birthdate.substring(0, 4)); // 4자리 연도 추출
        int month = Integer.parseInt(birthdate.substring(4, 6)); // 월 추출
        int day = Integer.parseInt(birthdate.substring(6, 8)); // 일 추출

        // 나이 계산
        LocalDate birthDate = LocalDate.of(year, month, day);
        int age = LocalDate.now().getYear() - birthDate.getYear();

        // 생일이 아직 지나지 않았다면 나이를 1살 줄임
        if (LocalDate.now().getMonthValue() < month ||
            (LocalDate.now().getMonthValue() == month && LocalDate.now().getDayOfMonth() < day)) {
            age--;
        }

        return age;
    }
  



    public static boolean calcIsAdult(String certification,int age) {
        if(certification == null) {
        	return age<19? false:true;
        }
        switch (certification) {    
            case "19+":
            case "19":
            return age>=19?true:false;
            case "18":
            return age>=18?true:false;
            case "15":
            return age>=15?true:false;
            case "12":
            return age>=12?true:false;
            case "ALL":
            case "All":
                return true;
            default:
                return true;
        }
    }

    //주차 계산
    public static int getWeekOfMonth(LocalDateTime date) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);
        return date.get(weekFields.weekOfMonth());
    }
}
