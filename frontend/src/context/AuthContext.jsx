import { createContext, useContext, useState } from "react";
import { getAuthDataFromToken } from "../utils/jwt";

const AuthContext = createContext();

const getStoredAuth = () => {
  const raw = localStorage.getItem("authData");

  if (!raw) {
    return {
      accessToken: null,
      refreshToken: null,
      expiresIn: null,
      userId: null,
      username: null,
      role: null,
      tenantName: null,
      roles: [],
      isAuthenticated: false,
    };
  }

  try {
    const parsed = JSON.parse(raw);

    return {
      accessToken: parsed.accessToken || null,
      refreshToken: parsed.refreshToken || null,
      expiresIn: parsed.expiresIn || null,
      userId: parsed.userId || null,
      username: parsed.username || null,
      role: parsed.role || null,
      tenantName: parsed.tenantName || null,
      roles: parsed.roles || [],
      isAuthenticated: !!parsed.accessToken,
    };
  } catch (error) {
    localStorage.removeItem("authData");

    return {
      accessToken: null,
      refreshToken: null,
      expiresIn: null,
      userId: null,
      username: null,
      role: null,
      tenantName: null,
      roles: [],
      isAuthenticated: false,
    };
  }
};

const normalizeRole = (role) => {
  if (!role) return "";
  return role.startsWith("ROLE_") ? role : `ROLE_${role}`;
};

export function AuthProvider({ children }) {
  const [authState, setAuthState] = useState(getStoredAuth);

  const login = ({
    accessToken,
    refreshToken,
    expiresIn,
    userId,
    username,
    role,
    tenantName,
  }) => {
    const { roles } = getAuthDataFromToken(accessToken);

    const nextAuth = {
      accessToken,
      refreshToken,
      expiresIn,
      userId,
      username,
      role,
      tenantName,
      roles,
      isAuthenticated: true,
    };

    localStorage.setItem("authData", JSON.stringify(nextAuth));
    setAuthState(nextAuth);
  };

  const logout = () => {
    localStorage.removeItem("authData");

    setAuthState({
      accessToken: null,
      refreshToken: null,
      expiresIn: null,
      userId: null,
      username: null,
      role: null,
      tenantName: null,
      roles: [],
      isAuthenticated: false,
    });
  };

  const hasRole = (role) => {
    if (!authState.roles?.length) return false;

    const normalized = normalizeRole(role);

    return authState.roles
      .map(normalizeRole)
      .includes(normalized);
  };

  const hasAnyRole = (roles = []) => {
    return roles.some((role) => hasRole(role));
  };

  return (
    <AuthContext.Provider
      value={{
        ...authState,
        login,
        logout,
        hasRole,
        hasAnyRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);