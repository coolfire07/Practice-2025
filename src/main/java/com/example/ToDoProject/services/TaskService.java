package com.example.ToDoProject.services;

import com.example.ToDoProject.entities.Task;
import com.example.ToDoProject.entities.TaskStatus;
import com.example.ToDoProject.repositories.TaskRepository;
import com.example.ToDoProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Task> getTasks(TaskStatus status, LocalDate completionDate, String keyword, Long userId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getUser().getId().equals(userId))
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> completionDate == null || task.getCompletionDate().equals(completionDate))
                .filter(task -> keyword == null || task.getTaskName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }


    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id) {
        System.out.println("Попытка удалить задачу с ID: " + id);
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task updateTask(Long id, Task task) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task taskToUpdate = existingTask.get();
            taskToUpdate.setTaskName(task.getTaskName());
            taskToUpdate.setDescription(task.getDescription());
            taskToUpdate.setCompletionDate(task.getCompletionDate());
            taskToUpdate.setStatus(task.getStatus());
            return taskRepository.save(taskToUpdate);
        }
        return null;
    }
}