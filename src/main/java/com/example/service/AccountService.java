package com.example.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account register (Account account) {
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return accountRepository.save(account);
    } 

    // login
    public Account login (String username, String password) {

        Optional<Account> accountExists = accountRepository.findByUsername(username);
        if (accountExists.isPresent()) {
            Account account = accountExists.get();
            if(account.getPassword().equals(password)) {
                return account;
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }
            
        } 
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        
    }

    // check for username
    public boolean usernameExists(String username) {
        return accountRepository.existsByUsername(username);
    }

}
