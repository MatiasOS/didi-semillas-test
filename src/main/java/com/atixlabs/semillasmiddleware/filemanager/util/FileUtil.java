package com.atixlabs.semillasmiddleware.filemanager.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

@NoArgsConstructor
@Component
public class FileUtil {

    /**
     * the File must exist
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public File getFileByPath(String path) throws FileNotFoundException {
        Optional<File> optionalFile;
        File file = new File(path);
        optionalFile = file.exists() ? Optional.of(file) : Optional.empty();
        //TODO make File manager exeception
        return optionalFile.orElseThrow(() -> new FileNotFoundException(String.format("File %s not exist.",path)));
    }

}
