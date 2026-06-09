package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Order;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserAndStatus(User user, OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user AND o.status = 'COMPLETED'")
    long countCompletedOrdersByUser(User user);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.user = :user AND o.status = 'COMPLETED' AND o.createdAt >= :from AND o.createdAt <= :to")
    BigDecimal sumCompletedOrderAmountByUserInPeriod(User user, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.user = :user AND o.status = 'COMPLETED'")
    BigDecimal sumTotalCompletedOrderAmountByUser(User user);
}
