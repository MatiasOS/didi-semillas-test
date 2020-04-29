package com.atixlabs.semillasmiddleware.filemanager.controller;

import com.atixlabs.semillasmiddleware.excelparser.app.service.SurveyExcelParseService;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.excelparser.app.exception.InvalidCategoryException;
import com.atixlabs.semillasmiddleware.filemanager.exception.EmptyFileException;
import com.atixlabs.semillasmiddleware.filemanager.service.FileManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class FileManagerController {

    @Autowired
    private FileManagerService fileManagerService;

    @Autowired
    private SurveyExcelParseService surveyExcelParseService;

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) throws Exception, InvalidCategoryException {

        log.info("uploadFile executed");

        if (file.isEmpty()) {
            throw new EmptyFileException("Empty file");
        }

        File receivedFile = fileManagerService.uploadFile(file);

        ProcessExcelFileResult processExcelFileResult = surveyExcelParseService.processSingleSheetFile(receivedFile.getPath());

        return ResponseEntity.ok(processExcelFileResult);
        //return(ResponseEntity.ok().build());
    }



}
