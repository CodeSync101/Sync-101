package com.example.backend.Controller;

import com.example.backend.Entity.Branch;
import com.example.backend.Entity.Commit;
import com.example.backend.Entity.Pull;
import com.example.backend.Entity.Task;
import com.example.backend.Repository.BranchRepository;
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
    private final BranchRepository branchRepository;


    public TaskController(TaskRepository taskRepository , CommitRepository commitRepository , PullRepository pullRepository, BranchRepository branchRepository) {
        this.taskRepository = taskRepository;
        this.commitRepository = commitRepository;
        this.pullRepository =pullRepository;
        this.branchRepository = branchRepository;
    }









    @GetMapping("/repo/{repoName}")
    public ResponseEntity<List<Task>> getTasksByRepository(@PathVariable String repoName) {
       List<Task> tasks= taskRepository.findByRepositoryName(repoName);
       tasks.forEach(task -> {
        List<Branch>  branches=  branchRepository.findByName(task.getBranchName()) ;
        branches.forEach(branche->{
            if(!branche.getCommits().isEmpty()){ task.setStatus("IN_PROGRESS");}
            if(branche.isMerged()){ task.setStatus("DONE");}
        });

       });

       return ResponseEntity.ok(tasks);
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

