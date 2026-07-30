package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.CreateTenantRequest;
import com.ecommerce.backend.dto.response.TenantResponse;
import com.ecommerce.backend.entity.Role;
import com.ecommerce.backend.entity.Tenant;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.RoleRepository;
import com.ecommerce.backend.repository.TenantRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.impl.TenantServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks
    private TenantServiceImpl tenantService;

    @Test
void createTenant_ShouldCreateSuccessfully() {

    CreateTenantRequest request = new CreateTenantRequest();
    request.setTenantName("nike");
    request.setDomain("nike.com");

    Tenant tenant = Tenant.builder()
            .tenantId(1L)
            .tenantName("nike")
            .domain("nike.com")
            .build();

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.empty());

    when(tenantRepository.save(any(Tenant.class)))
            .thenReturn(tenant);

    TenantResponse response = tenantService.createTenant(request);

    assertNotNull(response);
    assertEquals(1L, response.getTenantId());
    assertEquals("nike", response.getTenantName());
    assertEquals("nike.com", response.getDomain());

    verify(tenantRepository).save(any(Tenant.class));
}

@Test
void createTenant_ShouldThrow_WhenTenantAlreadyExists() {

    CreateTenantRequest request = new CreateTenantRequest();
    request.setTenantName("nike");

    Tenant tenant = new Tenant();

    when(tenantRepository.findByTenantName("nike"))
            .thenReturn(Optional.of(tenant));

    RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> tenantService.createTenant(request));

    assertEquals("Tenant already exists.", exception.getMessage());

    verify(tenantRepository, never()).save(any(Tenant.class));
}

@Test
void getAllTenants_ShouldReturnAllTenants() {

    Tenant tenant1 = Tenant.builder()
            .tenantId(1L)
            .tenantName("Nike")
            .domain("nike.com")
            .build();

    Tenant tenant2 = Tenant.builder()
            .tenantId(2L)
            .tenantName("Puma")
            .domain("puma.com")
            .build();

    when(tenantRepository.findAll())
            .thenReturn(List.of(tenant1, tenant2));

    List<TenantResponse> response = tenantService.getAllTenants();

    assertEquals(2, response.size());
    assertEquals("Nike", response.get(0).getTenantName());
    assertEquals("Puma", response.get(1).getTenantName());

    verify(tenantRepository).findAll();
}



@Test
void getTenantById_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> tenantService.getTenantById(1L));

    assertEquals("Tenant not found.", exception.getMessage());
}

@Test
void updateTenant_ShouldUpdateSuccessfully() {

    Tenant tenant = Tenant.builder()
            .tenantId(1L)
            .tenantName("Nike")
            .domain("nike.com")
            .build();

    CreateTenantRequest request = new CreateTenantRequest();
    request.setTenantName("Adidas");
    request.setDomain("adidas.com");

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.of(tenant));

    when(tenantRepository.save(any(Tenant.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    TenantResponse response =
            tenantService.updateTenant(1L, request);

    assertEquals("Adidas", response.getTenantName());
    assertEquals("adidas.com", response.getDomain());

    verify(tenantRepository).save(any(Tenant.class));
}

@Test
void updateTenant_ShouldThrow_WhenTenantNotFound() {

    CreateTenantRequest request = new CreateTenantRequest();
    request.setTenantName("Nike");

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> tenantService.updateTenant(1L, request));

    assertEquals("Tenant not found.", exception.getMessage());
}

@Test
void deleteTenant_ShouldDeleteSuccessfully() {

    Tenant tenant = Tenant.builder()
            .tenantId(1L)
            .build();

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.of(tenant));

    tenantService.deleteTenant(1L);

    verify(tenantRepository).delete(tenant);
}

@Test
void deleteTenant_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> tenantService.deleteTenant(1L));

    assertEquals("Tenant not found.", exception.getMessage());
}

@Test
void assignTenantAdmin_ShouldAssignSuccessfully() {

    Tenant tenant = new Tenant();
    tenant.setTenantId(1L);

    User user = new User();
    user.setUserId(2L);
    user.setKeycloakId("kc123");

    Role role = new Role();
    role.setRoleName("ROLE_TENANT");

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findById(2L))
            .thenReturn(Optional.of(user));

    when(roleRepository.findByRoleName("ROLE_TENANT"))
            .thenReturn(Optional.of(role));

    tenantService.assignTenantAdmin(1L, 2L);

    assertEquals(tenant, user.getTenant());
    assertEquals(role, user.getRole());

    verify(userRepository).save(user);
    verify(keycloakService).removeRealmRole("kc123", "ROLE_USER");
    verify(keycloakService).assignRealmRole("kc123", "ROLE_TENANT");
}

@Test
void assignTenantAdmin_ShouldThrow_WhenTenantNotFound() {

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> tenantService.assignTenantAdmin(1L, 2L));

    assertEquals("Tenant not found.", exception.getMessage());

    verify(userRepository, never()).findById(anyLong());
}

@Test
void assignTenantAdmin_ShouldThrow_WhenUserNotFound() {

    Tenant tenant = new Tenant();

    when(tenantRepository.findById(1L))
            .thenReturn(Optional.of(tenant));

    when(userRepository.findById(2L))
            .thenReturn(Optional.empty());

    RuntimeException exception =
            assertThrows(RuntimeException.class,
                    () -> tenantService.assignTenantAdmin(1L, 2L));

    assertEquals("User not found.", exception.getMessage());

    verify(roleRepository, never()).findByRoleName(anyString());
}
}