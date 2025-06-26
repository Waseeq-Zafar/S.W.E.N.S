package com.swens.user_service.grpc;

import com.swens.grpc.*;
import com.swens.user_service.dto.TaskUserDTO;
import com.swens.user_service.dto.UserLoginDTO;
import com.swens.user_service.service.UserService;  // Your service class in com.swens.user_service.service
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    public UserGrpcService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getUserCredentials(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        String email = request.getEmail();

        UserLoginDTO user = userService.getUserByEmail(email);

        UserResponse.Builder responseBuilder = UserResponse.newBuilder();

        if (user != null) {
            responseBuilder
                    .setName(user.getName())
                    .setHashedPassword(user.getPassword())
                    .setRole(user.getRole());
        } else {
            responseBuilder.setName("");
            responseBuilder.setHashedPassword("");
            responseBuilder.setRole("");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    // For task service

    @Override
    public void getUsersByRole(RoleRequest request, StreamObserver<UsersResponse> responseObserver) {
        String role = request.getRole();

        // Get users from your service layer (returning TaskUserDTOs)
        List<TaskUserDTO> taskUsers = userService.getUsersByRole(role);

        // Build gRPC response
        UsersResponse.Builder responseBuilder = UsersResponse.newBuilder();

        for (TaskUserDTO user : taskUsers) {
            UserInfo userInfo = UserInfo.newBuilder()
                    .setId(user.getUserId().toString())
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .setRole(user.getRole())
                    .build();

            responseBuilder.addUsers(userInfo);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
