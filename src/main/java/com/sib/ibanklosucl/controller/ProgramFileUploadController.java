package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.model.VehicleLoanProgramFile;
import com.sib.ibanklosucl.service.program.VehicleLoanProgramFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/program/file")
@Slf4j
public class ProgramFileUploadController {

    @Autowired
    private VehicleLoanProgramFileService fileService;

    /**
     * Upload file for 60/40 program
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("wiNum") String wiNum,
            @RequestParam("applicantId") Long applicantId,
            @RequestParam("slNo") Long slNo,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Uploading file for wiNum: {}, applicantId: {}", wiNum, applicantId);

            VehicleLoanProgramFile savedFile = fileService.saveFile(file, wiNum, applicantId, slNo, request.getRemoteAddr());

            response.put("status", "success");
            response.put("message", "File uploaded successfully");
            response.put("fileId", savedFile.getId());
            response.put("fileName", savedFile.getFileName());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Validation error during file upload: {}", e.getMessage());
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Error during file upload", e);
            response.put("status", "error");
            response.put("message", "An error occurred while uploading the file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all files for applicant and work item
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getFiles(
            @RequestParam("wiNum") String wiNum,
            @RequestParam("applicantId") Long applicantId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<VehicleLoanProgramFile> files = fileService.getFilesByApplicantAndWiNum(applicantId, wiNum);

            response.put("status", "success");
            response.put("files", files);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching files", e);
            response.put("status", "error");
            response.put("message", "An error occurred while fetching files");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Download file
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {

        try {
            VehicleLoanProgramFile file = fileService.getFileById(fileId);

            Path filePath = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable");
            }

            String contentType = file.getFileType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete file
     */
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable Long fileId) {

        Map<String, Object> response = new HashMap<>();

        try {
            fileService.deleteFile(fileId);

            response.put("status", "success");
            response.put("message", "File deleted successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting file", e);
            response.put("status", "error");
            response.put("message", "An error occurred while deleting the file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
