package com.donorapi.models;

import com.donorapi.entity.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRegistrationRequest {
    private String username;
    private String phone;
    private String password;
    private UserRoles roles;
}
