package esfot.tesis.botics.service;

import esfot.tesis.botics.auth.entity.User;

public interface UserService {
    User getUser(String username);
}
