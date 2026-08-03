package com.ecommerce.backend.repository;

import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    @Query("select distinct o from Order o join o.orderItems oi where oi.product.tenant = :tenant order by o.orderDate desc")
    List<Order> findByTenant(@Param("tenant") Tenant tenant);

}