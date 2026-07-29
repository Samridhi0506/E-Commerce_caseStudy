package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.CreateOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.OrderItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.OrderService;
import org.springframework.stereotype.Service;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.repository.TenantRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        TenantRepository tenantRepository) {

    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
}

private void validateUserAccess(Long userId) {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    User loggedInUser = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    if (!loggedInUser.getUserId().equals(userId)) {
        throw new AccessDeniedException("Access denied.");
    }
}

private void validateOrderAccess(Order order) {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    User loggedInUser = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    if (!order.getUser().getUserId().equals(loggedInUser.getUserId())) {
        throw new AccessDeniedException("Access denied.");
    }
}

    @Override
public OrderResponse placeOrder(String tenantName,
                                Long userId,
                                CreateOrderRequest request){

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product = productRepository
        .findByProductIdAndTenant(request.getProductId(), tenant)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    if (product.getStock() < request.getQuantity()) {
        throw new BadRequestException("Insufficient stock");
    }

    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(java.time.LocalDateTime.now());
    order.setTotalAmount(
            product.getPrice().multiply(
                    java.math.BigDecimal.valueOf(request.getQuantity())
            )
    );

    Order savedOrder = orderRepository.save(order);

    OrderItem orderItem = new OrderItem();
    orderItem.setOrder(savedOrder);
    orderItem.setProduct(product);
    orderItem.setQuantity(request.getQuantity());
    orderItem.setPrice(product.getPrice());

    orderItemRepository.save(orderItem);

    product.setStock(product.getStock() - request.getQuantity());
    productRepository.save(product);

    OrderResponse response = new OrderResponse();
    response.setOrderId(savedOrder.getOrderId());
    response.setOrderDate(savedOrder.getOrderDate());
    response.setTotalAmount(savedOrder.getTotalAmount());

    List<com.ecommerce.backend.dto.response.OrderItemResponse> items =
            List.of(
                    new com.ecommerce.backend.dto.response.OrderItemResponse(
                            product.getProductName(),
                            orderItem.getQuantity(),
                            orderItem.getPrice()
                    )
            );

    response.setItems(items);

    return response;
}

    @Override
public List<OrderResponse> getOrdersByUser(String tenantName,
                                           Long userId) {

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    List<Order> orders = orderRepository.findByUser(user);

    return orders.stream().map(order -> {

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<com.ecommerce.backend.dto.response.OrderItemResponse> items =
                orderItems.stream().map(item ->
                        new com.ecommerce.backend.dto.response.OrderItemResponse(
                                item.getProduct().getProductName(),
                                item.getQuantity(),
                                item.getPrice()
                        )
                ).toList();

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setItems(items);

        return response;

    }).toList();
}

    @Override
public OrderResponse getOrderById(String tenantName,
                                  Long orderId) {

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    validateOrderAccess(order);

    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

    List<com.ecommerce.backend.dto.response.OrderItemResponse> items =
            orderItems.stream().map(item ->
                    new com.ecommerce.backend.dto.response.OrderItemResponse(
                            item.getProduct().getProductName(),
                            item.getQuantity(),
                            item.getPrice()
                    )
            ).toList();

    OrderResponse response = new OrderResponse();
    response.setOrderId(order.getOrderId());
    response.setOrderDate(order.getOrderDate());
    response.setTotalAmount(order.getTotalAmount());
    response.setItems(items);

    return response;
}

    @Override
public void cancelOrder(String tenantName,
                        Long orderId) {

    Tenant tenant = tenantRepository.findByTenantName(tenantName)
        .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    validateOrderAccess(order);

    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

    // Restore product stock
    for (OrderItem item : orderItems) {

        Product product = item.getProduct();
        product.setStock(product.getStock() + item.getQuantity());

        productRepository.save(product);
    }

    // Delete all order items
    orderItemRepository.deleteAll(orderItems);

    // Delete the order
    orderRepository.delete(order);
}
}