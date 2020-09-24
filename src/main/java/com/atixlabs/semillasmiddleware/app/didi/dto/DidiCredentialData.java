package com.atixlabs.semillasmiddleware.app.didi.dto;

import com.atixlabs.semillasmiddleware.app.model.credential.*;
import com.atixlabs.semillasmiddleware.app.model.credential.constants.CredentialCategoriesCodes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
@Getter
public class DidiCredentialData {
    private ArrayList<DidiCredentialDataElem> cert;
    private ArrayList<ArrayList<DidiCredentialDataElem>> participant;
    private ArrayList<DidiCredentialDataElem> others;

    @Override
    public String toString() {
        return  "{" +
                " \"cert\":"+cert+
                ",\"participant\":"+participant+""+
                ",\"others\":"+others+
                "}";
    }

    public DidiCredentialData(Credential credential, String templateDescription){
        this.participant = new ArrayList<>();
        ArrayList<DidiCredentialDataElem> part = new ArrayList<>();
        //DID + NOMBRE + APELLIDO are mandatory fields
        part.add(new DidiCredentialDataElem("DID", credential.getIdDidiReceptor()));
        part.add(new DidiCredentialDataElem("NOMBRE", credential.getBeneficiaryFirstName()));
        part.add(new DidiCredentialDataElem("APELLIDO", credential.getBeneficiaryLastName()));
        participant.add(part);

        this.others = new ArrayList<>();
        this.cert = new ArrayList<>();
        cert.add(new DidiCredentialDataElem("CERTIFICADO O CURSO", templateDescription));

        switch (CredentialCategoriesCodes.getEnumByStringValue(credential.getCredentialCategory())){
            case IDENTITY:
                buildDidiCredentialDataFromIdentity((CredentialIdentity) credential);
                break;
            case DWELLING:
                buildDidiCredentialDataFromDwelling((CredentialDwelling) credential);
                break;
            case ENTREPRENEURSHIP:
                buildDidiCredentialDataFromEntrepreneurship((CredentialEntrepreneurship) credential);
                break;
            case BENEFIT:
                buildDidiCredentialDataFromBenefit((CredentialBenefits) credential);
                break;
            case CREDIT:
                buildDidiCredentialDataFromCredit((CredentialCredit) credential);
                break;
            case BENEFIT_SANCOR:
                buildDidiCredentialDataFromSancorBenefit((CredentialBenefitSancor) credential);
                break;
            default:
                log.error("NO EXISTE el tipo de credencial para enviar a didi");
                break;
        }
    }

    private void buildDidiCredentialDataFromIdentity(CredentialIdentity credential){
        cert.add(new DidiCredentialDataElem("Dni Titular", credential.getCreditHolderDni().toString()));
        cert.add(new DidiCredentialDataElem("Nombre Titular", credential.getCreditHolderFirstName()+" "+credential.getCreditHolderLastName()));
        cert.add(new DidiCredentialDataElem("Relacion con Titular",credential.getRelationWithCreditHolder()));
        cert.add(new DidiCredentialDataElem("Dni Beneficiario", credential.getBeneficiaryDni().toString()));
        cert.add(new DidiCredentialDataElem("Nombre Beneficiario", credential.getBeneficiaryFirstName()+" "+credential.getBeneficiaryLastName()));
        cert.add(new DidiCredentialDataElem("Genero", credential.getBeneficiaryGender()));
        cert.add(new DidiCredentialDataElem("Fecha de Nacimiento", credential.getBeneficiaryBirthDate().toString()));
    }

    private void buildDidiCredentialDataFromDwelling(CredentialDwelling credential){
        cert.add(new DidiCredentialDataElem("Tipo de Tenencia", credential.getPossessionType()));
        cert.add(new DidiCredentialDataElem("Tipo de Vivienda", credential.getDwellingType()));
        cert.add(new DidiCredentialDataElem("Distrito de Residencia", credential.getDwellingAddress()));

        cert.add(new DidiCredentialDataElem("Instalacion de luz", credential.getLightInstallation()));
        cert.add(new DidiCredentialDataElem("Condiciones grales", credential.getGeneralConditions()));
        cert.add(new DidiCredentialDataElem("Tipo de barrio", credential.getNeighborhoodType()));
        cert.add(new DidiCredentialDataElem("Red de gas", credential.getGas().toString()));
        cert.add(new DidiCredentialDataElem("Garrafa", credential.getCarafe().toString()));
        cert.add(new DidiCredentialDataElem("Red de agua", credential.getWater().toString()));
        cert.add(new DidiCredentialDataElem("Pozo/Bomba", credential.getWatterWell().toString()));

        cert.add(new DidiCredentialDataElem("Localidad", ""));
        cert.add(new DidiCredentialDataElem("Barrio", ""));
        cert.add(new DidiCredentialDataElem("Direccion", ""));

    }

    private void buildDidiCredentialDataFromEntrepreneurship(CredentialEntrepreneurship credential){
        cert.add(new DidiCredentialDataElem("Tipo de Emprendimiento", credential.getEntrepreneurshipType()));
        cert.add(new DidiCredentialDataElem("Inicio de Actividad", credential.getStartActivity().toString()));
        cert.add(new DidiCredentialDataElem("Actividad Principal", credential.getMainActivity()));
        cert.add(new DidiCredentialDataElem("Nombre del Emprendimiento", credential.getEntrepreneurshipName()));
        cert.add(new DidiCredentialDataElem("Domicilio del Emprendimiento", credential.getEntrepreneurshipAddress()));
    }

