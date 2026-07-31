import { useEffect, useState } from "react";
import { Card, Col, Container, Row, Spinner, Table } from "react-bootstrap";
import { useAuth } from "../../context/AuthContext";
import { getProducts } from "../../services/productService";
import { getCategories } from "../../services/categoryService";

function TenantDashboard() {
  const { tenantName, username } = useAuth();

  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    if (!tenantName) return;

    const fetchDashboard = async () => {
      try {
        const [productRes, categoryRes] = await Promise.all([
          getProducts(tenantName),
          getCategories(tenantName),
        ]);

        setProducts(productRes.data.content || []);
        setCategories(categoryRes.data || []);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, [tenantName]);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <h2>Welcome, {username}</h2>
      <p className="text-muted">Tenant: {tenantName}</p>

      <Row className="mb-4">
        <Col md={6}>
          <Card className="shadow-sm">
            <Card.Body>
              <Card.Title>Total Products</Card.Title>
              <h2>{products.length}</h2>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6}>
          <Card className="shadow-sm">
            <Card.Body>
              <Card.Title>Total Categories</Card.Title>
              <h2>{categories.length}</h2>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="shadow-sm">
        <Card.Header>Recent Products</Card.Header>

        <Card.Body>
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
              </tr>
            </thead>

            <tbody>
              {products.length === 0 ? (
                <tr>
                  <td colSpan="4" className="text-center">
                    No products available.
                  </td>
                </tr>
              ) : (
                products.map((product) => (
                  <tr key={product.productId}>
                    <td>{product.productName}</td>
                    <td>{product.category}</td>
                    <td>₹ {product.price}</td>
                    <td>{product.stock}</td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </Card.Body>
      </Card>
    </Container>
  );
}

export default TenantDashboard;