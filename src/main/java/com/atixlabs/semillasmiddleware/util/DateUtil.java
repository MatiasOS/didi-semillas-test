package com.atixlabs.semillasmiddleware.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateUtil {

    public Date getDateNow(){
        return new Date();
    }

    public LocalDateTime getLocalDateTimeNow(){ return LocalDateTime.now();}

    public LocalDateTime getLocalDateTimeNowWithFormat(DateTimeFormatter formatter){
        String dateNow = LocalDateTime.now().format(formatter);
        return (LocalDateTime.parse(dateNow, formatter));
    }

}
