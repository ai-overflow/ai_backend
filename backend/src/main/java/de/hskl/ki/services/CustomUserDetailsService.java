package de.hskl.ki.services;

import de.hskl.ki.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * This service is used to request user credentials from the repository
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Searches a user by username
     * @param userName username of the user
     * @return userDetails of user
     * @throws UsernameNotFoundException if there is no such user
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        var user = userRepository.findUserByName(userName);

        if (user != null) {
            return new User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());
        }
        throw new UsernameNotFoundException(userName);
    }
}
