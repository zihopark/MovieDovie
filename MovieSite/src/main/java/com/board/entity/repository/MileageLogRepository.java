package com.board.entity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.entity.MileageLogEntity;

public interface MileageLogRepository extends JpaRepository<MileageLogEntity, Long> {

	List<MileageLogEntity> findAllByEmail(String email);

}
