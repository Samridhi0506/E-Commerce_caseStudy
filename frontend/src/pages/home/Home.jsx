import { Button, Card, Col, Container, Row } from "react-bootstrap";

function Home() {
  return (
    <Container>

      <Card className="text-center shadow-lg p-5 border-0">

        <h1>Welcome to E-Commerce</h1>

        <p className="mt-3">
          Buy and manage products across multiple tenants.
        </p>

        <Button size="lg">
          Explore Products
        </Button>

      </Card>

      <Row className="mt-5">

        <Col md={4}>
          <Card className="shadow-sm">
            <Card.Body>
              <h4>Products</h4>
              <p>Browse available products.</p>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4}>
          <Card className="shadow-sm">
            <Card.Body>
              <h4>Orders</h4>
              <p>Track your orders.</p>
            </Card.Body>
          </Card>
        </Col>

        <Col md={4}>
          <Card className="shadow-sm">
            <Card.Body>
              <h4>Favorites</h4>
              <p>Save your favourite products.</p>
            </Card.Body>
          </Card>
        </Col>

      </Row>

    </Container>
  );
}

export default Home;