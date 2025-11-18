package com.sib.ibanklosucl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import  com.sib.ibanklosucl.model.user.User;
import org.springframework.data.repository.history.RevisionRepository;

public interface UserRepository extends RevisionRepository<User, Long,Long>, JpaRepository<User, Long> {
    User findByUsername(String username);
}
