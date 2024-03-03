package com.jbs.StockGame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Account;

@Service
public class CustomAccountDetailsService implements UserDetailsService {
    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.findByUsername(username);
        if(account == null)
            return null;

        return User.builder()
        .username(account.getUsername())
        .password(account.getPassword())
        .roles(account.getRole())
        .build();
    }
}
