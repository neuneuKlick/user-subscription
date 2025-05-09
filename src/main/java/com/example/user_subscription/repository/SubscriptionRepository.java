package com.example.user_subscription.repository;

import com.example.user_subscription.model.Subscription;
import com.example.user_subscription.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByUserAndServiceName(User user, String serviceName);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.user WHERE s.user.id = :userId")
    List<Subscription> findWithUserByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Subscription s WHERE s.id = :subscriptionId AND s.user.id = :userId")
    int deleteByIdAndUserId(@Param("subscriptionId") Long subscriptionId,
                            @Param("userId") Long userId);

    @Query("""
        SELECT s.serviceName, COUNT(s.id) as count 
        FROM Subscription s 
        GROUP BY s.serviceName 
        ORDER BY count DESC
        """)
    List<Object[]> findTop3PopularSubscriptions(Pageable pageable);

    default List<Object[]> findTop3PopularSubscriptions() {
        return findTop3PopularSubscriptions(PageRequest.of(0, 3));
    }
}
