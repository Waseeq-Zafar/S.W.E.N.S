package com.swens.workflow_service.repository;

import com.swens.workflow_service.model.Workflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkflowRepository extends MongoRepository<Workflow, String> {
    Workflow findByWorkflowId(String workflowId);
}
