import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  Badge,
  Button,
  Card,
  Col,
  Container,
  Row,
  Spinner,
} from "react-bootstrap";
import { FaHeart, FaShoppingCart } from "react-icons/fa";
import { getProductById } from "../../services/productService";

function ProductDetails() {
  const { productId } = useParams();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProduct();
  }, [productId]);

  const loadProduct = async () => {
    try {
      const response = await getProductById(productId);
      setProduct(response.data);
    } catch (error) {
      console.error(error);
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

  if (!product) {
    return (
      <Container className="mt-5">
        <h3>Product not found.</h3>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          <Card className="shadow border-0">
            <Card.Body className="p-4">

              <Badge bg="primary" className="mb-3">
                {product.category}
              </Badge>

              <h2 className="fw-bold">
                {product.productName}
              </h2>

              <h3 className="text-success my-3">
                ₹ {Number(product.price).toLocaleString("en-IN")}
              </h3>

              <p className="text-muted">
                {product.description}
              </p>

              <hr />

              <div className="mb-4">
                {product.stock > 10 ? (
                  <Badge bg="success">
                    In Stock ({product.stock})
                  </Badge>
                ) : product.stock > 0 ? (
                  <Badge bg="warning" text="dark">
                    Low Stock ({product.stock})
                  </Badge>
                ) : (
                  <Badge bg="danger">
                    Out of Stock
                  </Badge>
                )}
              </div>

              <div className="d-grid gap-2">

                <Button
                  variant="warning"
                  disabled={product.stock === 0}
                >
                  <FaShoppingCart className="me-2" />
                  {product.stock === 0
                    ? "Out of Stock"
                    : "Add to Cart"}
                </Button>

                <Button variant="outline-danger">
                  <FaHeart className="me-2" />
                  Add to Favorites
                </Button>

              </div>

            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default ProductDetails;