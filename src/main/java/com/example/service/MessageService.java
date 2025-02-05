package com.example.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    public MessageService (MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    // create
    @Transactional
    public Message createMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255 || !accountRepository.existsById((message.getPostedBy()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return messageRepository.save(message);
    }

    // get all
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // get by id
    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    // delete by id
    @Transactional
    public boolean deleteMessageById(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId); 
            return true;
        } 
        return false;
    } 
 
    // update
    @Transactional
    public void updateMessageText(Integer messageId, String newMessageText) {
        if (newMessageText == null || newMessageText.isBlank() || newMessageText.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        message.setMessageText(newMessageText);
        messageRepository.save(message);
    }

    // get by user
    public List<Message> getMessagesByUser(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}
