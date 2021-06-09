package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.User;

public interface CustomUserRepository {
    User findUserByName(String userName);
}
