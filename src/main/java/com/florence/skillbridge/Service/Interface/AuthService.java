package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Request.LoginDTO;
import com.florence.skillbridge.DTO.Request.RegisterDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;

public interface AuthService {
    UserDTO login(LoginDTO loginDTO);
    void register(RegisterDTO registerDTO);
}
