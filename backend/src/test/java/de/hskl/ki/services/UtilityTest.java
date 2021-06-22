package de.hskl.ki.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
class UtilityTest {

    @Test
    void testRandomString() {
        String pattern = "[A-Za-z0-9]{20}";
        for(int i=0; i < 20; i++) {
            assertTrue(Utility.generateRandomString(20).matches(pattern));
        }
    }

    @Test
    void testRandomStringLength() {
        for(int i=0; i < 20; i++) {
            assertEquals(20, Utility.generateRandomString(20).length());
        }
    }

}
