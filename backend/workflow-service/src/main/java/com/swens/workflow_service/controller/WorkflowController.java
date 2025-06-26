package com.swens.workflow_service.controller;


import com.swens.workflow_service.dto.WorkflowResponseDTO;
import com.swens.workflow_service.model.Workflow;
import com.swens.workflow_service.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<WorkflowResponseDTO> createEmptyWorkflow(@RequestBody String name) {
        WorkflowResponseDTO responseDTO = workflowService.createWorkflow(name);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.getWorkflowId()) // assuming getId() gives the workflow's ID
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }


    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        List<Workflow> workflows = workflowService.getAllWorkflows();
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }

    @GetMapping("/{workflowId}")
    public ResponseEntity<Workflow> getWorkflowByWorkflowId(@PathVariable String workflowId) {
        Workflow workflow = workflowService.getWorkflowByWorkflowId(workflowId);
        if (workflow == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(workflow, HttpStatus.OK);
    }

}
