package com.trelloiii.cibot.service;

import com.trelloiii.cibot.Utils;
import com.trelloiii.cibot.model.Root;
import com.trelloiii.cibot.model.User;
import com.trelloiii.cibot.repository.RootRepository;
import com.trelloiii.cibot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RootRepository rootRepository;

    @Autowired
    public UserService(UserRepository userRepository, RootRepository rootRepository) {
        this.userRepository = userRepository;
        this.rootRepository = rootRepository;
        firstStartup();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }
    public User saveUser(org.telegram.telegrambots.meta.api.objects.User user){
        Root root=rootRepository.findAll().get(0);
        rootRepository.save(root);
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

    public boolean isRootActive(){
        return getRoot().isActivated();
    }
    public Root getRoot(){
        return rootRepository.findAll().get(0);
    }
    public void generateNewRootPassword(){
        Root root=rootRepository.findAll().get(0);
        root.setPassword(UUID.randomUUID().toString());
        File password=new File(String.format("%s/root_password", Utils.USER_DIST));
        try (FileWriter fileWriter=new FileWriter(password)){
            fileWriter.write(root.getPassword());
        }
        catch (IOException e){
            e.printStackTrace();
        }
        rootRepository.save(root);
    }
    public void firstStartup(){
        if(rootRepository.findAll().isEmpty()){
            Root root=new Root();
            root.setId(UUID.randomUUID().toString());
            root.setPassword(UUID.randomUUID().toString());
            root.setActivated(false);
            rootRepository.save(root);
            File file=new File("./root_password");
            try(FileWriter fileWriter=new FileWriter(file)){
                fileWriter.write(root.getPassword());
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
