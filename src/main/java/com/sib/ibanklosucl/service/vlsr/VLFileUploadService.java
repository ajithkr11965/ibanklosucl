package com.sib.ibanklosucl.service.vlsr;


import com.sib.ibanklosucl.model.VLFileUpload;
import com.sib.ibanklosucl.repository.VLFileUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VLFileUploadService {

    @Autowired
    private VLFileUploadRepository fileInfoRepository;


    public List<VLFileUpload> findFileBySlno(Long slno) {
        return fileInfoRepository.findAllBySlno(slno);
    }
    public Long countileBySlno(Long slno) {
        return fileInfoRepository.countAllBySlno(slno);
    }

    public VLFileUpload saveFile(VLFileUpload fileInfo) {
        return fileInfoRepository.save(fileInfo);
    }

    public void deleteFile(Long slno) {
        fileInfoRepository.deleteById(slno);
    }
}