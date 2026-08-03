import { Card, Col, Container, Row } from "react-bootstrap";
import { Link } from "react-router-dom";

function AdminHome() {
  return (
    <Container className="mt-4">
      <Row className="align-items-center mb-4">
        <Col lg={8}>
          <div className="p-4 rounded-3 bg-white shadow-sm">
            <h2 className="mb-2">Admin Dashboard</h2>
          </div>
        </Col>
      </Row>

      <Row className="g-4">
        <Col md={6} lg={4}>
          <Card as={Link} to="tenants" className="text-decoration-none h-100 shadow-sm border-0" style={{ color: "inherit" }}>
            <Card.Img
              variant="top"
              src="https://images.unsplash.com/photo-1504384308090-c894fdcc538d?auto=format&fit=crop&w=900&q=60"
              alt="Tenant overview"
              className="rounded-top"
              style={{ height: 160, objectFit: "cover" }}
            />
            <Card.Body className="d-flex flex-column justify-content-between h-100">
              <div>
                <Card.Title>Tenant Management</Card.Title>
                <Card.Text className="text-muted">
                  Configure tenants and manage tenant admin access.
                </Card.Text>
              </div>
              <div className="mt-3 d-flex justify-content-between align-items-center">
                <span className="text-primary fw-semibold">Go to tenants →</span>
                <span aria-label="building emoji" role="img">🏢</span>
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6} lg={4}>
          <Card as={Link} to="categories" className="text-decoration-none h-100 shadow-sm border-0" style={{ color: "inherit" }}>
            <Card.Img
              variant="top"
              src="https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=900&q=60"
              alt="Category overview"
              className="rounded-top"
              style={{ height: 160, objectFit: "cover" }}
            />
            <Card.Body className="d-flex flex-column justify-content-between h-100">
              <div>
                <Card.Title>Category Management</Card.Title>
                <Card.Text className="text-muted">
                  Update category structure and keep catalogs organized.
                </Card.Text>
              </div>
              <div className="mt-3 d-flex justify-content-between align-items-center">
                <span className="text-primary fw-semibold">Go to categories →</span>
                <span aria-label="tag emoji" role="img">🏷️</span>
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6} lg={4}>
          <Card as={Link} to="users" className="text-decoration-none h-100 shadow-sm border-0" style={{ color: "inherit" }}>
            <Card.Img
              variant="top"
              src="https://images.unsplash.com/photo-1542744173-8e7e53415bb0?auto=format&fit=crop&w=900&q=60"
              alt="User insights"
              className="rounded-top"
              style={{ height: 160, objectFit: "cover" }}
            />
            <Card.Body className="d-flex flex-column justify-content-between h-100">
              <div>
                <Card.Title>User & Order Insights</Card.Title>
                <Card.Text className="text-muted">
                  Review users and view order activity in one place.
                </Card.Text>
              </div>
              <div className="mt-3 d-flex justify-content-between align-items-center">
                <span className="text-primary fw-semibold">Go to insights →</span>
                <span aria-label="chart emoji" role="img">📊</span>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default AdminHome;
