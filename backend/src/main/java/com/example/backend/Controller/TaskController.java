package com.example.backend.Controller;

import com.example.backend.Entity.Commit;
import com.example.backend.Entity.Pull;
import com.example.backend.Entity.Task;
import com.example.backend.Repository.CommitRepository;
import com.example.backend.Repository.PullRepository;
import com.example.backend.Repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskRepository taskRepository;
    private final CommitRepository commitRepository;
    private final PullRepository pullRepository;


    public TaskController(TaskRepository taskRepository , CommitRepository commitRepository ,PullRepository pullRepository) {
        this.taskRepository = taskRepository;
        this.commitRepository = commitRepository;
        this.pullRepository =pullRepository;
    }









    @GetMapping("/repo/{repoName}")
    public ResponseEntity<List<Task>> getTasksByRepository(@PathVariable String repoName) {
        // 1. Charger les commits du repo
        List<Commit> commits = commitRepository.findAll()
                .stream()
                .filter(commit -> repoName.equals(commit.getRepositoryName()))
                .toList();

        List<Pull> pulls = pullRepository.findAll().stream()
                .filter(pull -> repoName.equalsIgnoreCase(pull.getRepositoryName().trim()))
                .toList();

        // 2. Charger les tâches du repo
        List<Task> tasks = taskRepository.findByRepositoryName(repoName);

        // 3. Pour chaque tâche TO_DO, vérifier s’il y a un commit dans la même branche
        for (Task task : tasks) {
            if ("TO_DO".equals(task.getStatus())) {
                boolean hasCommit = commits.stream()
                        .anyMatch(commit -> commit.getBranchName().equals(task.getBranchName()));

                if (hasCommit) {
                    task.setStatus("IN_PROGRESS");
                    taskRepository.save(task);
                }
            }
            // Passer à DONE si un pull du même auteur et même repo, mergé
            if ("IN_PROGRESS".equals(task.getStatus())) {
                boolean hasMergedPull = pulls.stream()
                        .anyMatch(pull ->
                                pull.getAuthor().trim().equalsIgnoreCase(task.getAuthor().trim()) &&
                                        pull.getMergedAt() != null &&
                                        pull.getRepositoryName().trim().equalsIgnoreCase(task.getRepositoryName().trim())
                        );

                if (hasMergedPull) {
                    task.setStatus("DONE");
                    taskRepository.save(task);
                }
            }
        }

        return ResponseEntity.ok(taskRepository.findByRepositoryName(repoName));
    }







    @PostMapping
        public Task createTask(@RequestBody Task task) {
            return taskRepository.save(task);
        }





    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setName(updatedTask.getName());
            task.setDescription(updatedTask.getDescription());
            task.setBranchName(updatedTask.getBranchName());
            task.setAuthor(updatedTask.getAuthor());
            task.setStatus(updatedTask.getStatus());
            task.setRepositoryName(updatedTask.getRepositoryName());

            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }





    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

