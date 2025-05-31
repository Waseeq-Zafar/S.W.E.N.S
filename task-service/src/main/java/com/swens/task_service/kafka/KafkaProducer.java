package com.swens.task_service.kafka;

import com.swens.task_service.model.Task;
import com.swens.events.TaskEventProto.AssignedUser;
import com.swens.events.TaskEventProto.TaskEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskCreatedEvent(Task task) {
        TaskEvent.Builder builder = TaskEvent.newBuilder()
                .setTaskId(task.getTaskId())
                .setTaskName(task.getTaskName())
                .setEventType("TASK_CREATED")
                .setTaskStatus(task.getStatus())
                .setWorkflowId(task.getWorkflowId())
                .setTimestamp(System.currentTimeMillis());

        if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
            for (com.swens.task_service.model.Task.AssignedUser user : task.getAssignedUsers()) {
                AssignedUser protoUser = AssignedUser.newBuilder()
                        .setUserId(user.getUserId())
                        .setUserName(user.getUserName())
                        .setEmail(user.getEmail())
                        .build();
                builder.addAssignedUsers(protoUser);
            }
        }

        TaskEvent event = builder.build();

        try {
            kafkaTemplate.send("task.created", event.toByteArray());
            log.info("Sent TASK_CREATED event for taskId {}", task.getTaskId());

            if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
                task.getAssignedUsers().forEach(user -> {
                    log.info("Assigned User: ID = {}, Name = {}", user.getUserId(), user.getUserName());
                });
            } else {
                log.info("No assigned users for taskId {}", task.getTaskId());
            }

        } catch (Exception e) {
            log.error("Error sending TASK_CREATED event: {}", event, e);
        }
    }

    public void sendTaskUpdatedEvent(Task task) {
        TaskEvent.Builder builder = TaskEvent.newBuilder()
                .setTaskId(task.getTaskId())
                .setTaskName(task.getTaskName())
                .setEventType("TASK_UPDATED")
                .setTaskStatus(task.getStatus())
                .setWorkflowId(task.getWorkflowId())
                .setTimestamp(System.currentTimeMillis());

        if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
            for (com.swens.task_service.model.Task.AssignedUser user : task.getAssignedUsers()) {
                AssignedUser protoUser = AssignedUser.newBuilder()
                        .setUserId(user.getUserId())
                        .setUserName(user.getUserName())
                        .setEmail(user.getEmail())
                        .build();
                builder.addAssignedUsers(protoUser);
            }
        }

        TaskEvent event = builder.build();

        try {
            kafkaTemplate.send("task.updated", event.toByteArray());
            log.info("Sent TASK_UPDATED event for taskId {}", task.getTaskId());

            if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
                task.getAssignedUsers().forEach(user -> {
                    log.info("Assigned User - ID: {}, Name: {}", user.getUserId(), user.getUserName());
                });
            } else {
                log.info("No assigned users for taskId {}", task.getTaskId());
            }

        } catch (Exception e) {
            log.error("Error sending TASK_UPDATED event: {}", event, e);
        }
    }
}
