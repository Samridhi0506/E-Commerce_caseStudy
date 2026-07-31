import { Badge, Button, Card } from "react-bootstrap";
import { FaHeart, FaShoppingCart } from "react-icons/fa";

function ProductCard({ product }) {
  return (
    <Card
      className="h-100 shadow-sm border-0"
      style={{
        borderRadius: "16px",
      }}
    >
      <Card.Body className="d-flex flex-column">

        <div className="d-flex justify-content-between align-items-start">

          <Card.Title className="fw-bold fs-5 mb-0">
            {product.productName}
          </Card.Title>

          <Badge bg="primary">
            {product.category}
          </Badge>

        </div>

        <Card.Text
          className="text-muted mt-3"
          style={{
            minHeight: "70px",
          }}
        >
          {product.description}
        </Card.Text>

        <div className="mt-auto">

          <h3 className="text-success fw-bold">
            ₹ {Number(product.price).toLocaleString("en-IN")}
          </h3>

          <div className="d-flex justify-content-between mb-3">

            <span className="text-secondary">
              Stock
            </span>

            <strong>{product.stock}</strong>

          </div>

          <div className="d-grid gap-2">

            <Button variant="warning">
              <FaShoppingCart className="me-2" />
              Add to Cart
            </Button>

            <Button variant="outline-danger">
              <FaHeart className="me-2" />
              Add to Favorites
            </Button>

          </div>

        </div>

      </Card.Body>
    </Card>
  );
}

export default ProductCard;