    private void buildDidiCredentialDataFromBenefit(CredentialBenefits credential){
        cert.add(new DidiCredentialDataElem("Dni Beneficiario", credential.getBeneficiaryDni().toString()));
        cert.add(new DidiCredentialDataElem("Caracter", credential.getBeneficiaryType()));
    }

    private void buildDidiCredentialDataFromSancorBenefit(CredentialBenefitSancor credential){
        cert.add(new DidiCredentialDataElem("POLIZA", credential.getPolicyNumber().toString()));
        cert.add(new DidiCredentialDataElem("CERT", credential.getCertificateNumber().toString()));
        cert.add(new DidiCredentialDataElem("REF", credential.getPolicyNumber().toString()));
        cert.add(new DidiCredentialDataElem("DNI", credential.getBeneficiaryDni().toString()));
    }

    private void buildDidiCredentialDataFromCredit(CredentialCredit credential){
        cert.add(new DidiCredentialDataElem("Dni Titular", credential.getBeneficiaryDni().toString()));
        cert.add(new DidiCredentialDataElem("Id Credito", credential.getIdBondareaCredit()));
        cert.add(new DidiCredentialDataElem("Tipo de Credito", credential.getCreditType()));
        cert.add(new DidiCredentialDataElem("Id Grupo", credential.getIdGroup()));
        cert.add(new DidiCredentialDataElem("Ciclo", credential.getCurrentCycle()));
        cert.add(new DidiCredentialDataElem("Estado de Credito", credential.getCreditState()));
        cert.add(new DidiCredentialDataElem("Saldo Vencido", credential.getExpiredAmount().toString()));
        cert.add(new DidiCredentialDataElem("Cuota", credential.getCurrentCycleNumber().toString()));
        cert.add(new DidiCredentialDataElem("Cuotas Totales", String.valueOf(credential.getTotalCycles())));
     //   cert.add(new DidiCredentialDataElem("Cuotas Vencidas", String.valueOf(credential.getAmountExpiredCycles())));
    }

}
/*CREDITO
    "value":"Vivienda Semillas v1", "name":"CERTIFICADO O CURSO"
    "value":"30111111", "name":"Dni Titular"
    "value":"SEM-CRED-01", "name":"Id Credito"
    "value":"SEM-CRED", "name":"Tipo de Credito"
    "value":"GRUPO-AX1", "name":"Id Grupo"
    "value":"VIGENTE", "name":"Estado de Credito"
    "value":"0", "name":"Saldo Vencido"
    "value":"2", "name":"Cuota"
    "value":"12", "name":"Cuotas Totales"
    "value":"0", "name":"Cuotas Vencidas"
EMPRENDIMIENTO
            "value": "Vivienda Semillas v1",            "_id": "5ec2f5303fbea6397dcde622",            "name": "CERTIFICADO O CURSO"
            "value": "Comercio",            "_id": "5ec2f5303fbea6397dcde623",            "name": "Tipo de Emprendimiento"
            "value": "2010/01/01",            "_id": "5ec2f5303fbea6397dcde624",            "name": "Inicio de Actividad"
            "value": "Confiteria",            "_id": "5ec2f5303fbea6397dcde625",            "name": "Actividad Principal"
            "value": "Panaderia Pepe",            "_id": "5ec2f5303fbea6397dcde626",            "name": "Nombre del Emprendimiento"
            "value": "Distrito 2",            "_id": "5ec2f5303fbea6397dcde627",            "name": "Domicilio del Emprendimiento"
VIVIENDA
            "value": "Vivienda Semillas v1",            "_id": "5ec2f2933fbea6397dcde61a",            "name": "CERTIFICADO O CURSO"
            "value": "Alquiler",            "_id": "5ec2f2933fbea6397dcde61b",            "name": "Tipo de Tenencia"
            "value": "Departamento",            "_id": "5ec2f2933fbea6397dcde61c",            "name": "Tipo de Vivienda"
            "value": "Distrito 2",            "_id": "5ec2f2933fbea6397dcde61d",            "name": "Distrito de Residencia"
IDENTIDAD
            "value": "Identidad Semillas v1", "_id": "5ec2de1f3fbea6397dcde60e","name": "CERTIFICADO O CURSO"
            "value": "30655655",            "_id": "5ec2de1f3fbea6397dcde60f",            "name": "Dni Titular"
            "value": "Dario Raimondo",            "_id": "5ec2de1f3fbea6397dcde610",            "name": "Nombre Titular"
            "value": "Titular",            "_id": "5ec2de1f3fbea6397dcde611",            "name": "Relacion con Titular"
            "value": "30655655",            "_id": "5ec2de1f3fbea6397dcde612",            "name": "Dni Beneficiario"
            "value": "Dario Raimondo",            "_id": "5ec2de1f3fbea6397dcde613",            "name": "Nombre Beneficiario"
            "value": "Masculino",            "_id": "5ec2de1f3fbea6397dcde614",            "name": "Genero"
            "value": "1984/01/14",            "_id": "5ec2de1f3fbea6397dcde615",            "name": "Fecha de Nacimiento"
 */