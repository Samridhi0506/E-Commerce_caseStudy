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

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
public OrderResponse placeOrder(Long userId, CreateOrderRequest request) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

    if (product.getStock() < request.getQuantity()) {
        throw new RuntimeException("Insufficient stock");
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
public List<OrderResponse> getOrdersByUser(Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

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
public OrderResponse getOrderById(Long orderId) {

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

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
public void cancelOrder(Long orderId) {

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

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