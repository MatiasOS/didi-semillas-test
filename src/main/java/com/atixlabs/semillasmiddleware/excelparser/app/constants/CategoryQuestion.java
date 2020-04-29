package com.atixlabs.semillasmiddleware.excelparser.app.constants;

public interface CategoryQuestion {
    public Class<?> getDataType();
    public boolean isRequired();
    public String getQuestionName();
    public static CategoryQuestion get(String questionName){
        return null;
    };
    public String name();

}
