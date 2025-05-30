package com.swens.workflow_service.service;

import com.swens.workflow_service.dto.WorkFlowUpdatedDTO;
import com.swens.workflow_service.dto.WorkflowResponseDTO;
import com.swens.workflow_service.exception.WorkflowNotFoundException;
import com.swens.workflow_service.kafka.KafkaProducerNotification;
import com.swens.workflow_service.mapper.WorkflowMapper;
import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.model.Workflow;
import com.swens.workflow_service.repository.WorkflowRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;
    private final KafkaProducerNotification kafkaProducerNotification;

    public WorkflowService(
            WorkflowRepository workflowRepository,
            WorkflowMapper workflowMapper,
            KafkaProducerNotification kafkaProducerNotification) {
        this.workflowRepository = workflowRepository;
        this.workflowMapper = workflowMapper;
        this.kafkaProducerNotification = kafkaProducerNotification;
    }

    /**
     * Add a new task to an existing workflow or update an existing task.
     * Also sends task update notifications.
     *
     * @param workflowId the workflow identifier
     * @param task       the task to add or update
     */
    public void addOrUpdateWorkflow(String workflowId, Task task) {
        // Fetch workflow by workflowId (custom business id)
        Workflow workflow = workflowRepository.findByWorkflowId(workflowId);

        if (workflow == null) {
            throw new WorkflowNotFoundException("Workflow with ID " + workflowId + " does not exist.");
        }

        // Map the task to WorkFlowUpdatedDTOs (assuming a list, maybe for subtasks or steps)
        List<WorkFlowUpdatedDTO> dtos = workflowMapper.toWorkFlowUpdatedDTO(task);
        List<Task.AssignedUser> assignedUsers = task.getAssignedUsers();

        // Send notifications with user info if lists align
        if (assignedUsers != null && dtos != null && assignedUsers.size() == dtos.size()) {
            for (int i = 0; i < dtos.size(); i++) {
                WorkFlowUpdatedDTO dto = dtos.get(i);
                Task.AssignedUser user = assignedUsers.get(i);
                kafkaProducerNotification.sendTaskAddedMessage(dto, user.getUserName(), user.getEmail());
            }
        }

        // Update or add task in workflow tasks list
        List<Task> tasks = workflow.getTasks();
        if (tasks == null) {
            tasks = new ArrayList<>();
            workflow.setTasks(tasks);
        }

        boolean taskExists = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskId().equals(task.getTaskId())) {
                tasks.set(i, task);
                taskExists = true;
                break;
            }
        }
        if (!taskExists) {
            tasks.add(task);
        }

        // Update workflow metadata
        workflow.setUpdatedAt(System.currentTimeMillis());

        // Calculate completion percentage and update
        int completion = calculateWorkflowCompletion(workflow);
        workflow.setCompletionPercentage(completion);

        workflowRepository.save(workflow);

        // Optionally send workflow completion event if completed 100%
        if (completion == 100) {
            kafkaProducerNotification.sendWorkflowCompletedMessage(workflow.getWorkflowId());
        }
    }

    public WorkflowResponseDTO createWorkflow() {
        String uniqueWorkflowId = "workflow-" + UUID.randomUUID();
        long now = System.currentTimeMillis();

        Workflow workflow = new Workflow();
        workflow.setWorkflowId(uniqueWorkflowId);
        workflow.setCreatedAt(now);
        workflow.setUpdatedAt(now);
        workflow.setCompletionPercentage(100);
        workflow.setTasks(new ArrayList<>());

        Workflow savedWorkflow = workflowRepository.save(workflow);

        // Map entity to DTO before returning
        return workflowMapper.toResponseDTO(savedWorkflow);
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public Workflow getWorkflowByWorkflowId(String workflowId) {
        Workflow workflow = workflowRepository.findByWorkflowId(workflowId);
        if (workflow == null) {
            return null;
        }
        int completion = calculateWorkflowCompletion(workflow);
        workflow.setCompletionPercentage(completion);
        return workflow;
    }

    public int calculateWorkflowCompletion(Workflow workflow) {
        List<Task> tasks = workflow.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            return 0;
        }

        long completedCount = tasks.stream()
                .filter(task -> {
                    String status = task.getTaskStatus();
                    return status != null && status.equalsIgnoreCase("completed");
                })
                .count();

        return (int) ((completedCount * 100) / tasks.size());
    }
}
