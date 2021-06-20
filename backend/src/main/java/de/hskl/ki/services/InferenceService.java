package de.hskl.ki.services;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class InferenceService {
    public boolean moveModelsToTriton(Path projectDir) {
        return false;
    }

    public boolean removeModelFromTriton() {
        return false;
    }
}
