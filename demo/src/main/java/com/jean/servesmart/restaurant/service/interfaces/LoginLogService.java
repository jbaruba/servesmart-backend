package com.jean.servesmart.restaurant.service.interfaces;

import java.util.List;

import com.jean.servesmart.restaurant.dto.loginlog.LoginLogCreateDto;
import com.jean.servesmart.restaurant.dto.loginlog.LoginLogResponseDto;

public interface LoginLogService {

    void log(LoginLogCreateDto dto);

    List<LoginLogResponseDto> getByUser(Integer userId);
}
