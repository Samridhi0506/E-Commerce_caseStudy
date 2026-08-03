package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateOrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.entity.Cart;
import com.ecommerce.backend.entity.Order;
import com.ecommerce.backend.entity.OrderItem;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.CartRepository;
import com.ecommerce.backend.repository.OrderItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "tenantuser",
                        null
                )
        );
    }

    @Test
void placeOrder_ShouldPlaceOrderSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductId(10L);
    product.setProductName("Air Max");
    product.setPrice(BigDecimal.valueOf(100));
    product.setStock(10);
    product.setTenant(tenant);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setProductId(10L);
    request.setQuantity(2);

    Order order = new Order();
    order.setOrderId(100L);
    order.setUser(user);
    order.setTotalAmount(BigDecimal.valueOf(200));
    order.setOrderDate(java.time.LocalDateTime.now());

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(productRepository.findByProductIdAndTenant(10L, tenant))
            .thenReturn(Optional.of(product));

    when(orderRepository.save(any(Order.class)))
            .thenReturn(order);

    OrderResponse response =
            orderService.placeOrder("nike", 1L, request);

    assertNotNull(response);
    assertEquals(100L, response.getOrderId());
    assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
    assertEquals(1, response.getItems().size());

    verify(orderRepository).save(any(Order.class));
    verify(orderItemRepository).save(any(OrderItem.class));
    verify(productRepository).save(product);
}

@Test
void placeOrder_ShouldThrow_WhenTenantNotFound() {

    CreateOrderRequest request = new CreateOrderRequest();
    request.setProductId(10L);
    request.setQuantity(2);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.placeOrder("nike", 1L, request));

    assertEquals("Tenant not found", exception.getMessage());

    verify(orderRepository, never()).save(any(Order.class));
}

@Test
void placeOrder_ShouldThrow_WhenAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setProductId(10L);
    request.setQuantity(2);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    assertThrows(AccessDeniedException.class,
            () -> orderService.placeOrder("nike", 2L, request));

    verify(userRepository, never()).findById(anyLong());
    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(orderRepository, never()).save(any(Order.class));
}

@Test
void placeOrder_ShouldThrow_WhenUserNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setProductId(10L);
    request.setQuantity(2);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    when(userRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.placeOrder("nike", 1L, request));

    assertEquals("User not found", exception.getMessage());

    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(orderRepository, never()).save(any(Order.class));
}

@Test
void placeOrder_ShouldThrow_WhenProductNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setProductId(10L);
    request.setQuantity(2);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(productRepository.findByProductIdAndTenant(10L, tenant))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.placeOrder("nike", 1L, request));

    assertEquals("Product not found", exception.getMessage());

    verify(orderRepository, never()).save(any(Order.class));
}

@Test
void placeOrder_ShouldThrow_WhenInsufficientStock() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductId(10L);
    product.setProductName("Air Max");
    product.setPrice(BigDecimal.valueOf(100));
    product.setStock(1);
    product.setTenant(tenant);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setProductId(10L);
    request.setQuantity(5);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(productRepository.findByProductIdAndTenant(10L, tenant))
            .thenReturn(Optional.of(product));

    BadRequestException exception =
            assertThrows(BadRequestException.class,
                    () -> orderService.placeOrder("nike", 1L, request));

    assertEquals("Insufficient stock", exception.getMessage());

    verify(orderRepository, never()).save(any(Order.class));
    verify(orderItemRepository, never()).save(any(OrderItem.class));
}

@Test
void getOrdersByUser_ShouldReturnOrdersSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductName("Air Max");

    Order order = new Order();
    order.setOrderId(100L);
    order.setUser(user);
    order.setOrderDate(java.time.LocalDateTime.now());
    order.setTotalAmount(BigDecimal.valueOf(200));

    OrderItem item = new OrderItem();
    item.setOrder(order);
    item.setProduct(product);
    item.setQuantity(2);
    item.setPrice(BigDecimal.valueOf(100));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(orderRepository.findByUser(user))
            .thenReturn(List.of(order));

    when(orderItemRepository.findByOrder(order))
            .thenReturn(List.of(item));

    List<OrderResponse> response =
            orderService.getOrdersByUser("nike", 1L);

    assertEquals(1, response.size());
    assertEquals(100L, response.get(0).getOrderId());
    assertEquals(1, response.get(0).getItems().size());

    verify(orderRepository).findByUser(user);
}

@Test
void getOrdersByUser_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.getOrdersByUser("nike", 1L));

    assertEquals("Tenant not found", exception.getMessage());
}

@Test
void getOrdersByUser_ShouldThrow_WhenAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    assertThrows(AccessDeniedException.class,
            () -> orderService.getOrdersByUser("nike", 2L));

    verify(orderRepository, never()).findByUser(any(User.class));
}

