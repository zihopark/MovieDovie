package com.board.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "mileage_log")
@Table(name = "mileage_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MileageLogEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mileage_log_seq")
	@SequenceGenerator(name = "mileage_log_seq", sequenceName = "mileage_log_seq", allocationSize = 1, initialValue = 1)
	private Long id;

	//이메일
	@Column(name = "email")
	private String email;

	//제목
	@Column(name = "title")
	private String title;

	//결제 타입
	@Column(name = "payment_type")
	private String paymentType;

	//적립 금액
	@Column(name = "amount")
	private int amount;
	
	//적립 일자
	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
