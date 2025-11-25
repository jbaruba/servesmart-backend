package com.jean.servesmart.restaurant.service.interfaces;

import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.LoginLog.LoginLogResponseDto;

import java.util.List;

public interface LoginLogService {

    void log(LoginLogCreateDto dto);

    List<LoginLogResponseDto> getByUser(Integer userId);
}
