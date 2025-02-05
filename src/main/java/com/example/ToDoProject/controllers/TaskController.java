package com.example.ToDoProject.controllers;

import com.example.ToDoProject.entities.Task;
import com.example.ToDoProject.entities.TaskStatus;
import com.example.ToDoProject.entities.User;
import com.example.ToDoProject.repositories.UserRepository;
import com.example.ToDoProject.services.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public String getTasks(@RequestParam(required = false) TaskStatus status,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completionDate,
                           @RequestParam(required = false) String keyword,
                           HttpSession session,
                           Model model) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        List<Task> tasks = taskService.getTasks(status, completionDate, keyword, userId);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }


    @GetMapping("/api/tasks")
    @ResponseBody
    public ResponseEntity<List<Task>> getTasksJson(@RequestParam(required = false) TaskStatus status,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completionDate,
                                                   @RequestParam(required = false) String keyword,
                                                   HttpSession session) {

        try {
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            List<Task> tasks = taskService.getTasks(status, completionDate, keyword, userId);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        task.setUser(user);
        Task savedTask = taskService.saveTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(@PathVariable Long id) {
        System.out.println("DELETE запрос получен для ID: " + id);

        if (taskService.deleteTask(id)) {
            System.out.println("Задача удалена успешно");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Task deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("Задача не найдена");

            Map<String, String> response = new HashMap<>();
            response.put("error", "Task not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        if (updatedTask != null) {
            return ResponseEntity.ok(updatedTask);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
