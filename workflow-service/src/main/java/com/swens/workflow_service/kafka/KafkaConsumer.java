package com.swens.workflow_service.kafka;

import com.swens.events.TaskEventProto.TaskEvent;
import com.swens.workflow_service.dto.TaskEventDto;
import com.swens.workflow_service.mapper.WorkflowMapper;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.service.WorkflowService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {


    private final WorkflowService workflowService;
    private final WorkflowMapper workflowMapper;

    public KafkaConsumer(WorkflowService workflowService, WorkflowMapper workflowMapper) {
        this.workflowService = workflowService;
        this.workflowMapper = workflowMapper;
    }

    @KafkaListener(topics = {"task.created", "task.updated"}, groupId = "workflow-group")
    public void consumeTaskEvent(ConsumerRecord<String, byte[]> record) {
        try {
            byte[] data = record.value();

            // Parse protobuf binary data to TaskEvent object
            TaskEvent event = TaskEvent.parseFrom(data);


            // Map to your Task model dto
            TaskEventDto taskEventDto = new TaskEventDto();
            taskEventDto.setTaskId(event.getTaskId());
            taskEventDto.setTaskName(event.getTaskName());
            taskEventDto.setAssignedUserId(event.getAssignedUserId());
            taskEventDto.setEventType(event.getEventType());
            taskEventDto.setTaskStatus(event.getTaskStatus());
            taskEventDto.setWorkflowId(event.getWorkflowId());
            taskEventDto.setTimestamp(event.getTimestamp());

            // Since workflowId is not in proto, decide workflowId logic here
            String workflowId =  event.getWorkflowId(); // Or your custom logic here

            Task task = workflowMapper.mapDtoToTask(taskEventDto);

            // Save or update the workflow with this task
            workflowService.addOrUpdateWorkflow(workflowId, task);

            System.out.println("Consumed and saved task event: " + event.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
