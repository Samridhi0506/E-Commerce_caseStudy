import api from "./api";

export const placeOrder = async (tenantName, userId, order) => {
  return await api.post(
    `/orders/${tenantName}/user/${userId}`,
    order
  );
};

export const checkout = async (tenantName, userId) => {
  return await api.post(
    `/orders/${tenantName}/user/${userId}/checkout`
  );
};

export const getOrders = async (tenantName, userId) => {
  return await api.get(
    `/orders/${tenantName}/user/${userId}`
  );
};

export const getTenantOrders = async (tenantName) => {
  return await api.get(
    `/orders/${tenantName}/tenant`
  );
};

export const getOrderById = async (tenantName, orderId) => {
  return await api.get(
    `/orders/${tenantName}/${orderId}`
  );
};

export const cancelOrder = async (tenantName, orderId) => {
  return await api.delete(
    `/orders/${tenantName}/${orderId}`
  );
};

export const updateOrderStatus = async (tenantName, orderId, status) => {
  return await api.patch(
    `/orders/${tenantName}/${orderId}/status`,
    null,
    { params: { status } }
  );
};