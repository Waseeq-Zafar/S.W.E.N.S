package com.swens.task_service.repository;

import com.swens.task_service.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    // Custom finder: get tasks by status
    List<Task> findByStatus(String status);

    // Custom finder: get tasks where a user is assigned
    List<Task> findByAssignedUsersUserId(String userId);

    List<Task> findByAssignedUsersEmail(String email);

}
