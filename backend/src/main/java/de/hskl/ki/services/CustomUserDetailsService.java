package de.hskl.ki.services;

import de.hskl.ki.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        //userRepository.findOne()

        // TODO: load from DB
        return new User("foo", "$2y$12$/MbUDAYLELrRsKuTNBP82ulG3BbXxuA.iei8HrRBAX3rwwsdgkiry", new ArrayList<>());
    }
}
