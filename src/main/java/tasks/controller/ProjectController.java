package tasks.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tasks.entity.Project;
import tasks.entity.ProjectFile;
import tasks.entity.User;
import tasks.repository.ProjectRepository;
import tasks.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String listProjects(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String status,
                               Model model,
                               Principal principal) {
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
        model.addAttribute("newProject", new Project());
    
        if (principal != null) {
            userRepository.findByUsername(principal.getName())
                    .ifPresent(user -> model.addAttribute("userRole", user.getRole().name()));
        }
    
        return "project";
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
        Optional<Project> optional = projectRepository.findById(project.getId());
        if (optional.isPresent()) {
            Project existing = optional.get();
    
            // 기존 파일 리스트 유지
            project.setFiles(existing.getFiles());
    
            // createdBy는 수정되면 안 되므로 유지
            project.setCreatedBy(existing.getCreatedBy());
    
            if (principal != null) {
                User user = userRepository.findByUsername(principal.getName()).orElse(null);
                project.setUpdatedBy(user);
            }
    
            projectRepository.save(project);
        }
        return "redirect:/projects";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        Optional<Project> optional = projectRepository.findById(id);
        if (optional.isPresent()) {
            Project project = optional.get();
            if (project.getFiles() != null) {
                for (ProjectFile file : project.getFiles()) {
                    try {
                        Files.deleteIfExists(Paths.get(file.getPath()));
                    } catch (IOException e) {
                        e.printStackTrace(); // 또는 log로 대체
                    }
                }
            }
            projectRepository.deleteById(id);
        }
        return "redirect:/projects";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Project getProject(@PathVariable Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @GetMapping("/download")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        List<Project> projects = projectRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Projects");
        Row header = sheet.createRow(0);
        String[] columns = {"name", "description", "startDate", "endDate", "status", "requestDept", "requester",
                "ipRequested", "firewallRequested", "vmRequested", "serverSetupRequested"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (Project p : projects) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(p.getName());
            row.createCell(1).setCellValue(p.getDescription() != null ? p.getDescription() : "");
            row.createCell(2).setCellValue(p.getStartDate() != null ? p.getStartDate().toString() : "");
            row.createCell(3).setCellValue(p.getEndDate() != null ? p.getEndDate().toString() : "");
            row.createCell(4).setCellValue(p.getStatus());
            row.createCell(5).setCellValue(p.getRequestDept());
            row.createCell(6).setCellValue(p.getRequester());
            row.createCell(7).setCellValue(p.isIpRequested());
            row.createCell(8).setCellValue(p.isFirewallRequested());
            row.createCell(9).setCellValue(p.isVmRequested());
            row.createCell(10).setCellValue(p.isServerSetupRequested());
        }

        String fileName = URLEncoder.encode("projects.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        if (file.isEmpty()) return "redirect:/projects";

        User user = null;
        if (principal != null) {
            user = userRepository.findByUsername(principal.getName()).orElse(null);
        }

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Project p = new Project();
            p.setName(getCellValue(row.getCell(0)));
            p.setDescription(getCellValue(row.getCell(1)));
            p.setStartDate(parseDate(row.getCell(2)));
            p.setEndDate(parseDate(row.getCell(3)));
            p.setStatus(getCellValue(row.getCell(4)));
            p.setRequestDept(getCellValue(row.getCell(5)));
            p.setRequester(getCellValue(row.getCell(6)));
            p.setIpRequested(parseBoolean(row.getCell(7)));
            p.setFirewallRequested(parseBoolean(row.getCell(8)));
            p.setVmRequested(parseBoolean(row.getCell(9)));
            p.setServerSetupRequested(parseBoolean(row.getCell(10)));
            p.setCreatedBy(user);
            p.setUpdatedBy(user);

            if (p.getName() != null && !p.getName().isBlank()) {
                projectRepository.save(p);
            }
        }

        workbook.close();
        return "redirect:/projects";
    }

    private String getCellValue(Cell cell) {
        return cell != null ? cell.toString().trim() : "";
    }

    private LocalDate parseDate(Cell cell) {
        try {
            return cell != null ? LocalDate.parse(cell.toString().trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean parseBoolean(Cell cell) {
        return cell != null && (
                "true".equalsIgnoreCase(cell.toString().trim()) ||
                "1".equals(cell.toString().trim())
        );
    }
}
