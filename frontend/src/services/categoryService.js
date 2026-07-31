import api from "./api";

export const getCategories = async (tenantName) => {
  return await api.get(`/categories/${tenantName}`);
};

export const createCategory = async (tenantName, category) => {
  return await api.post(`/categories/${tenantName}`, category);
};

export const updateCategory = async (tenantName, categoryId, category) => {
  return await api.put(`/categories/${tenantName}/${categoryId}`, category);
};

export const deleteCategory = async (tenantName, categoryId) => {
  return await api.delete(`/categories/${tenantName}/${categoryId}`);
};