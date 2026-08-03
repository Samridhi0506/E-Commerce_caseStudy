import api from "./api";

export const getUsers = async () => {
  return await api.get("/users");
};

export const getUserById = async (userId) => {
  return await api.get(`/users/${userId}`);
};

export const getUserOrders = async (userId) => {
  return await api.get(`/users/${userId}/orders`);
};
