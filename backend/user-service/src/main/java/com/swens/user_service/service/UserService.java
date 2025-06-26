package com.swens.user_service.service;


import com.swens.user_service.dto.TaskUserDTO;
import com.swens.user_service.dto.UserLoginDTO;
import com.swens.user_service.dto.UserRequestDTO;
import com.swens.user_service.dto.UserResponseDTO;
import com.swens.user_service.exception.EmailAlreadyExistsException;
import com.swens.user_service.exception.EmailNotFoundException;
import com.swens.user_service.mapper.UserMapper;
import com.swens.user_service.model.Users;
import com.swens.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if(userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("A user with this email " + "already exists" + userRequestDTO.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        userRequestDTO.setPassword(encodedPassword);
        Users users = userRepository.save(UserMapper.toModel(userRequestDTO));
        return UserMapper.toDTO(users);
    }

    public UserLoginDTO getUserByEmail(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("User not found with email: " + email));

        return new UserLoginDTO(users.getName(), users.getPassword(), users.getRole());
    }


    public List<TaskUserDTO> getUsersByRole(String role) {
        List<Users> usersList = userRepository.findByRole(role);

        return usersList.stream().map(user -> {
            TaskUserDTO dto = new TaskUserDTO();
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            return dto;
        }).toList();
    }
}
