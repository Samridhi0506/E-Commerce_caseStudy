import api from "./api";

export const placeOrder = async (tenantName, userId, order) => {
  return await api.post(`/orders/${tenantName}/user/${userId}`, order);
};

export const getOrders = async (tenantName, userId) => {
  return await api.get(`/orders/${tenantName}/user/${userId}`);
};

export const getOrderById = async (tenantName, orderId) => {
  return await api.get(`/orders/${tenantName}/${orderId}`);
};

export const cancelOrder = async (tenantName, orderId) => {
  return await api.delete(`/orders/${tenantName}/${orderId}`);
};