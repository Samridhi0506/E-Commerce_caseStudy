import { useEffect, useState } from "react";
import { Badge, Button, Card, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import { toast } from "react-toastify";
import FixedPagination from "../../components/common/FixedPagination";
import { getUsers, getUserOrders } from "../../services/userService";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;
  const [loading, setLoading] = useState(true);

  const loadUsers = async () => {
    try {
      const response = await getUsers();
      setUsers(response.data || []);
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to load users.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleViewOrders = async (user) => {
    try {
      setSelectedUser(user);
      const response = await getUserOrders(user.userId);
      setOrders(response.data || []);
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to load orders.");
    }
  };

  const filteredUsers = users.filter((user) =>
    [user.username, user.email, user.role, user.tenant || ""]
      .join(" ")
      .toLowerCase()
      .includes(searchQuery.toLowerCase())
  );

  const totalUsers = users.length;
  const filteredCount = filteredUsers.length;
  const selectedOrderCount = orders.length;
  const selectedOrderValue = orders.reduce(
    (sum, order) => sum + Number(order.totalAmount || 0),
    0
  );
  const pageCount = Math.ceil(filteredUsers.length / itemsPerPage) || 1;
  const paginatedUsers = filteredUsers.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
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
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start gap-3 mb-4">
        <div>
          <h2 className="mb-2">User & Order Insights</h2>
          <p className="text-muted mb-0">
            Browse users, filter quickly, and review order history for selected accounts.
          </p>
        </div>
      </div>

      <Row className="g-3 mb-4">
        <Col md={4}>
          <Card className="shadow-sm h-100 border-0">
            <Card.Body>
              <Card.Title>Total Users</Card.Title>
              <h3>{totalUsers}</h3>
              <Card.Text className="text-muted mb-0">
                Registered users in the system.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="shadow-sm h-100 border-0">
            <Card.Body>
              <Card.Title>Search Results</Card.Title>
              <h3>{filteredCount}</h3>
              <Card.Text className="text-muted mb-0">
                Users matching the current search.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="shadow-sm h-100 border-0">
            <Card.Body>
              <Card.Title>Selected Orders</Card.Title>
              <h3>{selectedOrderCount}</h3>
              <Card.Text className="text-muted mb-0">
                Orders for {selectedUser?.username || "the selected user"}.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="shadow-sm">
        <Card.Body>
          <div className="d-flex flex-column flex-lg-row justify-content-between align-items-start align-items-lg-center mb-3 gap-3">
            <div>
              <h5 className="mb-1">Users</h5>
              <p className="text-muted mb-0">Search by username, email, role, or tenant.</p>
            </div>
            <Form.Control
              type="search"
              placeholder="Search users"
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(1);
              }}
              style={{ maxWidth: 320 }}
            />
          </div>
          <Table striped bordered hover responsive className="mt-3">
            <thead>
              <tr>
                <th>User ID</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Tenant</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {paginatedUsers.map((user) => (
                <tr key={user.userId}>
                  <td>{user.userId}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>{user.role}</td>
                  <td>{user.tenant || "-"}</td>
                  <td>
                    <Button size="sm" onClick={() => handleViewOrders(user)}>
                      View Orders
                    </Button>
                  </td>
                </tr>
              ))}
              {paginatedUsers.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center text-muted py-4">
                    No users found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
          <FixedPagination
            currentPage={currentPage}
            totalPages={pageCount}
            onPageChange={handlePageChange}
          />
        </Card.Body>
      </Card>

      {orders.length > 0 && (
        <>
          <Row className="g-3 mb-4">
            <Col md={4}>
              <Card className="shadow-sm h-100 border-0">
                <Card.Body>
                  <Card.Title>Orders Loaded</Card.Title>
                  <h3>{selectedOrderCount}</h3>
                  <Card.Text className="text-muted mb-0">
                    Orders for the selected user.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="shadow-sm h-100 border-0">
                <Card.Body>
                  <Card.Title>Total Order Value</Card.Title>
                  <h3>₹{selectedOrderValue.toFixed(2)}</h3>
                  <Card.Text className="text-muted mb-0">
                    Revenue across loaded orders.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="shadow-sm h-100 border-0">
                <Card.Body>
                  <Card.Title>Average Order</Card.Title>
                  <h3>
                    ₹{(selectedOrderCount ? selectedOrderValue / selectedOrderCount : 0).toFixed(2)}
                  </h3>
                  <Card.Text className="text-muted mb-0">
                    Average value of loaded orders.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          </Row>

          <Card className="shadow-sm">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <div>
                  <h5 className="mb-0">Orders for {selectedUser?.username}</h5>
                  <small className="text-muted">Most recent orders appear first.</small>
                </div>
                <Badge bg="info">{selectedOrderCount} orders</Badge>
              </div>
              <Table striped bordered hover responsive className="mt-3">
                <thead>
                  <tr>
                    <th>Total</th>
                    <th>Date</th>
                    <th>Items</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.orderId}>
                      <td>₹{Number(order.totalAmount).toFixed(2)}</td>
                      <td>{order.orderDate}</td>
                      <td>
                        {order.items?.map((item) => (
                          <div key={`${order.orderId}-${item.productName}`}>
                            {item.productName} × {item.quantity}
                          </div>
                        ))}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </>
      )}
    </Container>
  );
}

export default AdminUsers;
