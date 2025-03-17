package com.board.entity.movie;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="company")
@Table(name="company")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="COMPANY_SEQ")
	@SequenceGenerator(name="COMPANY_SEQ",sequenceName = "company_seq",initialValue = 1,allocationSize = 1)
	private Long seqno;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="movieId")
	private MovieEntity movieId;

	@Column(name="companyid")
	private Long companyId;
	
	private String logoPath;

	@Column(name="company_name")
	private String companyNm;
	
	private String originCountry;
}
