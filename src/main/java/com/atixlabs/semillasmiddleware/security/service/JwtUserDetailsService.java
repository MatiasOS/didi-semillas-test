package com.atixlabs.semillasmiddleware.security.service;

import com.atixlabs.semillasmiddleware.security.configuration.CustomUser;
import com.atixlabs.semillasmiddleware.security.model.User;
import com.atixlabs.semillasmiddleware.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> opUser = userRepository.findByUsername(username);

        if (opUser.isPresent()) {
            User user =  opUser.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            CustomUser userDetails = CustomUser.create(user);

            return userDetails;
            //return new User("user", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
             //       new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}

