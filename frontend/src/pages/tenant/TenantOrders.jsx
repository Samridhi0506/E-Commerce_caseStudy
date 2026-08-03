import { useEffect, useState } from "react";
import {
  Badge,
  Button,
  Card,
  Col,
  Container,
  Form,
  Row,
  Table,
  Spinner,
} from "react-bootstrap";
import { getTenantOrders, updateOrderStatus } from "../../services/orderService";
import { useAuth } from "../../context/AuthContext";
import FixedPagination from "../../components/common/FixedPagination";

function TenantOrders() {
  const [orders, setOrders] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const itemsPerPage = 8;
  const { tenantName } = useAuth();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        if (!tenantName) return;
        const response = await getTenantOrders(tenantName);
        setOrders(response.data || []);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [tenantName]);

  const filteredOrders = orders.filter((order) => {
    const productSummary = (order.items || [])
      .map((item) => `${item.productName} ${item.quantity} ${item.subtotal || ""}`)
      .join(" ");

    return [
      order.orderId,
      order.customerName,
      order.totalAmount,
      order.orderDate,
      order.status,
      productSummary,
    ]
      .join(" ")
      .toLowerCase()
      .includes(searchQuery.toLowerCase());
  });

  const orderCount = filteredOrders.length;
  const totalOrderValue = filteredOrders.reduce(
    (sum, order) => sum + Number(order.totalAmount || 0),
    0
  );
  const pageCount = Math.max(1, Math.ceil(orderCount / itemsPerPage));
  const paginatedOrders = filteredOrders.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handleStatusChange = async (orderId, statusValue) => {
    try {
      await updateOrderStatus(tenantName, orderId, statusValue);
      const response = await getTenantOrders(tenantName);
      setOrders(response.data || []);
    } catch (error) {
      console.error(error);
    }
  };

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start gap-3 mb-4">
        <div>
          <h2 className="mb-2">Tenant Orders</h2>  
        </div>
      </div>

      <Row className="g-3 mb-4">
        <Col md={4}>
          <Card className="shadow-sm h-100 border-0">
            <Card.Body>
              <Card.Title>Total Orders</Card.Title>
              <h3>{orderCount}</h3>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="shadow-sm h-100 border-0">
            <Card.Body>
              <Card.Title>Total Revenue</Card.Title>
              <h3>₹{totalOrderValue.toFixed(2)}</h3>
              
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="shadow-sm mb-4">
        <Card.Body>
          <div className="d-flex flex-column flex-lg-row justify-content-between align-items-start align-items-lg-center gap-3">
            <div>
              <h5 className="mb-1">Tenant orders</h5>
            </div>
            <Form.Control
              type="search"
              placeholder="Search orders"
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(1);
              }}
              style={{ maxWidth: 360 }}
            />
          </div>
        </Card.Body>
      </Card>

      <Card className="shadow-sm">
        <Card.Body>
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>Customer</th>
                <th>Products</th>
                <th>Total</th>
                <th>Date</th>
                <th>Status</th>
                <th>Update</th>
              </tr>
            </thead>
            <tbody>
              {paginatedOrders.map((order) => (
                <tr key={order.orderId}>
                  <td>{order.customerName || "-"}</td>
                  <td>
                    <div className="d-flex flex-column gap-1">
                      {(order.items || []).map((item, index) => (
                        <Badge
                          key={`${order.orderId}-${item.productName}-${index}`}
                          bg="light"
                          text="dark"
                          className="w-fit-content align-self-start"
                        >
                          {item.productName} × {item.quantity}
                        </Badge>
                      ))}
                    </div>
                  </td>
                  <td>₹{Number(order.totalAmount).toFixed(2)}</td>
                  <td>{order.orderDate}</td>
                  <td>{order.status}</td>
                  <td>
                    <Form.Select
                      value={order.status || "ORDERED"}
                      onChange={(e) => handleStatusChange(order.orderId, e.target.value)}
                    >
                      <option value="ORDERED">ORDERED</option>
                      <option value="SHIPPED">SHIPPED</option>
                      <option value="DELIVERED">DELIVERED</option>
                    </Form.Select>
                  </td>
                </tr>
              ))}
              {paginatedOrders.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center text-muted py-4">
                    No orders found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>

          <FixedPagination
            currentPage={currentPage}
            totalPages={pageCount}
            onPageChange={setCurrentPage}
          />
        </Card.Body>
      </Card>
    </Container>
  );
}

export default TenantOrders;
