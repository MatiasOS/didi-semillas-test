package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.constants.Categories;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.PersonQuestion;
import com.atixlabs.semillasmiddleware.excelparser.app.constants.PersonType;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerDto;
import com.atixlabs.semillasmiddleware.excelparser.app.dto.AnswerRow;
import com.atixlabs.semillasmiddleware.excelparser.dto.ProcessExcelFileResult;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Setter
@Getter
public class PersonCategory implements Category {

    String categoryUniqueName;
    private Categories categoryName;

    private AnswerDto firstLastName;
    private AnswerDto institutionLevel;
    private AnswerDto addressData1;
    private AnswerDto phoneData;
    private AnswerDto referenceContact;
    private AnswerDto educationLevel;
    private AnswerDto occupationSubcategory;

    private AnswerDto name;
    private AnswerDto surname;
    private AnswerDto idType;
    private AnswerDto documentNumber;
    private AnswerDto gender;
    private AnswerDto birthDate;
    private AnswerDto relation;
    private PersonType personType;
    private AnswerDto occupation;
    private AnswerDto studies;
    private AnswerDto works;
    private AnswerDto age;
    private AnswerDto childrenQuantity;
    private AnswerDto residenceTimeInCountry;
    private AnswerDto facebook;
    private AnswerDto address;
    private AnswerDto betweenStreets;
    private AnswerDto neighborhood;
    private AnswerDto zone;
    private AnswerDto locality;
    private AnswerDto address2;
    private AnswerDto referenceContactName;
    private AnswerDto referenceContactSurname;
    private AnswerDto referenceContactPhone;
    private AnswerDto nationality;
    private AnswerDto primary;
    private AnswerDto highSchool;
    private AnswerDto tertiary;
    private AnswerDto university;
    private AnswerDto others;
    private AnswerDto lastStudyYear;
    private AnswerDto workshops;
    private AnswerDto courses;
    private AnswerDto landLine;
    private AnswerDto cellPhone;
    private AnswerDto civilStatus;
    private AnswerDto email;

