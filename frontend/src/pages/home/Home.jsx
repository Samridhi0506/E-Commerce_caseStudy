import { useEffect, useState } from "react";
import { Alert, Col, Container, Row, Spinner } from "react-bootstrap";
import { getTenants } from "../../services/tenantService";
import TenantCard from "../../components/ui/TenantCard";

function Home() {

  const [tenants, setTenants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadTenants();
  }, []);

  const loadTenants = async () => {

    try {

      const response = await getTenants();

      setTenants(response.data);

    } catch (err) {

      setError("Unable to load stores.");

    } finally {

      setLoading(false);

    }

  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" />
      </div>
    );
  }

  if (error) {
    return <Alert variant="danger">{error}</Alert>;
  }

  return (
    <Container className="mt-4">

      <h2 className="text-center mb-4">
        Explore Stores
      </h2>

      <Row>

        {tenants.map((tenant) => (

          <Col
            key={tenant.tenantId}
            lg={4}
            md={6}
            className="mb-4"
          >
            <TenantCard tenant={tenant} />
          </Col>

        ))}

      </Row>

    </Container>
  );
}

export default Home;