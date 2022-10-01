package esfot.tesis.botics.service;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.repository.CrudUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    CrudUserRepository userRepository;

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
