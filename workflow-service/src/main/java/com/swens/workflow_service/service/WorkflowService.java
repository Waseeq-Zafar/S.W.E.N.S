package com.swens.workflow_service.service;


import com.swens.workflow_service.dto.WorkflowResponseDTO;
import com.swens.workflow_service.exception.WorkflowNotFoundException;
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

    public WorkflowService(WorkflowRepository workflowRepository, WorkflowMapper workflowMapper) {
        this.workflowRepository = workflowRepository;
        this.workflowMapper = workflowMapper;
    }

    // Add or update a workflow with a new task
    public void addOrUpdateWorkflow(String workflowId, Task task) {
        // Fetch workflow by workflowId (custom field), not MongoDB ID (_id)
        Workflow workflow = workflowRepository.findByWorkflowId(workflowId);

        if (workflow == null) {
            throw new WorkflowNotFoundException("Workflow with ID " + workflowId + " does not exist.");
        }

        List<Task> tasks = workflow.getTasks();
        if (tasks == null) {
            tasks = new ArrayList<>();
            workflow.setTasks(tasks);
        }

        // Check if the task already exists (by taskId)
        boolean taskExists = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTaskId().equals(task.getTaskId())) {
                tasks.set(i, task);  // Update existing task
                taskExists = true;
                break;
            }
        }

        // If a task doesn't exist, add a new one
        if (!taskExists) {
            tasks.add(task);
        }

        workflow.setUpdatedAt(System.currentTimeMillis());

        // Calculate and store the completion percentage if you have a field for it
        int completion = calculateWorkflowCompletion(workflow);

        workflow.setCompletionPercentage(completion);

        workflowRepository.save(workflow);
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

        // Mapping entity to DTO here inside the service
        return workflowMapper.toResponseDTO(savedWorkflow);
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }

    public int calculateWorkflowCompletion(Workflow workflow) {
        List<Task> tasks = workflow.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            return 100;  // or 100, if an empty workflow means fully done
        }

        long completedCount = tasks.stream()
                .filter(task -> "COMPLETE".equalsIgnoreCase(task.getTaskStatus()))
                .count();

        return (int) ((completedCount * 100) / tasks.size());
    }

    public Workflow getWorkflowByWorkflowId(String workflowId) {
        return workflowRepository.findByWorkflowId(workflowId);
    }
}
