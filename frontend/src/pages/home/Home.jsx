import { useEffect, useState } from "react";
import {
  Alert,
  Col,
  Container,
  Row,
  Spinner,
} from "react-bootstrap";

import { getTenants } from "../../services/tenantService";
import { getMarketplaceProducts } from "../../services/productService";

import TenantCard from "../../components/ui/TenantCard";
import ProductCard from "../../components/ui/ProductCard";

function Home() {
  const [products, setProducts] = useState([]);
  const [tenants, setTenants] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [productsResponse, tenantsResponse] = await Promise.all([
        getMarketplaceProducts(),
        getTenants(),
      ]);

      setProducts(productsResponse.data.content);
      setTenants(tenantsResponse.data);
    } catch (err) {
      console.error(err);
      setError("Unable to load marketplace.");
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

      {/* Featured Products */}

      <h2 className="mb-4 fw-bold">
        Featured Products
      </h2>

      <Row className="mb-5">
        {products.length === 0 ? (
          <Alert variant="info">
            No products available.
          </Alert>
        ) : (
          products.map((product) => (
            <Col
              key={product.productId}
              lg={4}
              md={6}
              className="mb-4"
            >
              <ProductCard product={product} />
            </Col>
          ))
        )}
      </Row>

      {/* Explore Stores */}

      <h2 className="mb-4 fw-bold">
        Explore Stores
      </h2>

      <Row>
        {tenants.length === 0 ? (
          <Alert variant="info">
            No stores available.
          </Alert>
        ) : (
          tenants.map((tenant) => (
            <Col
              key={tenant.tenantId}
              lg={4}
              md={6}
              className="mb-4"
            >
              <TenantCard tenant={tenant} />
            </Col>
          ))
        )}
      </Row>

    </Container>
  );
}

export default Home;