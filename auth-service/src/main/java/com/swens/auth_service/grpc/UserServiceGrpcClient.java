package com.swens.auth_service.grpc;

import com.swens.grpc.UserRequest;
import com.swens.grpc.UserResponse;
import com.swens.grpc.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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

    public UserResponse getUserCredentials(String email) {
        UserRequest request = UserRequest.newBuilder()
                .setEmail(email)
                .build();

        try {
            logger.info("Sending gRPC request to user_service for email: {}", email);
            return blockingStub.getUserCredentials(request);
        } catch (Exception e) {
            logger.error("gRPC call failed for email {}: {}", email, e.getMessage(), e);
            return null;
        }
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down gRPC channel");
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warn("Channel did not terminate, forcing shutdown");
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.warn("Shutdown interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
