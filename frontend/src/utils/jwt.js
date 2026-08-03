import { jwtDecode } from "jwt-decode";

export const decodeToken = (token) => {
  if (!token) return null;

  try {
    return jwtDecode(token);
  } catch (error) {
    console.error("Failed to decode token", error);
    return null;
  }
};

export const getUsernameFromToken = (token) => {
  const decoded = decodeToken(token);
  console.log(jwtDecode(token));

  return decoded?.preferred_username || "";
};

export const getRolesFromToken = (token) => {
  const decoded = decodeToken(token);
  console.log(jwtDecode(token));

  return decoded?.realm_access?.roles || [];
};

export const getAuthDataFromToken = (token) => {
  const decoded = decodeToken(token);
  console.log(jwtDecode(token));

  return {
    username: decoded?.preferred_username || decoded?.username || null,
    roles: decoded?.realm_access?.roles || [],
  };
};