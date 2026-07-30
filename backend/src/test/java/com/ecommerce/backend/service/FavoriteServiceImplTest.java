package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.AddFavoriteRequest;
import com.ecommerce.backend.dto.response.FavoriteResponse;
import com.ecommerce.backend.entity.Favorite;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.repository.FavoriteRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.impl.FavoriteServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

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
void addFavorite_ShouldAddSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductId(10L);
    product.setProductName("Air Max");
    product.setTenant(tenant);

    Favorite favorite = new Favorite();
    favorite.setFavoriteId(100L);
    favorite.setUser(user);
    favorite.setProduct(product);

    AddFavoriteRequest request = new AddFavoriteRequest();
    request.setProductId(10L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(productRepository.findByProductIdAndTenant(10L, tenant))
            .thenReturn(Optional.of(product));

    when(favoriteRepository.save(any(Favorite.class)))
            .thenReturn(favorite);

    FavoriteResponse response =
            favoriteService.addFavorite("nike", 1L, request);

    assertNotNull(response);
    assertEquals(100L, response.getFavoriteId());
    assertEquals(10L, response.getProductId());
    assertEquals("Air Max", response.getProductName());

    verify(favoriteRepository).save(any(Favorite.class));
}

@Test
void addFavorite_ShouldThrow_WhenTenantNotFound() {

    AddFavoriteRequest request = new AddFavoriteRequest();
    request.setProductId(10L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> favoriteService.addFavorite("nike", 1L, request));

    assertEquals("Tenant not found", exception.getMessage());

    verify(favoriteRepository, never()).save(any(Favorite.class));
}

@Test
void addFavorite_ShouldThrow_WhenAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    AddFavoriteRequest request = new AddFavoriteRequest();
    request.setProductId(10L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    assertThrows(AccessDeniedException.class,
            () -> favoriteService.addFavorite("nike", 2L, request));

    verify(userRepository, never()).findById(anyLong());
    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(favoriteRepository, never()).save(any(Favorite.class));
}

@Test
void addFavorite_ShouldThrow_WhenUserNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    AddFavoriteRequest request = new AddFavoriteRequest();
    request.setProductId(10L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    when(userRepository.findById(1L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> favoriteService.addFavorite("nike", 1L, request));

    assertEquals("User not found", exception.getMessage());

    verify(productRepository, never()).findByProductIdAndTenant(anyLong(), any(Tenant.class));
    verify(favoriteRepository, never()).save(any(Favorite.class));
}

@Test
void getFavoritesByUser_ShouldReturnFavoritesSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductId(10L);
    product.setProductName("Air Max");

    Favorite favorite = new Favorite();
    favorite.setFavoriteId(100L);
    favorite.setUser(user);
    favorite.setProduct(product);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));

    when(favoriteRepository.findByUser(user))
            .thenReturn(List.of(favorite));

    List<FavoriteResponse> response =
            favoriteService.getFavoritesByUser("nike", 1L);

    assertEquals(1, response.size());
    assertEquals(100L, response.getFirst().getFavoriteId());
    assertEquals("Air Max", response.getFirst().getProductName());

    verify(favoriteRepository).findByUser(user);
}

@Test
void getFavoritesByUser_ShouldThrow_WhenAccessDenied() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User loggedInUser = new User();
    loggedInUser.setUserId(1L);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(loggedInUser));

    assertThrows(AccessDeniedException.class,
            () -> favoriteService.getFavoritesByUser("nike", 2L));

    verify(userRepository, never()).findById(anyLong());
    verify(favoriteRepository, never()).findByUser(any(User.class));
}

@Test
void getFavoritesByUser_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> favoriteService.getFavoritesByUser("nike", 1L));

    assertEquals("Tenant not found", exception.getMessage());

    verify(favoriteRepository, never()).findByUser(any(User.class));
}

@Test
void removeFavorite_ShouldDeleteSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    User user = new User();
    user.setUserId(1L);

    Product product = new Product();
    product.setProductId(10L);

    Favorite favorite = new Favorite();
    favorite.setFavoriteId(100L);
    favorite.setUser(user);
    favorite.setProduct(product);

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(favoriteRepository.findById(100L))
            .thenReturn(Optional.of(favorite));

    when(userRepository.findByUsername("tenantuser"))
            .thenReturn(Optional.of(user));

    favoriteService.removeFavorite("nike", 100L);

    verify(favoriteRepository).delete(favorite);
}

@Test
void removeFavorite_ShouldThrow_WhenFavoriteNotFound() {

    Tenant tenant = new Tenant();
    tenant.setTenantName("nike");

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    when(favoriteRepository.findById(100L))
            .thenReturn(Optional.empty());

    ResourceNotFoundException exception =
            assertThrows(ResourceNotFoundException.class,
                    () -> favoriteService.removeFavorite("nike", 100L));

    assertEquals("Favorite not found", exception.getMessage());

    verify(favoriteRepository, never()).delete(any(Favorite.class));
}
}