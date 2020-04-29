package com.atixlabs.semillasmiddleware.filemanager.controller;

import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.app.exception.InvalidAnswerCastException;
import com.atixlabs.semillasmiddleware.excelparser.app.service.SurveyExcelParseService;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.excelparser.app.exception.InvalidCategoryException;
import com.atixlabs.semillasmiddleware.exceptionhandler.dto.ApiError;
import com.atixlabs.semillasmiddleware.security.controller.BasicAuthIntegrationTest;
import com.atixlabs.semillasmiddleware.security.dto.JwtRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

//@RunWith(SpringRunner.class)
@Slf4j
public class FileManagerControllerIntegrationTest extends BasicAuthIntegrationTest {

    public final String URL_UPLOAD = "/api/file/upload";

    @Autowired
    private SurveyExcelParseService surveyExcelParseService;

    @Test
    public void fileEmptyException() throws IOException {

        String token = loginAndGetToken(JwtRequest.builder().username("admin").password("password").build());

        Response response  = given()
                .headers("Authorization", "Bearer " + token)
                .multiPart("file","newFile","".getBytes())
                .when().post(URL_UPLOAD);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.as(ApiError.class).getStatus());
        assertEquals("Empty file", response.as(ApiError.class).getMessage());

    }

    private Response uploadFile(String token, String filePath) throws IOException {
        File newFile = new File(filePath);
        return given()
                .headers("Authorization", "Bearer " + token)
                .accept(ContentType.JSON)
                .multiPart(newFile)
                .when().post(URL_UPLOAD);
    }

    @Test
    public void fileUploadOk() throws IOException {

        String filePath = "src/test/resources/files/exampleFile.xls";

        //borra carpeta tmp local si ya existe algo pre-cargado
        File uploadFile = new File("/tmp/exampleFile.xls");
        if(uploadFile.exists())
            uploadFile.delete();

        String token = loginAndGetToken(JwtRequest.builder().username("admin").password("password").build());

        Response response  =  this.uploadFile(token, filePath);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        uploadFile = new File("/tmp/exampleFile.xls");
        assertEquals(true, uploadFile.exists());

        log.info("Absolute Path: "+uploadFile.getAbsolutePath());
        log.info("Path: "+uploadFile.getPath());
        log.info("Canonical Path: "+uploadFile.getCanonicalPath());

    }

    @Test
    public void fileUploadSurveyOk() throws Exception, InvalidCategoryException {

        String fileName = "survey_example.xlsx";
        String initialFilePath = "src/test/resources/files/"+fileName;
        String tmpFilePath = "/tmp/"+fileName;

        //revisa si ya existe de forma local y la borra
        File uploadFile = new File(tmpFilePath);
        if(uploadFile.exists())
            uploadFile.delete();

        String token = loginAndGetToken(JwtRequest.builder().username("admin").password("password").build());
        Response response  =  this.uploadFile(token, initialFilePath);
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());

        uploadFile = new File(tmpFilePath);

        log.info("Absolute Path: "+uploadFile.getAbsolutePath());
        log.info("Path: "+uploadFile.getPath());
        log.info("Canonical Path: "+uploadFile.getCanonicalPath());

        assertTrue(uploadFile.exists());

        log.info("file uploaded successfully");
        ProcessExcelFileResult processExcelFileResult = surveyExcelParseService.processSingleSheetFile(tmpFilePath);

        log.info(processExcelFileResult.toString());
        assertEquals(32, processExcelFileResult.getTotalValidRows());
    }

    @Test
    public void answerConversion() throws InvalidAnswerCastException {

        AnswerRow answerRow = new AnswerRow();
        answerRow.setAnswer("1");
        assertEquals(java.util.Optional.ofNullable(answerRow.getAnswerAsInteger()), java.util.Optional.ofNullable(1));

        answerRow.setAnswer("1");
        assertEquals(java.util.Optional.ofNullable(answerRow.getAnswerAsLong()), java.util.Optional.ofNullable(1L));

        answerRow.setAnswer("10.01");
        assertEquals(java.util.Optional.ofNullable(answerRow.getAnswerAsDouble()), java.util.Optional.ofNullable(10.01));

        answerRow.setAnswer("stringAnswer");
        assertEquals(java.util.Optional.ofNullable(answerRow.getAnswerAsString()), java.util.Optional.ofNullable("stringAnswer"));

        answerRow.setAnswer("03/04/20");
        assertEquals(   java.util.Optional.ofNullable(answerRow.getAnswerAsDate("dd/MM/yy")),
                        java.util.Optional.ofNullable(LocalDate.parse("03/04/20", DateTimeFormatter.ofPattern("dd/MM/yy"))));

        //ahora errores

        answerRow.setAnswer("100");
        assertFalse(java.util.Optional.ofNullable(answerRow.getAnswerAsDouble()) == java.util.Optional.ofNullable(100.00));

        answerRow.setAnswer("StringDouble");
        assertFalse(java.util.Optional.ofNullable(answerRow.getAnswerAsDouble()) == java.util.Optional.ofNullable(100.00));
    }

}
