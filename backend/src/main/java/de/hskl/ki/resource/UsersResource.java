package de.hskl.ki.resource;

import de.hskl.ki.db.document.Users;
import de.hskl.ki.db.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user/")
public class UsersResource {

    private final UserRepository userRepository;

    public UsersResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Users> getAll() {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    public Optional<Users> getUser(@PathVariable Integer id) {
        return userRepository.findById(id);
    }
}
