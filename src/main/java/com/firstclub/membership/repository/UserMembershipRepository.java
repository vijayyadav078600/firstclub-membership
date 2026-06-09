package com.firstclub.membership.repository;

import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {

    Optional<UserMembership> findByUserAndStatus(User user, MembershipStatus status);

    @Query("SELECT m FROM UserMembership m JOIN FETCH m.plan JOIN FETCH m.tier JOIN FETCH m.user WHERE m.user = :user AND m.status = 'ACTIVE'")
    Optional<UserMembership> findActiveByUser(User user);

    /** Pessimistic write lock for critical concurrent updates */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM UserMembership m WHERE m.user = :user AND m.status = 'ACTIVE'")
    Optional<UserMembership> findActiveByUserWithLock(User user);

    boolean existsByUserAndStatus(User user, MembershipStatus status);

    List<UserMembership> findByStatusAndEndDateBefore(MembershipStatus status, LocalDateTime now);

    @Query("SELECT m FROM UserMembership m JOIN FETCH m.user JOIN FETCH m.plan JOIN FETCH m.tier WHERE m.status = :status")
    List<UserMembership> findAllByStatusWithDetails(MembershipStatus status);
}
