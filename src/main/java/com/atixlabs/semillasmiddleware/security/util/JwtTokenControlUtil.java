package com.atixlabs.semillasmiddleware.security.util;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;



@Component
public class JwtTokenControlUtil {

    //TODO replace for redis
    private static final Map<String, String> tokens = Maps.newHashMap();


    public void setToken(String key, String token){
        tokens.put(key, token);
    }

    public Boolean isTokenValid(String token){
        return  tokens.containsValue(token);
    }


    public Boolean revoqueToken(String key) {
        if (tokens.containsKey(key)) {
            tokens.remove(key);
            return true;
        }
        return false;
    }
}
