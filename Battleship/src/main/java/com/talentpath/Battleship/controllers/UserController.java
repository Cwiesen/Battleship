package com.talentpath.Battleship.controllers;

import com.talentpath.Battleship.daos.UserRepository;
import com.talentpath.Battleship.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/userdata")
@CrossOrigin(origins="http://localhost:4200")
public class UserController {

    @Autowired
    UserRepository userRepo;

    @GetMapping("/")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping("/")
    public User addUser(@RequestBody User newUser) {
        return userRepo.saveAndFlush(newUser);
    }

    @PutMapping
    public User editUser(@RequestBody User editedUser) {
        return userRepo.saveAndFlush(editedUser);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userRepo.deleteById(id);
        return "User " + id + " successfully deleted";
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userRepo.getOne(id);
    }
}
