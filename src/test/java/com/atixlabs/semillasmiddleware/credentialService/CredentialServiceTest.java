package com.atixlabs.semillasmiddleware.credentialService;

import com.atixlabs.semillasmiddleware.SemillasMiddlewareApplication;
import com.atixlabs.semillasmiddleware.app.controller.CredentialController;
import com.atixlabs.semillasmiddleware.app.dto.CredentialDto;
import com.atixlabs.semillasmiddleware.app.service.CredentialService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ContextConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CredentialServiceTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-docs");

    @Mock
    CredentialService credentialService;


    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @Before
    public void setupMocks() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        MockitoAnnotations.initMocks(this);
    }

    private List<CredentialDto> credentialsMock(){
        List<CredentialDto> credentials = new ArrayList<>();


        CredentialDto credential1 = new CredentialDto(1L,2L,null, LocalDateTime.now(),LocalDateTime.now().plusDays(1),"Jorge Rodrigues",29302594L,"Estado");
        credentials.add(credential1);

        return credentials;
    }

    @Test
    public void getAllCredentials() throws Exception {
        when(credentialService.findAllCredentialsMock()).thenReturn(credentialsMock());


    List<>


        verify(credentialService).findAllCredentialsMock();

        String response = result.andReturn().getResponse().getContentAsString();
        //List<CredentialDto> credentialsDto = response.readVa

       /* Assertions.assertTrue(credentialsDto.size() > 0);
        Assertions.assertEquals(credentialsMock().get(0).getId() ,credentialsDto.get(0).getId());
        Assertions.assertEquals(credentialsMock().get(0).getDniBeneficiary() ,credentialsDto.get(0).getDniBeneficiary());
        Assertions.assertEquals(credentialsMock().get(0).getIdDidiCredential() ,credentialsDto.get(0).getIdDidiCredential());
        //Assertions.assertEquals(credentialsMock().get(0).getDateOfExpiry() ,credentialsDto.get(0).getDateOfExpiry());
        //Assertions.assertEquals(credentialsMock().get(0).getDateOfIssue() ,credentialsDto.get(0).getDateOfIssue());
        Assertions.assertEquals(credentialsMock().get(0).getName() ,credentialsDto.get(0).getName());*/
    }

}
