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
import com.ecommerce.backend.model.OrderStatus;
import org.springframework.stereotype.Service;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.dto.response.OrderItemResponse;
import com.ecommerce.backend.repository.CartRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final CartRepository cartRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        TenantRepository tenantRepository,
                        CartRepository cartRepository) {

    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.tenantRepository = tenantRepository;
    this.cartRepository = cartRepository;
}

private Tenant resolveTenant(String tenantName) {

    if (tenantName == null || tenantName.isBlank() || "global".equalsIgnoreCase(tenantName)) {
        return null;
    }

    return tenantRepository.findByTenantName(tenantName)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
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

private void validateTenantOrderAccess(Order order, Tenant tenant) {
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    User loggedInUser = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    if (loggedInUser.getTenant() == null || !loggedInUser.getTenant().getTenantId().equals(tenant.getTenantId())) {
        throw new AccessDeniedException("Tenant access denied.");
    }

    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
    boolean hasTenantOrderItem = orderItems.stream()
            .anyMatch(item -> item.getProduct().getTenant().getTenantId().equals(tenant.getTenantId()));

    if (!hasTenantOrderItem) {
        throw new AccessDeniedException("Order does not belong to your tenant.");
    }
}

private OrderResponse mapOrderToResponse(Order order) {
    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

    List<OrderItemResponse> items = orderItems.stream().map(item ->
            new OrderItemResponse(
                    item.getProduct().getProductName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice().multiply(
                            BigDecimal.valueOf(item.getQuantity()))
            )
    ).toList();

    OrderResponse response = new OrderResponse();
    response.setOrderId(order.getOrderId());
    response.setOrderDate(order.getOrderDate());
    response.setStatus(order.getStatus());
    response.setTotalAmount(order.getTotalAmount());
    response.setItems(items);
    response.setUserId(order.getUser().getUserId());
    response.setCustomerName(order.getUser().getUsername());

    return response;
}

    @Override
public OrderResponse placeOrder(String tenantName,
                                Long userId,
                                CreateOrderRequest request){

    Tenant tenant = resolveTenant(tenantName);

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Product product;

    if (tenant != null) {
        product = productRepository
            .findByProductIdAndTenant(request.getProductId(), tenant)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    } else {
        product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    if (product.getStock() < request.getQuantity()) {
        throw new BadRequestException("Insufficient stock");
    }

    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(java.time.LocalDateTime.now());
    order.setStatus(com.ecommerce.backend.model.OrderStatus.ORDERED);
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
    response.setStatus(savedOrder.getStatus());
    response.setTotalAmount(savedOrder.getTotalAmount());
    response.setUserId(user.getUserId());
    response.setCustomerName(user.getUsername());

    List<com.ecommerce.backend.dto.response.OrderItemResponse> items =
            List.of(
                    new com.ecommerce.backend.dto.response.OrderItemResponse(
                    product.getProductName(),
                    orderItem.getQuantity(),
                    orderItem.getPrice(),
                    orderItem.getPrice().multiply(
                    BigDecimal.valueOf(orderItem.getQuantity()))
)
            );

    response.setItems(items);

    return response;
}

    @Override
public List<OrderResponse> getOrdersByUser(String tenantName,
                                           Long userId) {

    resolveTenant(tenantName);

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    List<Order> orders = orderRepository.findByUser(user);

    return orders.stream().map(this::mapOrderToResponse).toList();
}

    @Override
public List<OrderResponse> getOrdersByTenant(String tenantName) {
    Tenant tenant = resolveTenant(tenantName);

    if (tenant == null) {
        throw new BadRequestException("Tenant name is required.");
    }

    List<Order> orders = orderRepository.findByTenant(tenant);

    return orders.stream().map(this::mapOrderToResponse).toList();
}

    @Override
public OrderResponse getOrderById(String tenantName,
                                  Long orderId) {

    resolveTenant(tenantName);

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    validateOrderAccess(order);

    return mapOrderToResponse(order);
}

    @Override
public void cancelOrder(String tenantName,
                        Long orderId) {

    resolveTenant(tenantName);

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

@Override
public OrderResponse updateOrderStatus(String tenantName, Long orderId, String status) {
    Tenant tenant = resolveTenant(tenantName);

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

    validateTenantOrderAccess(order, tenant);

    OrderStatus parsedStatus;
    try {
        parsedStatus = OrderStatus.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException ex) {
        throw new BadRequestException("Invalid status. Allowed values: ORDERED, SHIPPED, DELIVERED");
    }

    order.setStatus(parsedStatus);
    orderRepository.save(order);

    return mapOrderToResponse(order);
}

@Override
@Transactional
public OrderResponse checkout(String tenantName, Long userId) {

    Tenant tenant = resolveTenant(tenantName);

    validateUserAccess(userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));

    List<Cart> cartItems = cartRepository.findByUser(user);

    if (cartItems.isEmpty()) {
        throw new BadRequestException("Cart is empty.");
    }

    List<Cart> tenantCartItems = cartItems.stream()
            .filter(cart -> {
                if (tenant == null) {
                    return true;
                }
                return cart.getProduct().getTenant().getTenantName().equals(tenantName);
            })
            .toList();

    if (tenantCartItems.isEmpty()) {
        throw new BadRequestException("No cart items found for tenant " + tenantName + ".");
    }

    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(java.time.LocalDateTime.now());
    order.setStatus(OrderStatus.ORDERED);
    order.setTotalAmount(BigDecimal.ZERO);

    Order savedOrder = orderRepository.save(order);

    BigDecimal total = BigDecimal.ZERO;

    List<OrderItemResponse> responseItems = new java.util.ArrayList<>();

    for (Cart cart : tenantCartItems) {

        Product product = cart.getProduct();

        if (product.getStock() < cart.getQuantity()) {
            throw new BadRequestException(
                    product.getProductName() + " is out of stock.");
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(savedOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(cart.getQuantity());
        orderItem.setPrice(product.getPrice());

        orderItemRepository.save(orderItem);

        product.setStock(
                product.getStock() - cart.getQuantity());

        productRepository.save(product);

        BigDecimal subtotal =
                product.getPrice().multiply(
                        BigDecimal.valueOf(cart.getQuantity()));

        total = total.add(subtotal);

        responseItems.add(
        OrderItemResponse.builder()
                .productName(product.getProductName())
                .quantity(cart.getQuantity())
                .price(product.getPrice())
                .subtotal(subtotal)
                .build()
);
    }

    savedOrder.setTotalAmount(total);

    orderRepository.save(savedOrder);

    cartRepository.deleteAll(tenantCartItems);

    return OrderResponse.builder()
            .orderId(savedOrder.getOrderId())
            .orderDate(savedOrder.getOrderDate())
            .status(savedOrder.getStatus())
            .totalAmount(savedOrder.getTotalAmount())
            .items(responseItems)
            .build();
}
}