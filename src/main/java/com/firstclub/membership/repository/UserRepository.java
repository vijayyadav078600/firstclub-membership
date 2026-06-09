package com.firstclub.membership.repository;

import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.CohortType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByCohort(CohortType cohort);
}
