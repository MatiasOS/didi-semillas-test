package com.atixlabs.semillasmiddleware.excelparser.app.categories;

import com.atixlabs.semillasmiddleware.excelparser.app.exception.InvalidCategoryException;
import com.atixlabs.semillasmiddleware.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AnswerCategoryFactory {
    private static final Integer AMOUNT_CHILDREN = 11;
    private static final Integer AMOUNT_KINSMAN = 3;
    private static final String BENEFICIARY_CATEGORY_NAME="DATOS DEL BENEFICIARIO";
    private static final String SPOUSE_CATEGORY_NAME="DATOS DEL CONYUGE";
    private static final String CHILD_CATEGORY_NAME="DATOS HIJO";
    private static final String KINSMAN_CATEGORY_NAME="OTRO MIEMBRO DE LA FAMILIA";
    private static final String ENTREPRENEURSHIP_CATEGORY_NAME="EMPRENDIMIENTO";
    private static final String DWELLING_CATEGORY_NAME="VIVIENDA";



    private static Map<String, Class<?>> CATEGORIES_TYPE = new HashMap<String, Class<?>>() {{
        put(BENEFICIARY_CATEGORY_NAME, PersonCategory.class);
        put(SPOUSE_CATEGORY_NAME, PersonCategory.class);
        put(CHILD_CATEGORY_NAME, PersonCategory.class);
        put(KINSMAN_CATEGORY_NAME, PersonCategory.class);
        put(ENTREPRENEURSHIP_CATEGORY_NAME, EntrepreneurshipCategory.class);
        put(DWELLING_CATEGORY_NAME, DwellingCategory.class);
    }};



    private Map<String, Category> categories;

    public AnswerCategoryFactory(){
        generateCategoriesDinamically();
    }

    private void generateCategoriesDinamically(){
        this.categories = new HashMap<String,Category>(){{
            put(BENEFICIARY_CATEGORY_NAME, null);
            put(SPOUSE_CATEGORY_NAME, null);
            put(ENTREPRENEURSHIP_CATEGORY_NAME, null);
            put(DWELLING_CATEGORY_NAME, null);
        }};

        //Generates children keys dinamically
        for (int i = 1; i < AMOUNT_CHILDREN+1; i++) {
            categories.put(CHILD_CATEGORY_NAME +" "+ i, null);
        }

        //Generates family members' keys dinamically
        for (int i = 1; i < AMOUNT_KINSMAN+1; i++) {
            categories.put(KINSMAN_CATEGORY_NAME + " " + i, null);
        }
    }

    public Category get(String category) throws InvalidCategoryException, Exception {

        if (category == null)
            return null;

        //category = StringUtil.removeNumbers(category);
        category = StringUtil.toUpperCaseTrimAndRemoveAccents(category);

        if (!categories.containsKey(category)){
            throw new InvalidCategoryException(category);
        }

        if (categories.get(category) == null){
            Category newCategory = null;
            try{
                newCategory = createCategoryByCategoryName(category);
                categories.put(category,newCategory);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new Exception(e.getMessage());
            }
            return newCategory;
        }
        else{
            return categories.get(category);
        }
    }

    //If it's a person (beneficiary, child or other kinsman): pass the type of person to the PersonCategory's constructor)
    public Category createCategoryByCategoryName(String categoryName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        categoryName = StringUtil.removeNumbers(categoryName);
        Class<?> categoryType = CATEGORIES_TYPE.get(categoryName);
        if (categoryType == PersonCategory.class){
            return (Category) categoryType.getConstructor(String.class).newInstance(categoryName);
        }
        else{
            return (Category) categoryType.getConstructor().newInstance();
        }
    }

    public void reset(){
        generateCategoriesDinamically();
    }

}
