import api from "./api";

export const signup = async (userData) => {
  return await api.post("/auth/signup", userData);
};

export const login = async (credentials) => {
  return await api.post("/auth/login", credentials);
};