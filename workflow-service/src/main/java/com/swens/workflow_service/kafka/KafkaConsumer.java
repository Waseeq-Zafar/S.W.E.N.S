package com.swens.workflow_service.kafka;

import com.swens.events.TaskEventProto.TaskEvent;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.service.WorkflowService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @Autowired
    private WorkflowService workflowService;

    @KafkaListener(topics = {"task.created", "task.updated"}, groupId = "workflow-group")
    public void consumeTaskEvent(ConsumerRecord<String, byte[]> record) {
        try {
            byte[] data = record.value();

            // Parse protobuf binary data to TaskEvent object
            TaskEvent event = TaskEvent.parseFrom(data);


            // Map to your internal Task model
            Task task = new Task();
            task.setTaskId(event.getTaskId());
            task.setTaskName(event.getTaskName());
            task.setAssignedUserId(event.getAssignedUserId());
            task.setEventType(event.getEventType());
            task.setTaskStatus(event.getTaskStatus());
            task.setTimestamp(event.getTimestamp());

            // Since workflowId is not in proto, decide workflowId logic here
            String workflowId = "default-workflow"; // Or your custom logic here

            // Save or update the workflow with this task
            workflowService.addOrUpdateWorkflow(workflowId, task);

            System.out.println("Consumed and saved task event: " + event.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
