package com.atixlabs.semillasmiddleware.app.didi.dto;

import com.atixlabs.semillasmiddleware.app.model.credential.CredentialIdentity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Getter
public class MicrocredentialData {

    private String title;

    private List<String> names = new ArrayList<String>();

    public void buildDidiMicroCredentialDataForIdentity(){
        this.title = "Datos Personales";
        this.names = new ArrayList<String>();
        this.names.add("Genero");
        this.names.add("Fecha de Nacimiento");

    }

    /*@Override
    public String toString() {
        return  "{" +
                " \"title\":"+title+
                ",\"names\":"+names+""+

                "}";
    }*/

    @Override
    public String toString() {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return null;
        /*return "{" +
                "title:" + title + '\'' +
                ", names=" + names +
                '}';*/
    }
}
