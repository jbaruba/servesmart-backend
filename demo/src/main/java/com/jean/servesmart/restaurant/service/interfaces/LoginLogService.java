package com.jean.servesmart.restaurant.service.interfaces;

import java.util.List;

import com.jean.servesmart.restaurant.dto.loginLog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.loginLog.LoginLogResponseDto;

public interface LoginLogService {

    void log(LoginLogCreateDto dto);

    List<LoginLogResponseDto> getByUser(Integer userId);
}
