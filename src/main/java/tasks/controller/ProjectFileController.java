package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tasks.entity.Project;
import tasks.entity.ProjectFile;
import tasks.entity.User;
import tasks.repository.ProjectFileRepository;
import tasks.repository.ProjectRepository;
import tasks.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projects/file")
public class ProjectFileController {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final UserRepository userRepository;

    @Value("${tasks.upload.project-path}")
    private String uploadDir;

    @PostMapping("/upload/{projectId}")
    public String uploadFile(@PathVariable Long projectId,
                             @RequestParam("file") MultipartFile file,
                             Principal principal) {
        try {
            Optional<Project> optionalProject = projectRepository.findById(projectId);
            if (optionalProject.isEmpty()) return "redirect:/projects";

            Project project = optionalProject.get();

            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || !originalFilename.contains(".")) {
                    return "redirect:/projects";
                }

                String cleanedName = StringUtils.cleanPath(originalFilename);
                String ext = cleanedName.substring(cleanedName.lastIndexOf("."));
                String savedName = "project" + project.getId() + "_" + UUID.randomUUID() + ext;

                Path savePath = Paths.get(uploadDir, savedName);
                Files.createDirectories(savePath.getParent());
                file.transferTo(savePath.toFile());

                User user = null;
                if (principal != null) {
                    user = userRepository.findByUsername(principal.getName()).orElse(null);
                }

                ProjectFile pf = new ProjectFile();
                pf.setProject(project);
                pf.setOriginalName(cleanedName);
                pf.setSavedName(savedName);
                pf.setPath(savePath.toString());
                pf.setUploadedBy(user);
                projectFileRepository.save(pf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/projects";
    }

    @GetMapping("/list/{projectId}")
    @ResponseBody
    public List<ProjectFile> listFiles(@PathVariable Long projectId) {
        return projectFileRepository.findByProjectId(projectId);
    }

    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<ProjectFile> optional = projectFileRepository.findById(id);
        if (optional.isEmpty()) return;

        ProjectFile file = optional.get();
        Path path = Paths.get(file.getPath());
        if (!Files.exists(path)) return;

        String encodedName = URLEncoder.encode(file.getOriginalName(), "UTF-8").replaceAll("\\+", "%20");

        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
        response.setContentType("application/octet-stream");
        Files.copy(path, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        Optional<ProjectFile> optional = projectFileRepository.findById(id);
        if (optional.isPresent()) {
            ProjectFile file = optional.get();
            try {
                Files.deleteIfExists(Paths.get(file.getPath()));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            projectFileRepository.deleteById(id);
        }
        return ResponseEntity.ok().build();
    }
}
