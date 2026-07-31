import api from "./api";

export const getTenants = async () => {
  return await api.get(`/tenants`);
};

export const createTenant = async (tenant) => {
  return await api.post(`/tenants`, tenant);
};

export const getTenantById = async (tenantId) => {
  return await api.get(`/tenants/${tenantId}`);
};

export const updateTenant = async (tenantId, tenant) => {
  return await api.put(`/tenants/${tenantId}`, tenant);
};

export const deleteTenant = async (tenantId) => {
  return await api.delete(`/tenants/${tenantId}`);
};

export const assignTenantAdmin = async (tenantId, userId) => {
  return await api.post(`/tenants/${tenantId}/assign-admin/${userId}`);
};