package com.swens.workflow_service.service;


import com.swens.workflow_service.model.Task;
import com.swens.workflow_service.model.Workflow;
import com.swens.workflow_service.repository.WorkflowRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkflowService {
    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    // Add or update a workflow with a new task
    public Workflow addOrUpdateWorkflow(String workflowId, Task task) {
        Workflow workflow = workflowRepository.findById(workflowId).orElse(null);

        if (workflow == null) {
            workflow = new Workflow();
            workflow.setWorkflowId(workflowId);  // MongoDB ID
            workflow.setTasks(new ArrayList<>());
            workflow.setCreatedAt(System.currentTimeMillis());
        }

        workflow.getTasks().add(task);
        workflow.setUpdatedAt(System.currentTimeMillis());

        return workflowRepository.save(workflow);
    }

    public Workflow createWorkflow(Workflow workflow) {
        // Set creation and update timestamps if not already set
        long now = System.currentTimeMillis();
        if (workflow.getCreatedAt() == 0) {
            workflow.setCreatedAt(now);
        }
        workflow.setUpdatedAt(now);

        // Initialize task list if null
        if (workflow.getTasks() == null) {
            workflow.setTasks(new ArrayList<>());
        }

        // Save the new workflow to the repository
        return workflowRepository.save(workflow);
    }


    public List<Workflow> getAllWorkflows() {
        return workflowRepository.findAll();
    }
}
