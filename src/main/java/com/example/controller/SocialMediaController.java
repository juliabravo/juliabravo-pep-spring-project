package com.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    // new account
    @PostMapping("/register")
    public ResponseEntity<?> registerNewAccount(@RequestBody Account account) {
        if(account.getUsername() == null || account.getUsername().isBlank() || account.getPassword().length() < 4) {
            return ResponseEntity.badRequest().build();
        }
        if (accountService.usernameExists(account.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Account created = accountService.register(account);

        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        try {
            Account loggedIn = accountService.login(account.getUsername(), account.getPassword());
            return ResponseEntity.ok(loggedIn);
        } catch (ResponseStatusException e) {
            //return ResponseEntity.status(401).build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
        } 
        
    }

    // post message
    @PostMapping("/messages")
    public ResponseEntity<?> postMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            return ResponseEntity.ok(createdMessage);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    // get all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    // get message by id
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Integer messageId) {
        
            Message message = messageService.getMessageById(messageId);
            if (message == null) {
                return ResponseEntity.ok().body("");
            }
            return ResponseEntity.ok(message);
    }

    // delete message by id
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessageById(@PathVariable Integer messageId) {
        boolean deleted = messageService.deleteMessageById(messageId);
        if (deleted) {
            return ResponseEntity.ok(1);
        }
        return ResponseEntity.ok().build(); 
    }

    // update a message
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<?> updateMessageText(@PathVariable Integer messageId, @RequestBody Message updatedMessage) {
        try {
            messageService.updateMessageText(messageId, updatedMessage.getMessageText());
            return ResponseEntity.ok(1);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    // get messages by a user
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUser(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByUser(accountId);
        return ResponseEntity.ok(messages);
    }

}
