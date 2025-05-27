package com.swens.user_service.service;


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

        return new UserLoginDTO(users.getName(), users.getPassword());
    }







}
