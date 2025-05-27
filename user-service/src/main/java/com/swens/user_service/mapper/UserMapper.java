package com.swens.user_service.mapper;

import com.swens.user_service.dto.UserRequestDTO;
import com.swens.user_service.dto.UserResponseDTO;
import com.swens.user_service.model.Users;

public class UserMapper {
    public static UserResponseDTO toDTO(Users users) {
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setId(users.getId().toString());
        userDTO.setEmail(users.getEmail());
        userDTO.setPassword(users.getPassword());
        return userDTO;
    }

    public static Users toModel(UserRequestDTO userRequestDTO) {
        Users users = new Users();
        users.setName(userRequestDTO.getName());
        users.setEmail(userRequestDTO.getEmail());
        users.setPassword(userRequestDTO.getPassword());
        return users;
    }


}
