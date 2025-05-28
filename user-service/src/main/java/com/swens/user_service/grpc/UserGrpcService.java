package com.swens.user_service.grpc;

import com.swens.grpc.UserRequest;
import com.swens.grpc.UserResponse;
import com.swens.grpc.UserServiceGrpc;
import com.swens.user_service.dto.UserLoginDTO;
import com.swens.user_service.service.UserService;  // Your service class in com.swens.user_service.service
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

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
}
