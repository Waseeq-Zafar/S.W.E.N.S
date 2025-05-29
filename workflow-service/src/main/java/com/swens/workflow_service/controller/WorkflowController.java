package com.swens.workflow_service.controller;


import com.swens.workflow_service.model.Workflow;
import com.swens.workflow_service.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {
    private final WorkflowService workflowService;
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // POST /workflow
    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        return ResponseEntity.ok(workflowService.createWorkflow(workflow));
    }

    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflows = workflowService.getAllWorkflows();
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }

}
