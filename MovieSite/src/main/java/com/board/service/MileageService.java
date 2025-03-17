package com.board.service;

import java.util.List;

import com.board.entity.MileageLogEntity;

public interface MileageService {
	List<MileageLogEntity> getMileageLogs(String email);
}
