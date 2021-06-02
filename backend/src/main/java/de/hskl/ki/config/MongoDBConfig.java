package de.hskl.ki.config;

import de.hskl.ki.db.document.Users;
import de.hskl.ki.db.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class MongoDBConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            userRepository.save(new Users(1, "Peter", "$2y$12$/MbUDAYLELrRsKuTNBP82ulG3BbXxuA.iei8HrRBAX3rwwsdgkiry"));
            userRepository.save(new Users(2, "Parker", "$2y$12$SKZnjk0OfJ2IOFjC.XQG4.Aj7x2o3t8i0vUpMOyeG/0m.BqFFn5bO"));
        };
    }
}
