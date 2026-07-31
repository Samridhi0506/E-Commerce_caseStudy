import api from "./api";

export const addToCart = async (
  userId,
  productId,
  quantity = 1
) => {
  return await api.post(`/cart/${userId}`, {
    productId,
    quantity,
  });
};

export const getCart = async (userId) => {
  return await api.get(`/cart/${userId}`);
};

export const updateCartQuantity = async (
  cartId,
  quantity
) => {
  return await api.put(`/cart/${cartId}`, null, {
    params: { quantity },
  });
};

export const removeFromCart = async (cartId) => {
  return await api.delete(`/cart/${cartId}`);
};