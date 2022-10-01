package esfot.tesis.botics.auth.security.service;

import esfot.tesis.botics.auth.entity.User;
import esfot.tesis.botics.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String user_name) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(user_name).
                orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + user_name));
        return UserDetailsImpl.build(user);
    }

    public UserDetailsServiceImpl() {
        super();
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }
}
