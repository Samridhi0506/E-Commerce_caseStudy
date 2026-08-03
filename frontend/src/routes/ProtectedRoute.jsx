import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function ProtectedRoute({ children, roles = [] }) {
  const { isAuthenticated, hasAnyRole } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (roles.length > 0 && !hasAnyRole(roles)) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default ProtectedRoute;
