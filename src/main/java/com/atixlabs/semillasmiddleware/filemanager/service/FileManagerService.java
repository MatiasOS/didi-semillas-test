package com.atixlabs.semillasmiddleware.filemanager.service;

import com.atixlabs.semillasmiddleware.filemanager.configuration.FileManagerConfigurationProperties;
import com.atixlabs.semillasmiddleware.filemanager.exception.FileManagerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class FileManagerService {

    @Autowired
    private FileManagerConfigurationProperties fileManagerConfigurationProperties;

    public File getWorkFolder() throws FileManagerException, IOException {
        File workFolder = new File(fileManagerConfigurationProperties.getWorkPathDirectory());

        if (!workFolder.exists())
            workFolder.mkdirs();
        if (!workFolder.exists())
            throw new FileManagerException(String.format("Folder %s not exist.", fileManagerConfigurationProperties.getWorkPathDirectory()));

        return workFolder;
    }

    public File uploadFile(MultipartFile file) throws FileManagerException {
        try {
            File workMainFolder = getWorkFolder();
            File newFile = new File(workMainFolder.getAbsolutePath() + "/" + file.getOriginalFilename());
            if (!newFile.exists())
                newFile.createNewFile();
            file.transferTo(newFile);
            return newFile;
        } catch (IOException e) {
            log.error("Error creating file", e);
            throw new FileManagerException("Error creating file.");
        }
    }
}