@Test
void getOrderById_ShouldReturnOrderSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductName("Air Max");

    Order order = new Order();
    order.setOrderId(100L);
    order.setUser(user);
    order.setOrderDate(java.time.LocalDateTime.now());
    order.setTotalAmount(BigDecimal.valueOf(200));

    OrderItem item = new OrderItem();
    item.setOrder(order);
    item.setProduct(product);
    item.setQuantity(2);
    item.setPrice(BigDecimal.valueOf(100));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(orderRepository.findById(100L))
            .thenReturn(Optional.of(order));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(orderItemRepository.findByOrder(order))
            .thenReturn(List.of(item));

    OrderResponse response =
            orderService.getOrderById("nike", 100L);

    assertEquals(100L, response.getOrderId());
    assertEquals(1, response.getItems().size());
}

@Test
void checkout_ShouldCheckoutOnlyRequestedTenantItemsAndClearThoseCartRows() {

    Tenant nikeTenant = new Tenant();
    nikeTenant.setTenantName("nike");

    Tenant adidasTenant = new Tenant();
    adidasTenant.setTenantName("adidas");

    User user = new User();
    user.setUserId(1L);

    Product nikeProduct = new Product();
    nikeProduct.setProductId(10L);
    nikeProduct.setProductName("Air Max");
    nikeProduct.setPrice(BigDecimal.valueOf(100));
    nikeProduct.setStock(10);
    nikeProduct.setTenant(nikeTenant);

    Product adidasProduct = new Product();
    adidasProduct.setProductId(20L);
    adidasProduct.setProductName("Runner");
    adidasProduct.setPrice(BigDecimal.valueOf(200));
    adidasProduct.setStock(5);
    adidasProduct.setTenant(adidasTenant);

    Cart nikeCart = new Cart();
    nikeCart.setCartId(1L);
    nikeCart.setUser(user);
    nikeCart.setProduct(nikeProduct);
    nikeCart.setQuantity(2);

    Cart adidasCart = new Cart();
    adidasCart.setCartId(2L);
    adidasCart.setUser(user);
    adidasCart.setProduct(adidasProduct);
    adidasCart.setQuantity(1);

    Order createdOrder = new Order();
    createdOrder.setOrderId(500L);
    createdOrder.setUser(user);
    createdOrder.setOrderDate(java.time.LocalDateTime.now());
    createdOrder.setTotalAmount(BigDecimal.valueOf(200));

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(nikeTenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(cartRepository.findByUser(user))
            .thenReturn(List.of(nikeCart, adidasCart));

    when(orderRepository.save(any(Order.class)))
            .thenReturn(createdOrder);

    OrderResponse response = orderService.checkout("nike", 1L);

    assertEquals(500L, response.getOrderId());
    assertEquals(1, response.getItems().size());
    assertEquals("Air Max", response.getItems().get(0).getProductName());

    verify(productRepository).save(nikeProduct);
    verify(cartRepository).deleteAll(List.of(nikeCart));
    verify(cartRepository, never()).deleteAll(List.of(nikeCart, adidasCart));
}

@Test
void getOrderById_ShouldThrow_WhenOrderNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(orderRepository.findById(100L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.getOrderById("nike", 100L));

    assertEquals("Order not found", exception.getMessage());
}

@Test
void getOrderById_ShouldThrow_WhenAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    User orderUser = new User();
    orderUser.setUserId(2L);

    Order order = new Order();
    order.setOrderId(100L);
    order.setUser(orderUser);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(orderRepository.findById(100L))
            .thenReturn(Optional.of(order));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    assertThrows(AccessDeniedException.class,
            () -> orderService.getOrderById("nike", 100L));

    verify(orderItemRepository, never()).findByOrder(any(Order.class));
}

@Test
void cancelOrder_ShouldDeleteSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setStock(5);

    Order order = new Order();
    order.setOrderId(100L);
    order.setUser(user);

    OrderItem item = new OrderItem();
    item.setOrder(order);
    item.setProduct(product);
    item.setQuantity(2);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(orderRepository.findById(100L))
            .thenReturn(Optional.of(order));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(orderItemRepository.findByOrder(order))
            .thenReturn(List.of(item));

    orderService.cancelOrder("nike", 100L);

    assertEquals(7, product.getStock());

    verify(productRepository).save(product);
    verify(orderItemRepository).deleteAll(List.of(item));
    verify(orderRepository).delete(order);
}

@Test
void cancelOrder_ShouldThrow_WhenOrderNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(orderRepository.findById(100L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.cancelOrder("nike", 100L));

    assertEquals("Order not found", exception.getMessage());

    verify(orderRepository, never()).delete(any(Order.class));
}
}