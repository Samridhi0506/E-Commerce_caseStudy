import api from "./api";

export const getCategories = async () => {
  return await api.get(`/categories`);
};

export const createCategory = async (category) => {
  return await api.post(`/categories`, category);
};

export const updateCategory = async (categoryId, category) => {
  return await api.put(`/categories/${categoryId}`, category);
};

export const deleteCategory = async (categoryId) => {
  return await api.delete(`/categories/${categoryId}`);
};