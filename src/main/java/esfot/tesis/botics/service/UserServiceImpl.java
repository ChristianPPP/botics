package esfot.tesis.botics.service;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.repository.CrudUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    CrudUserRepository userRepository;

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getInternUser(String firstName) {
        return userRepository.getInternUserByFirstName(firstName);
    }

    @Override
    public List<User> getAllInternUsers() {
        return userRepository.getInternUsers();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User getUserByUserId(Long userId) {
        return userRepository.getReferenceById(userId);
    }
}