    //RECIBIR CATEGORIA
    public PersonCategory(String categoryUniqueName, Categories category){
        this.firstLastName = new AnswerDto(PersonQuestion.FIRST_LAST_NAME);
        this.institutionLevel = new AnswerDto(PersonQuestion.INSTITUTION_LEVEL);
        this.addressData1 = new AnswerDto(PersonQuestion.ADDRESS_DATA_1);
        this.phoneData = new AnswerDto(PersonQuestion.PHONE_DATA);
        this.referenceContact = new AnswerDto(PersonQuestion.REFERENCE_CONTACT);
        this.educationLevel = new AnswerDto(PersonQuestion.EDUCATION_LEVEL);
        this.name = new AnswerDto(PersonQuestion.NAME);
        this.surname = new AnswerDto(PersonQuestion.SURNAME);
        this.idType = new AnswerDto(PersonQuestion.ID_TYPE);
        this.documentNumber = new AnswerDto(PersonQuestion.ID_NUMBER);
        this.gender = new AnswerDto(PersonQuestion.GENDER);
        this.birthDate = new AnswerDto(PersonQuestion.BIRTH_DATE);
        this.relation = new AnswerDto(PersonQuestion.RELATION);
        this.occupation = new AnswerDto(PersonQuestion.OCCUPATION);
        this.studies = new AnswerDto(PersonQuestion.STUDIES);
        this.works = new AnswerDto(PersonQuestion.WORKS);
        this.age = new AnswerDto(PersonQuestion.AGE);
        this.childrenQuantity = new AnswerDto(PersonQuestion.CHILDREN_QUANTITY);

        this.residenceTimeInCountry = new AnswerDto(PersonQuestion.RESIDENCE_TIME_IN_COUNTRY);
        this.facebook = new AnswerDto(PersonQuestion.FACEBOOK);
        this.address = new AnswerDto(PersonQuestion.ADDRESS);
        this.betweenStreets = new AnswerDto(PersonQuestion.BETWEEN_STREETS);
        this.neighborhood = new AnswerDto(PersonQuestion.NEIGHBORHOOD);
        this.zone = new AnswerDto(PersonQuestion.ZONE);
        this.locality = new AnswerDto(PersonQuestion.LOCALITY);
        this.address2 = new AnswerDto(PersonQuestion.ADDRESS_2);
        this.referenceContactName = new AnswerDto(PersonQuestion.REFERENCE_CONTACT_NAME);
        this.referenceContactSurname = new AnswerDto(PersonQuestion.REFERENCE_CONTACT_SURNAME);
        this.referenceContactPhone = new AnswerDto(PersonQuestion.REFERENCE_CONTACT_PHONE);
        this.nationality = new AnswerDto(PersonQuestion.NATIONALITY);
        this.primary = new AnswerDto(PersonQuestion.PRIMARY);
        this.highSchool = new AnswerDto(PersonQuestion.HIGH_SCHOOL);
        this.tertiary = new AnswerDto(PersonQuestion.TERTIARY);
        this.university = new AnswerDto(PersonQuestion.UNIVERSITY);
        this.others = new AnswerDto(PersonQuestion.OTHERS);
        this.lastStudyYear = new AnswerDto(PersonQuestion.LAST_STUDY_YEAR);

        this.workshops = new AnswerDto(PersonQuestion.WORKSHOPS);
        this.courses = new AnswerDto(PersonQuestion.COURSES);
        this.landLine = new AnswerDto(PersonQuestion.LAND_LINE);
        this.cellPhone = new AnswerDto(PersonQuestion.CELLPHONE);
        this.civilStatus = new AnswerDto(PersonQuestion.CIVIL_STATUS);
        this.email = new AnswerDto(PersonQuestion.EMAIL);

        this.categoryUniqueName = categoryUniqueName;
        this.categoryName = category;//TODO:CREAR TIPO PERSONA O RESOLVER AGRUPACION

        String personTypeString = StringUtil.removeNumbers(StringUtil.toUpperCaseTrimAndRemoveAccents(categoryUniqueName.replaceAll("DATOS|DEL","")));

        this.occupationSubcategory = new AnswerDto(PersonQuestion.OCCUPATION);
        this.occupationSubcategory.setAnswer("SUBCATEGORY");

        try{
            this.personType = PersonType.get(personTypeString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            log.error("ERROR AL INTENTAR OBTENER EL TIPO DE PERSONA: "+e.getMessage());
        }
    }



    @Override
    public void loadData(AnswerRow answerRow, ProcessExcelFileResult processExcelFileResult) {
        String question = StringUtil.toUpperCaseTrimAndRemoveAccents(answerRow.getQuestion());
        PersonQuestion questionMatch = PersonQuestion.getEnumByStringValue(question);

        if (questionMatch == null)
            return;
        Optional<AnswerDto> optionalAnswer = getAnswerType(questionMatch, answerRow);
        optionalAnswer.ifPresent(param -> param.setAnswer(answerRow, processExcelFileResult));
    }

    private Optional<AnswerDto> getAnswerType(PersonQuestion questionMatch, AnswerRow answerRow){

        switch (questionMatch) {
            case FIRST_LAST_NAME:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.firstLastName);
            case INSTITUTION_LEVEL:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.institutionLevel);
            case ADDRESS_DATA_1:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.addressData1);
            case PHONE_DATA:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.phoneData);
            case REFERENCE_CONTACT:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.referenceContact);
            case EDUCATION_LEVEL:
                answerRow.setAnswer("SUBCATEGORY");
                return Optional.of(this.educationLevel);
            case OCCUPATION:
                if (!this.occupation.answerIsEmpty()) return Optional.empty();
                return Optional.of(this.occupation);
            case ID_TYPE:
                return Optional.of(this.idType);
            case ID_NUMBER:
                return Optional.of(this.documentNumber);
            case NAME:
                return Optional.of(this.name);
            case SURNAME:
                return Optional.of(this.surname);
            case GENDER:
                return Optional.of(this.gender);
            case BIRTH_DATE:
                return Optional.of(this.birthDate);
            case RELATION:
                return Optional.of(this.relation);
            case WORKS:
                return Optional.of(this.works);
            case STUDIES:
                return Optional.of(this.studies);
            case AGE:
                return Optional.of(this.age);
            case RESIDENCE_TIME_IN_COUNTRY:
                return Optional.of(this.residenceTimeInCountry);
            case CHILDREN_QUANTITY:
                return Optional.of(this.childrenQuantity);
            case FACEBOOK:
                return Optional.of(this.facebook);
            case ADDRESS:
                return Optional.of(this.address);
            case BETWEEN_STREETS:
                return Optional.of(this.betweenStreets);
            case NEIGHBORHOOD:
                return Optional.of(this.neighborhood);
            case ZONE:
                return Optional.of(this.zone);
            case LOCALITY:
                return Optional.of(this.locality);
            case ADDRESS_2:
                return Optional.of(this.address2);
            case REFERENCE_CONTACT_NAME:
                return Optional.of(this.referenceContactName);
            case REFERENCE_CONTACT_SURNAME:
                return Optional.of(this.referenceContactSurname);
            case REFERENCE_CONTACT_PHONE:
                return Optional.of(this.referenceContactPhone);
            case NATIONALITY:
                return Optional.of(this.nationality);
            case PRIMARY:
                return Optional.of(this.primary);
            case HIGH_SCHOOL:
                return Optional.of(this.highSchool);
            case TERTIARY:
                return Optional.of(this.tertiary);
            case UNIVERSITY:
                return Optional.of(this.university);
            case OTHERS:
                return Optional.of(this.others);
            case LAST_STUDY_YEAR:
                return Optional.of(this.lastStudyYear);
            case WORKSHOPS:
                return Optional.of(this.workshops);
            case COURSES:
                return Optional.of(this.courses);
            case LAND_LINE:
                return Optional.of(this.landLine);
            case CELLPHONE:
                return Optional.of(this.cellPhone);
            case CIVIL_STATUS:
                return Optional.of(this.civilStatus);
            case EMAIL:
                return Optional.of(this.email);
            default:
                return Optional.empty();
        }
    }

    @Override
    public String getCategoryUniqueName(){
        return categoryUniqueName;
    }

    @Override
    public Categories getCategoryName(){return categoryName;}

    @Override
    public boolean isValid(ProcessExcelFileResult processExcelFileResult) {
        List<AnswerDto> answers = new LinkedList<>();
        answers.add(this.name);
        answers.add(this.surname);
        answers.add(this.documentNumber);
        answers.add(this.gender);
        answers.add(this.birthDate);
        answers.add(this.relation);
        answers.add(this.occupation);
        answers.add(this.works);
        answers.add(this.studies);
        answers.add(this.age);

        List<Boolean> validations = answers.stream().map(answerDto -> answerDto.isValid(processExcelFileResult, categoryUniqueName)).collect(Collectors.toList());
        return validations.stream().allMatch(v->v);
    }

    @Override
    public boolean isEmpty(){
        return documentNumber.answerIsEmpty();
    }

    @Override
    public boolean isRequired(){
        switch (personType){
            case BENEFICIARY:
                return true;
            case CHILD:
            case SPOUSE:
            case OTHER_KINSMAN:
                return !(this.isEmpty());
        }
        return true;
    }

    public String getName() {
        return (String) name.getAnswer();
    }

    public String getFullName(){
        return new StringBuilder((String) name.getAnswer())
                .append('_')
                .append((String) surname.getAnswer())
                .toString();
    }

    public String getSurname() {
        return (String) surname.getAnswer();
    }

    public String getIdType() {
        return (String) idType.getAnswer();
    }

    public Long getDocumentNumber() {
        return (Long) documentNumber.getAnswer();
    }

    public String getGender() {
        return (String) gender.getAnswer();
    }

    public LocalDate getBirthDate() {
        return (LocalDate) birthDate.getAnswer();
    }

    public String getRelation() {
        return (String) relation.getAnswer();
    }

    public String getAddress() {return (String) address.getAnswer();}
    public String getLocation() {return (String) locality.getAnswer();}
    public String getNeighborhood() {return (String) neighborhood.getAnswer();}

    @Override
    public String toString() {
        return "PersonCategory{" +
                "categoryUniqueName='" + categoryUniqueName + '\'' +
                "firstLastName='" + firstLastName + '\'' +
                "institutionLevel='" + institutionLevel + '\'' +
                "addressData1='" + addressData1 + '\'' +
                "phoneData='" + phoneData + '\'' +
                "referenceContact='" + referenceContact + '\'' +
                "educationLevel='" + educationLevel + '\'' +
                ", categoryName=" + categoryName +
                ", name=" + name +
                ", surname=" + surname +
                ", idType=" + idType +
                ", documentNumber=" + documentNumber +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", relation=" + relation +
                ", personType=" + personType +
                ", occupation=" + occupation +
                ", studies=" + studies +
                ", works=" + works +
                ", age=" + age +
                '}';
    }

    @Override
    public List<AnswerDto> getAnswersList() {
        switch(personType) {
            case CHILD:
                return Arrays.asList(
                        firstLastName, name, surname, age, birthDate, idType, documentNumber, gender,
                        occupationSubcategory,occupation, studies, works
                );
            case SPOUSE:
                return Arrays.asList(firstLastName, name, surname, age, birthDate, idType, documentNumber, gender, childrenQuantity,
                                     occupationSubcategory, occupation, studies, works
                );
            case OTHER_KINSMAN:
                return Arrays.asList(firstLastName, name, surname, age, birthDate, idType, documentNumber, gender, relation,
                        occupationSubcategory, occupation, studies, works
                );
            default: //BENEFICIARY
                return Arrays.asList(
                        firstLastName, name, surname, age, birthDate, nationality, residenceTimeInCountry, idType, documentNumber, civilStatus, gender, childrenQuantity,
                        institutionLevel, primary, highSchool, tertiary, university, workshops, courses, others, lastStudyYear,
                        addressData1, address, betweenStreets, neighborhood, zone, locality, address2,
                        phoneData, landLine, cellPhone, facebook, email,
                        referenceContact, referenceContactName, referenceContactSurname, relation, referenceContactPhone,
                        studies, works
                );
        }
    }

    @Override
    public String getHtmlFromTemplate(String rowTemplate, String subCategoryTemplate, String subcategoryParam, String questionParam, String answerParam) {
        String html="";

        List<AnswerDto> answerDtos = this.getAnswersList();

        for (AnswerDto answer : answerDtos) {

            if (answer.getQuestion() != null) {// && answer.getAnswer() != null) {
                String ans = String.valueOf(Optional.ofNullable(answer.getAnswer()).orElse(""));
                if (ans.equals("SUBCATEGORY")) {
                    html += subCategoryTemplate
                            .replace(subcategoryParam, answer.getQuestion().getQuestionName());
                } else html += rowTemplate
                        .replace(questionParam, answer.getQuestion().getQuestionName())
                        .replace(answerParam, ans);
            }
        }
        return html;
    }
}
