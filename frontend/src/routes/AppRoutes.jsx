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
import ProtectedRoute from "./ProtectedRoute";

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

      {/* Protected Routes */}
      <Route
        path="/products/:tenantName"
        element={
          <ProtectedRoute roles={["ROLE_USER", "ROLE_TENANT"]}>
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
        path="/tenant/products"
        element={
          <ProtectedRoute roles={["ROLE_TENANT"]}>
            <ManageProducts />
          </ProtectedRoute>
        }
      />

      {/* Unknown Routes */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRoutes;