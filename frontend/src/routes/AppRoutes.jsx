import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import Home from "../pages/home/Home";
import Login from "../pages/auth/Login";
import Signup from "../pages/auth/Signup";
import ProductsPage from "../pages/product/ProductsPage";
import CategoriesPage from "../pages/category/CategoriesPage";
import FavoritesPage from "../pages/favorite/FavoritesPage";
import OrdersPage from "../pages/order/OrdersPage";
import TenantDashboard from "../pages/tenant/TenantDashboard";
import ManageProducts from "../pages/tenant/ManageProducts";
import TenantOrders from "../pages/tenant/TenantOrders";
import ProtectedRoute from "./ProtectedRoute";
import ProductDetails from "../pages/product/ProductDetails";
import CartPage from "../pages/cart/CartPage";
import AdminDashboard from "../pages/admin/AdminDashboard";
import AdminTenants from "../pages/admin/AdminTenants";
import AdminCategories from "../pages/admin/AdminCategories";
import AdminUsers from "../pages/admin/AdminUsers";

function AppRoutes() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      {/* Public Routes */}
      <Route
        path="/login"
        element={
          isAuthenticated ? <Navigate to="/" replace /> : <Login />
        }
      />

      <Route
  path="/cart"
  element={
    <ProtectedRoute roles={["ROLE_USER", "ROLE_TENANT"]}>
      <CartPage />
    </ProtectedRoute>
  }
/>

      <Route
        path="/signup"
        element={
          isAuthenticated ? <Navigate to="/" replace /> : <Signup />
        }
      />

      {/* Home (Public) */}
      <Route
  path="/"
  element={
    <ProtectedRoute>
      <Home />
    </ProtectedRoute>
  }
/>

<Route
  path="/product/:productId"
  element={
    <ProtectedRoute>
      <ProductDetails />
    </ProtectedRoute>
  }
/>

      {/* Protected Routes */}
      <Route
        path="/products/:tenantName"
        element={
          <ProtectedRoute roles={["ROLE_USER", "ROLE_TENANT", "ROLE_ADMIN"]}>
            <ProductsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/categories"
        element={
          <ProtectedRoute roles={["ROLE_USER", "ROLE_TENANT"]}>
            <CategoriesPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/favorites"
        element={
          <ProtectedRoute roles={["ROLE_USER", "ROLE_TENANT"]}>
            <FavoritesPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/orders"
        element={
          <ProtectedRoute roles={["ROLE_USER", "ROLE_TENANT"]}>
            <OrdersPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute roles={["ROLE_TENANT"]}>
            <TenantDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN"]}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/tenants"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN"]}>
            <AdminTenants />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/categories"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN"]}>
            <AdminCategories />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/users"
        element={
          <ProtectedRoute roles={["ROLE_ADMIN"]}>
            <AdminUsers />
          </ProtectedRoute>
        }
      />

      <Route
        path="/tenant/products"
        element={
          <ProtectedRoute roles={["ROLE_TENANT"]}>
            <ManageProducts />
          </ProtectedRoute>
        }
      />

      <Route
        path="/tenant/orders"
        element={
          <ProtectedRoute roles={["ROLE_TENANT"]}>
            <TenantOrders />
          </ProtectedRoute>
        }
      />

      {/* Unknown Routes */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRoutes;