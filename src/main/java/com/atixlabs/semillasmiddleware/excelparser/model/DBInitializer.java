package com.atixlabs.semillasmiddleware.excelparser.model;

import com.atixlabs.semillasmiddleware.excelparser.repository.CredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DBInitializer implements CommandLineRunner {

    @Autowired
    CredentialsRepository credentialsRepository;

    @Override
    public void run(String... args) throws Exception {

        log.info("--------CORRIENDO NUEVO DB-INITIALIZER---------");

/*        CredentialData credentialData = new CredentialData();

        credentialData.setCredentialName("Credencial Crediticia");
        credentialData.setCategoryName("Datos Beneficiario");
        credentialData.setQuestionName("Número de Documento");
*/
        Credentials credentials = new Credentials();
        credentials.setCredentialName("Credencial Crediticia");
        credentialsRepository.save(credentials);



    }
}