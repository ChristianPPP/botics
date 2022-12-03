package esfot.tesis.botics.service;

import esfot.tesis.botics.auth.entity.User;

import java.util.List;

public interface UserService {
    User getUser(String username);
    User getUserById(Long id);
    List<User> getAllInternUsers();
    User getInternUser(String firstName);
    User getUserByUserId(Long userId);
}
