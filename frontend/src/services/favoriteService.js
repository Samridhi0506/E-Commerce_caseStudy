import api from "./api";

export const addFavorite = async (
  tenantName,
  userId,
  productId
) => {
  return await api.post(
    `/favorites/${tenantName}/user/${userId}`,
    {
      productId,
    }
  );
};

export const getFavorites = async (
  tenantName,
  userId
) => {
  return await api.get(
    `/favorites/${tenantName}/user/${userId}`
  );
};

export const removeFavorite = async (
  tenantName,
  favoriteId
) => {
  return await api.delete(
    `/favorites/${tenantName}/${favoriteId}`
  );
};