package com.jean.servesmart.restaurant.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private Boolean active; 
    private String role;    
}
