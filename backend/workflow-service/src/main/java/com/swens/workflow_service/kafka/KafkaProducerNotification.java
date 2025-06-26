package com.swens.workflow_service.kafka;

import com.swens.events.TaskEventProto;
import com.swens.workflow_service.dto.WorkFlowUpdatedDTO;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.model.Workflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerNotification {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerNotification.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    // Kafka topic names
    private static final String TASK_ADDED_TOPIC = "workflow.updated";
    private static final String WORKFLOW_COMPLETED_TOPIC = "workflow.completed";

    public KafkaProducerNotification(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send task added message to Kafka using protobuf.
     */
    public void sendTaskAddedMessage(Workflow workflow, boolean taskExists) {
        if (workflow.getTasks() == null || workflow.getTasks().isEmpty()) {
            log.warn("No tasks found in workflow with ID {}", workflow.getWorkflowId());
            return;
        }

        String eventType = !taskExists ? "TASK_UPDATED" : "TASK_CREATED";

        for (Task task : workflow.getTasks()) {
            TaskEventProto.TaskEvent.Builder builder = TaskEventProto.TaskEvent.newBuilder()
                    .setTaskId(task.getTaskId())
                    .setEventType(eventType)
                    .setTaskName(task.getTaskName())
                    .setTaskStatus(task.getTaskStatus())
                    .setWorkflowId(workflow.getWorkflowId())
                    .setAdminEmail(task.getAdminEmail())
                    .setTimestamp(System.currentTimeMillis());

            if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
                for (Task.AssignedUser user : task.getAssignedUsers()) {
                    TaskEventProto.AssignedUser protoUser = TaskEventProto.AssignedUser.newBuilder()
                            .setUserId(user.getUserId())
                            .setUserName(user.getUserName())
                            .setEmail(user.getEmail())
                            .build();
                    builder.addAssignedUsers(protoUser);
                }
            }

            TaskEventProto.TaskEvent event = builder.build();

            try {
                kafkaTemplate.send(TASK_ADDED_TOPIC, event.toByteArray());
                log.info("Sent TASK_CREATED event for taskId {}", task.getTaskId());

                if (task.getAssignedUsers() != null && !task.getAssignedUsers().isEmpty()) {
                    task.getAssignedUsers().forEach(user ->
                            log.info("Assigned User: ID = {}, Name = {}, Email = {}",
                                    user.getUserId(), user.getUserName(), user.getEmail())
                    );
                } else {
                    log.info("No assigned users for taskId {}", task.getTaskId());
                }

            } catch (Exception e) {
                log.error("Error sending TASK_CREATED event for taskId {}: {}", task.getTaskId(), e.getMessage(), e);
            }
        }
    }



    public void sendWorkflowCompletedMessage(Workflow workflow) {
        if (workflow.getTasks() == null || workflow.getTasks().isEmpty()) {
            log.warn("No tasks available in workflow ID {} to send completion message.", workflow.getWorkflowId());
            return;
        }

        Task validTask = workflow.getTasks().stream()
                .filter(task -> task.getAdminEmail() != null && !task.getAdminEmail().trim().isEmpty())
                .findFirst()
                .orElse(null);

        if (validTask == null) {
            log.error("No valid task with adminEmail found in workflowId {}. Skipping message.", workflow.getWorkflowId());
            return;
        }

        TaskEventProto.TaskEvent.Builder builder = TaskEventProto.TaskEvent.newBuilder()
                .setTaskId(validTask.getTaskId())
                .setEventType("WORKFLOW_COMPLETED")
                .setTaskName(validTask.getTaskName())
                .setTaskStatus("Completed")
                .setWorkflowId(workflow.getWorkflowId())
                .setAdminEmail(workflow.getAdminEmail())
                .setTimestamp(System.currentTimeMillis());

        if (validTask.getAssignedUsers() != null && !validTask.getAssignedUsers().isEmpty()) {
            for (Task.AssignedUser user : validTask.getAssignedUsers()) {
                builder.addAssignedUsers(
                        TaskEventProto.AssignedUser.newBuilder()
                                .setUserId(user.getUserId())
                                .setUserName(user.getUserName())
                                .setEmail(user.getEmail())
                                .build()
                );
            }
        } else {
            log.info("No assigned users for taskId {}", validTask.getTaskId());
        }

        TaskEventProto.TaskEvent event = builder.build();

        try {
            kafkaTemplate.send(WORKFLOW_COMPLETED_TOPIC, event.toByteArray());
            log.info("Sent WORKFLOW_COMPLETED event for workflowId {}", workflow.getWorkflowId());
        } catch (Exception e) {
            log.error("Error sending WORKFLOW_COMPLETED event for workflowId {}: {}", workflow.getWorkflowId(), e.getMessage(), e);
        }
    }

}
