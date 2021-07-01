package de.hskl.ki.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class ProjectServiceTest {

    @Test
    void moveProjectDirToRoot() {
        //Mockito.when(citizenRepository.getDataFromDB()).thenReturn("Something you'd like to Return");
    }
}
