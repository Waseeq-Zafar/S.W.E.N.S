package com.swens.task_service.grpc;

import com.swens.grpc.RoleRequest;
import com.swens.grpc.UserInfo;
import com.swens.grpc.UsersResponse;
import com.swens.grpc.UserServiceGrpc;
import com.swens.task_service.dto.UserInfoDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class UserServiceGrpcClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceGrpcClient.class);

    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public UserServiceGrpcClient(@Value("${grpc.client.userService.address}") String url) {
        logger.info("Initializing gRPC client with URL: {}", url);
        try {
            String cleanedUrl = url.replace("static://", "");
            String[] parts = cleanedUrl.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid gRPC URL format. Expected host:port");
            }

            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            this.channel = ManagedChannelBuilder
                    .forAddress(host, port)
                    .usePlaintext()
                    .build();

            this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
            logger.info("gRPC client initialized for {}:{}", host, port);
        } catch (Exception e) {
            logger.error("Failed to initialize gRPC client: {}", e.getMessage(), e);
            throw new RuntimeException("gRPC client init failed", e);
        }
    }

    /**
     * Fetches all users by role via gRPC.
     *
     * @param role the role to filter users (e.g., "admin", "user")
     * @return UsersResponse containing a list of users with the given role
     */
    public List<UserInfoDTO> getUsersByRole(String role) {
        RoleRequest request = RoleRequest.newBuilder()
                .setRole(role)
                .build();

        try {
            logger.info("Sending gRPC request to get users by role: {}", role);
            UsersResponse response = blockingStub.getUsersByRole(request);

            return response.getUsersList().stream()
                    .map(userInfo -> new UserInfoDTO(
                            userInfo.getId(),
                            userInfo.getName(),
                            userInfo.getEmail(),
                            userInfo.getRole()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("gRPC call to get users by role '{}' failed: {}", role, e.getMessage(), e);
            return List.of();
        }
    }


    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down gRPC channel");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("Channel did not terminate in time, forcing shutdown");
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.warn("Channel shutdown interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
