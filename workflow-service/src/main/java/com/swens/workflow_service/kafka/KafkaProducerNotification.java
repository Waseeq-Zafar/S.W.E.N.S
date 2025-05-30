package com.swens.workflow_service.kafka;

import com.swens.workflow_service.dto.WorkFlowUpdatedDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerNotification {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerNotification.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Kafka topic names
    private static final String TASK_ADDED_TOPIC = "workflow.updated";
    private static final String WORKFLOW_COMPLETED_TOPIC = "workflow.completed";

    public KafkaProducerNotification(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    public void sendTaskAddedMessage(WorkFlowUpdatedDTO dto, String userName, String email) {
        try {
            // Add user info to dto if needed or create a wrapper
            TaskNotificationMessage message = new TaskNotificationMessage(dto, userName, email);

            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TASK_ADDED_TOPIC, dto.getTaskId(), jsonMessage);
            logger.info("Sent task added message for taskId {} to topic {}", dto.getTaskId(), TASK_ADDED_TOPIC);
        } catch (Exception e) {
            logger.error("Failed to send task added message", e);
        }
    }


    public void sendWorkflowCompletedMessage(String workflowId) {
        try {
            kafkaTemplate.send(WORKFLOW_COMPLETED_TOPIC, workflowId, "Workflow completed: " + workflowId);
            logger.info("Sent workflow completed message for workflowId {} to topic {}", workflowId, WORKFLOW_COMPLETED_TOPIC);
        } catch (Exception e) {
            logger.error("Failed to send workflow completed message", e);
        }
    }

    // Inner class representing the structure of a task notification message
    private static class TaskNotificationMessage {
        private final WorkFlowUpdatedDTO task;
        private final String userName;
        private final String email;

        public TaskNotificationMessage(WorkFlowUpdatedDTO task, String userName, String email) {
            this.task = task;
            this.userName = userName;
            this.email = email;
        }

        public WorkFlowUpdatedDTO getTask() {
            return task;
        }

        public String getUserName() {
            return userName;
        }

        public String getEmail() {
            return email;
        }
    }
}
