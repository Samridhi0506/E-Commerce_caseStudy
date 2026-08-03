import { useEffect, useState } from "react";
import { Badge, Button, Card, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import { toast } from "react-toastify";
import FixedPagination from "../../components/common/FixedPagination";
import { createTenant, deleteTenant, getTenants, updateTenant, assignTenantAdmin } from "../../services/tenantService";
import { getUsers } from "../../services/userService";

function AdminTenants() {
  const [tenants, setTenants] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tenantForm, setTenantForm] = useState({ tenantName: "", domain: "" });
  const [editingTenantId, setEditingTenantId] = useState(null);
  const [selectedTenant, setSelectedTenant] = useState("");
  const [selectedUserId, setSelectedUserId] = useState("");
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [currentTenantPage, setCurrentTenantPage] = useState(1);
  const [currentTenantUserPage, setCurrentTenantUserPage] = useState(1);
  const itemsPerPage = 6;

  const loadData = async () => {
    try {
      const [tenantResponse, userResponse] = await Promise.all([getTenants(), getUsers()]);
      setTenants(tenantResponse.data || []);
      setUsers(userResponse.data || []);
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to load tenant data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleTenantSubmit = async (event) => {
    event.preventDefault();

    try {
      if (editingTenantId) {
        await updateTenant(editingTenantId, tenantForm);
        toast.success("Tenant updated successfully.");
      } else {
        await createTenant(tenantForm);
        toast.success("Tenant created successfully.");
      }

      setTenantForm({ tenantName: "", domain: "" });
      setEditingTenantId(null);
      await loadData();
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to save tenant.");
    }
  };

  const handleEditTenant = (tenant) => {
    setEditingTenantId(tenant.tenantId);
    setTenantForm({ tenantName: tenant.tenantName, domain: tenant.domain });
  };

  const handleDeleteTenant = async (tenantId) => {
    try {
      await deleteTenant(tenantId);
      toast.success("Tenant removed successfully.");
      await loadData();
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to delete tenant.");
    }
  };

  const handleAssignTenantAdmin = async () => {
    if (!selectedUserId || !selectedTenant) {
      toast.error("Choose a user and tenant first.");
      return;
    }

    const tenant = tenants.find((item) => item.tenantName === selectedTenant);
    if (!tenant) {
      toast.error("Selected tenant not found.");
      return;
    }

    try {
      await assignTenantAdmin(tenant.tenantId, Number(selectedUserId));
      toast.success("Tenant admin assigned successfully.");
      await loadData();
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to assign tenant admin.");
    }
  };

  const filteredTenants = tenants.filter((tenant) =>
    tenant.tenantName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    tenant.domain.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const tenantPageCount = Math.max(Math.ceil(filteredTenants.length / itemsPerPage), 1);
  const paginatedTenants = filteredTenants.slice(
    (currentTenantPage - 1) * itemsPerPage,
    currentTenantPage * itemsPerPage
  );

  const tenantAdminCount = users.filter((user) => user.role === "ROLE_TENANT").length;
  const adminCount = users.filter((user) => user.role === "ROLE_ADMIN").length;
  const tenantUsers = users.filter((user) => user.role === "ROLE_TENANT");
  const selectedTenantUsers = users.filter(
    (user) => user.role === "ROLE_TENANT" && user.tenant === selectedTenant
  );

  const selectedTenantUserPageCount = Math.max(
    Math.ceil(selectedTenantUsers.length / itemsPerPage),
    1
  );
  const paginatedSelectedTenantUsers = selectedTenantUsers.slice(
    (currentTenantUserPage - 1) * itemsPerPage,
    currentTenantUserPage * itemsPerPage
  );

  const handleSelectUser = (user) => {
    setSelectedUser(user);
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <div className="mb-4">
        <h2 className="mb-1">Tenant Management</h2>
      </div>

      <Row className="g-4 mb-4">
        <Col md={6}>
          <Card className="shadow-sm h-100">
            <Card.Body>
              <Card.Title>{editingTenantId ? "Edit Tenant" : "Create Tenant"}</Card.Title>
              <Card.Text className="text-muted mb-4">
                Add a new marketplace brand or update an existing tenant entry.
              </Card.Text>
              <Form onSubmit={handleTenantSubmit}>
                <Form.Group className="mb-3">
                  <Form.Label>Tenant Name</Form.Label>
                  <Form.Control
                    value={tenantForm.tenantName}
                    onChange={(e) => setTenantForm({ ...tenantForm, tenantName: e.target.value })}
                    placeholder="e.g. nike"
                    required
                  />
                </Form.Group>

                <Form.Group className="mb-3">
                  <Form.Label>Domain</Form.Label>
                  <Form.Control
                    value={tenantForm.domain}
                    onChange={(e) => setTenantForm({ ...tenantForm, domain: e.target.value })}
                    placeholder="e.g. nike.example.com"
                  />
                </Form.Group>

                <Button type="submit" variant="primary">
                  {editingTenantId ? "Update Tenant" : "Create Tenant"}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6}>
          <Card className="shadow-sm h-100">
            <Card.Body>
              <Card.Title>Tenant Admin Assignment</Card.Title>
              <Card.Text className="text-muted mb-4">
                Select a tenant and assign a user to the tenant admin role.
              </Card.Text>
              <Form>
                <Row className="g-2">
                  <Col xs={12} lg={6}>
                    <Form.Label>Tenant</Form.Label>
                    <Form.Select value={selectedTenant} onChange={(e) => setSelectedTenant(e.target.value)}>
                      <option value="">Choose Tenant</option>
                      {tenants.map((tenant) => (
                        <option key={tenant.tenantId} value={tenant.tenantName}>
                          {tenant.tenantName}
                        </option>
                      ))}
                    </Form.Select>
                  </Col>
                  <Col xs={12} lg={6}>
                    <Form.Label>User</Form.Label>
                    <Form.Select value={selectedUserId} onChange={(e) => setSelectedUserId(e.target.value)}>
                      <option value="">Select User</option>
                      {users
                        .filter((user) => user.role === "ROLE_USER")
                        .map((user) => (
                          <option key={user.userId} value={user.userId}>
                            {user.username} — {user.email}
                          </option>
                        ))}
                    </Form.Select>
                  </Col>
                </Row>
                <Button className="mt-4" onClick={handleAssignTenantAdmin} variant="outline-primary">
                  Assign Tenant Admin
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="g-4 mb-4">
        <Col md={6}>
          <Card className="shadow-sm border-info">
            <Card.Body>
              <Card.Title>Tenant Brands</Card.Title>
              <Card.Text className="text-muted">Total tenant brands currently registered.</Card.Text>
              <div className="display-6 fw-bold">{tenants.length}</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="shadow-sm border-success">
            <Card.Body>
              <Card.Title>Tenant Admins</Card.Title>
              <Card.Text className="text-muted">Users with tenant admin access.</Card.Text>
              <div className="display-6 fw-bold">{tenantAdminCount}</div>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="shadow-sm border-secondary">
            <Card.Body>
              <Card.Title>Platform Admins</Card.Title>
              <Card.Text className="text-muted">Total admin users.</Card.Text>
              <div className="display-6 fw-bold">{adminCount}</div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="shadow-sm mb-4">
        <Card.Body>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <div>
              <h5 className="mb-1">Tenant List</h5>
            </div>
            <Badge bg="secondary">{filteredTenants.length} entries</Badge>
          </div>

          <div className="mb-3">
            <Form.Control
              placeholder="Search tenants or domains"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ minWidth: 240 }}
            />
          </div>

          <Table bordered hover responsive className="align-middle">
            <thead className="table-light">
              <tr>
                <th>Brand</th>
                <th>Domain</th>
                <th>Assigned Tenant Users</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {paginatedTenants.map((tenant) => {
                const tenantUsersForRow = users.filter(
                  (user) => user.role === "ROLE_TENANT" && user.tenant === tenant.tenantName
                );

                return (
                  <tr
                    key={tenant.tenantId}
                    onClick={() => {
                      setSelectedTenant(tenant.tenantName);
                      setSelectedUser(null);
                    }}
                    style={{ cursor: "pointer" }}
                    className={selectedTenant === tenant.tenantName ? "table-active" : ""}
                  >
                    <td>
                      <div className="fw-semibold">{tenant.tenantName}</div>
                    </td>
                    <td>{tenant.domain}</td>
                    <td>
                      {tenantUsersForRow.length > 0 ? (
                        tenantUsersForRow.map((user) => (
                          <Badge key={user.userId} bg="light" text="dark" className="me-1 mb-1">
                            {user.username}
                          </Badge>
                        ))
                      ) : (
                        <span className="text-muted">No tenant users assigned</span>
                      )}
                    </td>
                    <td>
                      <Button
                        size="sm"
                        variant="outline-secondary"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleEditTenant(tenant);
                        }}
                      >
                        Edit
                      </Button>{" "}
                      <Button
                        size="sm"
                        variant="outline-danger"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDeleteTenant(tenant.tenantId);
                        }}
                      >
                        Delete
                      </Button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </Table>
          <FixedPagination
            currentPage={currentTenantPage}
            totalPages={tenantPageCount}
            onPageChange={setCurrentTenantPage}
          />
        </Card.Body>
      </Card>

      {selectedTenant ? (
        <Row className="g-4 mb-4">
          <Col lg={8}>
            <Card className="shadow-sm h-100">
              <Card.Body>
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <div>
                    <Card.Title className="mb-1">Tenant Users</Card.Title>
                    <Card.Text className="text-muted mb-0">
                      Users assigned to {selectedTenant}. Click a row to view details.
                    </Card.Text>
                  </div>
                  <Badge bg="secondary">{selectedTenantUsers.length} users</Badge>
                </div>

                <Table bordered hover responsive>
                  <thead className="table-light">
                    <tr>
                      <th>Username</th>
                      <th>Email</th>
                      <th>Role</th>
                      <th>Tenant</th>
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedSelectedTenantUsers.map((user) => (
                      <tr
                        key={user.userId}
                        onClick={() => handleSelectUser(user)}
                        style={{ cursor: "pointer" }}
                        className={selectedUser?.userId === user.userId ? "table-active" : ""}
                      >
                        <td>{user.username}</td>
                        <td>{user.email}</td>
                        <td>{user.role.replace("ROLE_", "")}</td>
                        <td>{user.tenant || "—"}</td>
                      </tr>
                    ))}
                    {paginatedSelectedTenantUsers.length === 0 && (
                      <tr>
                        <td colSpan={4} className="text-center text-muted py-4">
                          No tenant users are assigned to {selectedTenant} yet.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </Table>
                <FixedPagination
                  currentPage={currentTenantUserPage}
                  totalPages={selectedTenantUserPageCount}
                  onPageChange={setCurrentTenantUserPage}
                />
              </Card.Body>
            </Card>
          </Col>
          <Col lg={4}>
            <Card className="shadow-sm h-100">
              <Card.Body>
                <Card.Title>User Details</Card.Title>
                {selectedUser ? (
                  <div className="mt-3">
                    <p className="mb-2">
                      <strong>Username:</strong> {selectedUser.username}
                    </p>
                    <p className="mb-2">
                      <strong>Email:</strong> {selectedUser.email}
                    </p>
                    <p className="mb-2">
                      <strong>Role:</strong> {selectedUser.role.replace("ROLE_", "")}
                    </p>
                    <p className="mb-2">
                      <strong>Tenant:</strong> {selectedUser.tenant || "Not assigned"}
                    </p>
                    <p className="mb-2">
                      <strong>User ID:</strong> {selectedUser.userId}
                    </p>
                  </div>
                ) : (
                  <div className="mt-3 text-muted">
                    <p className="mb-2">No user selected.</p>
                    <p className="mb-0">Click a tenant user row to view full details.</p>
                  </div>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>
      ) : null}
    </Container>
  );
}

export default AdminTenants;
