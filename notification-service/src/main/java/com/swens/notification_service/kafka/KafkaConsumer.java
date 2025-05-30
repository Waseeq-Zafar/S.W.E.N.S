package com.swens.notification_service.kafka;

import com.swens.events.TaskEventProto;
import com.swens.notification_service.mail.EmailService;
import com.swens.notification_service.model.TaskEventModel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KafkaConsumer {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = {"workflow.updated", "workflow.completed"}, groupId = "workflow-group")
    public void consumeTaskEvent(ConsumerRecord<String, byte[]> record) {
        try {
            byte[] data = record.value();

            // Parse protobuf binary data to TaskEvent object
            TaskEventProto.TaskEvent event = TaskEventProto.TaskEvent.parseFrom(data);

            System.out.println("========== Received TaskEvent ==========");
            System.out.println("Task ID: " + event.getTaskId());
            System.out.println("Task Name: " + event.getTaskName());
            System.out.println("Event Type: " + event.getEventType());
            System.out.println("Task Status: " + event.getTaskStatus());
            System.out.println("Workflow ID: " + event.getWorkflowId());
            System.out.println("Timestamp (epoch ms): " + event.getTimestamp());

            // Print all assigned users from a protobuf message
            System.out.println("Assigned Users count: " + event.getAssignedUsersCount());
            for (TaskEventProto.AssignedUser protoUser : event.getAssignedUsersList()) {
                System.out.println(" - User ID: " + protoUser.getUserId());
                System.out.println("   User Name: " + protoUser.getUserName());
                System.out.println("   User Email: " + protoUser.getEmail());
            }
            System.out.println("=======================================");

            // Map to your TaskEventModel
            TaskEventModel taskEventModel = new TaskEventModel();
            taskEventModel.setTaskId(event.getTaskId());
            taskEventModel.setTaskName(event.getTaskName());
            taskEventModel.setEventType(event.getEventType());
            taskEventModel.setTaskStatus(event.getTaskStatus());
            taskEventModel.setWorkflowId(event.getWorkflowId());
            taskEventModel.setTimestamp(event.getTimestamp());

            List<TaskEventModel.AssignedUser> assignedUsers = event.getAssignedUsersList().stream().map(protoUser -> {
                TaskEventModel.AssignedUser user = new TaskEventModel.AssignedUser();
                user.setUserId(protoUser.getUserId());
                user.setUserName(protoUser.getUserName());
                user.setEmail(protoUser.getEmail());
                return user;
            }).collect(Collectors.toList());

            taskEventModel.setAssignedUsers(assignedUsers);

            // Convert timestamp to IST formatted string
            Instant instant = Instant.ofEpochMilli(taskEventModel.getTimestamp());
            ZoneId istZoneId = ZoneId.of("Asia/Kolkata");
            ZonedDateTime istDateTime = instant.atZone(istZoneId);
            String formattedTimestamp = istDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss z"));

            System.out.println("Timestamp in IST: " + formattedTimestamp);

            // Compose email content
            String subject = "Task Event Notification: " + taskEventModel.getTaskName();
            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("Task ID: ").append(taskEventModel.getTaskId()).append("\n");
            bodyBuilder.append("Task Name: ").append(taskEventModel.getTaskName()).append("\n");
            bodyBuilder.append("Event Type: ").append(taskEventModel.getEventType()).append("\n");
            bodyBuilder.append("Task Status: ").append(taskEventModel.getTaskStatus()).append("\n");
            bodyBuilder.append("Workflow ID: ").append(taskEventModel.getWorkflowId()).append("\n");
            bodyBuilder.append("Timestamp (IST): ").append(formattedTimestamp).append("\n\n");

            bodyBuilder.append("Assigned Users:\n");
            for (TaskEventModel.AssignedUser user : assignedUsers) {
                bodyBuilder.append(" - ").append(user.getUserName())
                        .append(" (").append(user.getEmail()).append(")\n");
            }

            String body = bodyBuilder.toString();

            // Send email to all assigned users
            for (TaskEventModel.AssignedUser user : assignedUsers) {
                System.out.println("Sending email to: " + user.getEmail());
                emailService.sendTaskNotification(user.getEmail(), subject, body);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
