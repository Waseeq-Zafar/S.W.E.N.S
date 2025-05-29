package com.swens.task_service.kafka;

import com.swens.task_service.model.Task;
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
        TaskEvent event = TaskEvent.newBuilder()
                .setTaskId(task.getTaskId())
                .setTaskName(task.getDescription()) // using description as task name
                .setAssignedUserId(getFirstAssignedUserId(task))
                .setEventType("TASK_CREATED")
                .setTimestamp(System.currentTimeMillis())
                .build();

        try {
            kafkaTemplate.send("task.created", event.toByteArray());
            log.info("Sent TASK_CREATED event for taskId {}", task.getTaskId());
        } catch (Exception e) {
            log.error("Error sending TASK_CREATED event: {}", event, e);
        }
    }

    public void sendTaskUpdatedEvent(Task task) {
        TaskEvent event = TaskEvent.newBuilder()
                .setTaskId(task.getTaskId())
                .setTaskName(task.getDescription()) // using description as task name
                .setAssignedUserId(getFirstAssignedUserId(task))
                .setEventType("TASK_UPDATED")
                .setTimestamp(System.currentTimeMillis())
                .build();

        try {
            kafkaTemplate.send("task.updated", event.toByteArray());
            log.info("Sent TASK_UPDATED event for taskId {}", task.getTaskId());
        } catch (Exception e) {
            log.error("Error sending TASK_UPDATED event: {}", event, e);
        }
    }

    private String getFirstAssignedUserId(Task task) {
        if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
            return task.getAssignedUsers().get(0).getUserId();
        }
        return "UNKNOWN";
    }
}
