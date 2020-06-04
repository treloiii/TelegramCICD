package com.trelloiii.cibot.service;

import com.trelloiii.cibot.model.User;
import com.trelloiii.cibot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }
    public User saveUser(org.telegram.telegrambots.meta.api.objects.User user){
        return userRepository.save(mapFromTelegram(user));
    }

    public boolean checkIfExists(User user){
        return userRepository.findById(user.getId()).isPresent();
    }
    public boolean checkIfExists(org.telegram.telegrambots.meta.api.objects.User user){
        return userRepository.findById(user.getId()).isPresent();
    }

    public User mapFromTelegram(org.telegram.telegrambots.meta.api.objects.User user){
        User mapped=new User();
        mapped.setId(user.getId());
        mapped.setLocale(user.getLanguageCode());
        mapped.setName(user.getFirstName()+" "+user.getLastName());
        mapped.setNickname(user.getUserName());
        return mapped;
    }
}
