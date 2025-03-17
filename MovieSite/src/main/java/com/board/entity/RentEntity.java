package com.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.board.entity.movie.MovieEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="rent")
@Table(name="rent")
public class RentEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RENT_SEQ")
	@SequenceGenerator(name="RENT_SEQ", sequenceName = "rent_seq", initialValue = 1, allocationSize = 1)
	private long seqno;
	
	@Column(name="rentdate",nullable=false)
	private LocalDateTime rentdate;
	
	@Column(name="returndate",nullable=false)
	private LocalDateTime returndate;
	
	@Column(name = "status", length=2, nullable = false)
	private String status;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="email")
	private MemberEntity email;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;
	
	@Column(name="payment_type")
	private String paymentType; //구매 방법 ('CASH' 혹은 'POINT')

	@Column(name="sales", columnDefinition = "integer default 0")
	private int sales;
	
	/*
	//FK 만들기
	//FK 읽어올 때 Eager, Lazy 두 가지 타입이 있음.
	//Eager : 부모키가 있는 테이블부터 검사해서 부모키가 제대로 되어 있는지 확인하고 자식키를 읽음 -> 정확도는 높지만 성능이 저하.
	//Lazy : 자식키가 있는 테이블만 읽음 -> 정확도는 떨어지지만 성능이 향상.
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="email", nullable = false)
	private MemberEntity email;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "movieseqno", referencedColumnName = "seqno", nullable = false)
	private MovieEntity movieseqno;
	*/
}
