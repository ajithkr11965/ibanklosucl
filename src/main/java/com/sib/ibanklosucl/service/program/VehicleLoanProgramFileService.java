package com.sib.ibanklosucl.service.program;

import com.sib.ibanklosucl.model.VehicleLoanProgramFile;
import com.sib.ibanklosucl.repository.program.VehicleLoanProgramFileRepository;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VehicleLoanProgramFileService {

    @Autowired
    private VehicleLoanProgramFileRepository fileRepository;

    @Autowired
    private UserSessionData usd;

    @Value("${file.upload.dir:uploads/program-files}")
    private String uploadDir;

    /**
     * Save uploaded file to disk and database
     */
    @Transactional
    public VehicleLoanProgramFile saveFile(MultipartFile file, String wiNum, Long applicantId, Long slNo, String reqIp) throws IOException {
        log.info("Saving file for wiNum: {}, applicantId: {}", wiNum, applicantId);

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file size (5MB limit)
        long maxFileSize = 5 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !isValidFileType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only PDF, JPG, PNG are allowed");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, wiNum, applicantId.toString());
        Files.createDirectories(uploadPath);

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save file metadata to database
        VehicleLoanProgramFile programFile = new VehicleLoanProgramFile();
        programFile.setWiNum(wiNum);
        programFile.setSlNo(slNo);
        programFile.setApplicantId(applicantId);
        programFile.setFileName(originalFilename);
        programFile.setFilePath(filePath.toString());
        programFile.setFileType(contentType);
        programFile.setFileSize(file.getSize());
        programFile.setUploadDate(new Date());
        programFile.setDelFlg("N");
        programFile.setCmUser(usd.getPPCNo());
        programFile.setCmDate(new Date());
        programFile.setReqIpAddr(CommonUtils.getIP(reqIp));
        programFile.setHomeSol(usd.getSolid());

        VehicleLoanProgramFile savedFile = fileRepository.save(programFile);
        log.info("File saved successfully with ID: {}", savedFile.getId());

        return savedFile;
    }

    /**
     * Get all files for applicant and work item
     */
    public List<VehicleLoanProgramFile> getFilesByApplicantAndWiNum(Long applicantId, String wiNum) {
        return fileRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, "N");
    }

    /**
     * Delete file (soft delete)
     */
    @Transactional
    public void deleteFile(Long fileId) {
        VehicleLoanProgramFile file = fileRepository.findByIdAndDelFlg(fileId, "N")
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        file.setDelFlg("Y");
        file.setDelUser(usd.getPPCNo());
        file.setDelDate(new Date());

        fileRepository.save(file);
        log.info("File marked as deleted: {}", fileId);
    }

    /**
     * Delete all files for applicant and work item
     */
    @Transactional
    public void deleteFilesByApplicantAndWiNum(Long applicantId, String wiNum) {
        fileRepository.deleteByApplicantIdAndWiNum(applicantId, wiNum);
        log.info("All files deleted for applicantId: {}, wiNum: {}", applicantId, wiNum);
    }

    /**
     * Validate file type
     */
    private boolean isValidFileType(String contentType) {
        return contentType.equals("application/pdf") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png");
    }

    /**
     * Get file by ID
     */
    public VehicleLoanProgramFile getFileById(Long fileId) {
        return fileRepository.findByIdAndDelFlg(fileId, "N")
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }
}
