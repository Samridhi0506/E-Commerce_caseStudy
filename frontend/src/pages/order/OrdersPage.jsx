import { useEffect, useState } from "react";
import { Container, Table, Spinner } from "react-bootstrap";
import { getOrders } from "../../services/orderService";
import { useAuth } from "../../context/AuthContext";

function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const { tenantName } = useAuth();
  const userId = 1;

  useEffect(() => {
    const fetch = async () => {
      try {
        const response = await getOrders(tenantName, userId);
        setOrders(response.data || []);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetch();
  }, []);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <h2>Orders</h2>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Total</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.orderId}>
              <td>{order.orderId}</td>
              <td>{order.totalAmount}</td>
              <td>{order.orderDate}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
}

export default OrdersPage;
