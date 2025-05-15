package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tasks.entity.Project;
import tasks.entity.User;
import tasks.repository.ProjectRepository;
import tasks.repository.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String listProjects(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               Model model) {
        List<Project> projects;

        if ((keyword != null && !keyword.isEmpty()) && (status != null && !status.isEmpty())) {
            projects = projectRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status);
        } else if (keyword != null && !keyword.isEmpty()) {
            projects = projectRepository.findByNameContainingIgnoreCase(keyword);
        } else if (status != null && !status.isEmpty()) {
            projects = projectRepository.findByStatus(status);
        } else {
            projects = projectRepository.findAll();
        }

        model.addAttribute("projects", projects);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("newProject", new Project()); // ✅ 모달용 빈 객체
        return "project";
    }

    @GetMapping("/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "project-form";
    }

    @PostMapping("/save")
    public String saveProject(@ModelAttribute Project project, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            project.setCreatedBy(user);
            project.setUpdatedBy(user);
        }
        projectRepository.save(project);
        return "redirect:/projects";
    }

    @PostMapping("/update")
    public String updateProject(@ModelAttribute Project project, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            project.setUpdatedBy(user);
        }
        projectRepository.save(project);
        return "redirect:/projects";
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable Long id, Model model) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            model.addAttribute("project", projectOpt.get());
            return "project-form";
        } else {
            return "redirect:/projects";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }

    // ✅ 수정 모달용 API
    @GetMapping("/api/{id}")
    @ResponseBody
    public Project getProject(@PathVariable Long id) {
        return projectRepository.findById(id).orElse(null);
    }
}
