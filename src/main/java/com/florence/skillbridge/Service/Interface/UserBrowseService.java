package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Request.UserFilterDTO;
import com.florence.skillbridge.DTO.Response.OtherProfileDTO;
import com.florence.skillbridge.DTO.Response.PagedUsersDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;

public interface UserBrowseService {
    PagedUsersDTO getFilteredUsers(UserFilterDTO userFilterDTO, UserDTO userDTO);
    OtherProfileDTO getOtherProfile(int id);
}
