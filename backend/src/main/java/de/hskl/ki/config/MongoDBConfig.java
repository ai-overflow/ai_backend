package de.hskl.ki.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.hskl.ki.db.document.User;
import de.hskl.ki.db.repository.UserRepository;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class MongoDBConfig {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Bean
    public MongoClient mongoClient() {
        var pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        var codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .codecRegistry(codecRegistry)
                .build());
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            userRepository.save(new User("1", "Peter", "$2y$12$/MbUDAYLELrRsKuTNBP82ulG3BbXxuA.iei8HrRBAX3rwwsdgkiry"));
            userRepository.save(new User("2", "Parker", "$2y$12$SKZnjk0OfJ2IOFjC.XQG4.Aj7x2o3t8i0vUpMOyeG/0m.BqFFn5bO"));
        };
    }
}
