package com.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.board.entity.MileageLogEntity;
import com.board.entity.repository.MileageLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MileageServiceImpl implements MileageService {
	private final MileageLogRepository mileageLogRepository;

	@Override
	public List<MileageLogEntity> getMileageLogs(String email) {
		return mileageLogRepository.findAllByEmail(email);
	}
}
