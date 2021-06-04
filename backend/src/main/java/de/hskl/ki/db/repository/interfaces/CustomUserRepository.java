package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.Users;

public interface CustomUserRepository {
    Users findUserByName(String userName);
}
