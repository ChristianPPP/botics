package esfot.tesis.botics.auth.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public interface UserDetailsService extends UserDetailsManager {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
