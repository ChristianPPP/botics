package esfot.tesis.botics.auth.security.service;

import esfot.tesis.botics.auth.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public interface UserDetailsService extends UserDetailsManager {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    String updateResetPasswordToken(String token, String email);
    User getByResetPasswordToken(String token);
    void resetPassword(User user, String newPassword);
}
