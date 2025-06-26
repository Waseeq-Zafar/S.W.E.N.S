package com.swens.workflow_service.kafka;

import com.swens.events.TaskEventProto.TaskEvent;
import com.swens.workflow_service.dto.TaskEventDTO;
import com.swens.workflow_service.mapper.WorkflowMapper;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.service.WorkflowService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

            // Map to your TaskEventDTO
            TaskEventDTO taskEventDto = new TaskEventDTO();
            taskEventDto.setTaskId(event.getTaskId());
            taskEventDto.setTaskName(event.getTaskName());
            taskEventDto.setEventType(event.getEventType());
            taskEventDto.setTaskStatus(event.getTaskStatus());
            taskEventDto.setWorkflowId(event.getWorkflowId());
            taskEventDto.setTimestamp(event.getTimestamp());
            taskEventDto.setAdminEmail(event.getAdminEmail());




            // Map assigned users from proto to DTO
            List<TaskEventDTO.AssignedUserDTO> assignedUsers = event.getAssignedUsersList().stream().map(protoUser -> {
                TaskEventDTO.AssignedUserDTO userDto = new TaskEventDTO.AssignedUserDTO();
                userDto.setUserId(protoUser.getUserId());
                userDto.setUserName(protoUser.getUserName());
                userDto.setEmail(protoUser.getEmail());
                return userDto;
            }).collect(Collectors.toList());

            taskEventDto.setAssignedUsers(assignedUsers);

            String workflowId = event.getWorkflowId(); // your logic here

            Task task = workflowMapper.mapDtoToTask(taskEventDto);

            workflowService.addOrUpdateWorkflow(workflowId, task);

            System.out.println("Consumed and saved task event: " + event.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
