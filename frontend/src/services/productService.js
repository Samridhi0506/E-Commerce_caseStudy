import api from "./api";

export const getProducts = async (tenantName, page = 0, size = 10) => {
  return await api.get(`/products/${tenantName}`, {
    params: { page, size },
  });
};

export const searchProducts = async (tenantName, keyword) => {
  return await api.get(`/products/${tenantName}/search`, {
    params: { keyword },
  });
};

export const getProductsByCategory = async (tenantName, categoryId) => {
  return await api.get(`/products/${tenantName}/category/${categoryId}`);
};

export const createProduct = async (tenantName, product) => {
  return await api.post(`/products/${tenantName}`, product);
};

export const updateProduct = async (tenantName, productId, update) => {
  return await api.put(`/products/${tenantName}/${productId}`, update);
};

export const updateStock = async (tenantName, productId, quantity) => {
  return await api.patch(`/products/${tenantName}/${productId}/stock`, null, {
    params: { quantity },
  });
};

export const deleteProduct = async (tenantName, productId) => {
  return await api.delete(`/products/${tenantName}/${productId}`);
};