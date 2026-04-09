package com.florence.skillbridge.Service.Interface;

import com.florence.skillbridge.DTO.Request.AddUserInterestDTO;
import com.florence.skillbridge.DTO.Request.AddUserSkillDTO;
import com.florence.skillbridge.DTO.Response.MeDTO;
import com.florence.skillbridge.DTO.Response.UserDTO;

public interface ProfileService {
    MeDTO getMyInfo(UserDTO userDTO);

    void addSkillToUser(UserDTO userDTO, AddUserSkillDTO skillDTO);

    void addInterestToUser(UserDTO userDTO, AddUserInterestDTO interestDTO);

    void deleteSkillToUser(int skillId, UserDTO userDTO);

    void deleteInterestToUser(int skillId, UserDTO userDTO);
}
