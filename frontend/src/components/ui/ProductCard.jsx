import { Badge, Button, Card } from "react-bootstrap";
import { FaHeart, FaShoppingCart, FaEye } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { addFavorite } from "../../services/favoriteService";
import { toast } from "react-toastify";

function ProductCard({ product, onFavoriteToggle }) {

  const { userId, tenantName } = useAuth();
  const navigate = useNavigate();

  const isFavorite = product.isFavorite;

  const handleAddFavorite = async () => {
    try {
      await addFavorite(
        tenantName,
        userId,
        product.productId
      );

      onFavoriteToggle(product.productId);

      toast.success(
        isFavorite
          ? "Removed from favorites!"
          : "Added to favorites!"
      );
    } catch (error) {
      toast.error(
        error.response?.data?.message ||
        "Unable to update favorite."
      );
    }
  };

  return (
    <Card className="h-100 shadow-sm border-0" style={{ borderRadius: "16px" }}>
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
          style={{ minHeight: "70px" }}
        >
          {product.description}
        </Card.Text>

        <div className="mt-auto">

          <h3 className="text-success fw-bold">
            ₹ {Number(product.price).toLocaleString("en-IN")}
          </h3>

          <div className="mb-3">
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
              variant="primary"
              onClick={() =>
                navigate(`/product/${product.productId}`)
              }
            >
              <FaEye className="me-2" />
              View Details
            </Button>

            <Button
              variant="warning"
              disabled={product.stock === 0}
            >
              <FaShoppingCart className="me-2" />
              {product.stock === 0
                ? "Out of Stock"
                : "Add to Cart"}
            </Button>

            <Button
              variant={isFavorite ? "danger" : "outline-danger"}
              onClick={handleAddFavorite}
            >
              <FaHeart className="me-2" />
              {isFavorite ? "Favorited" : "Add to Favorites"}
            </Button>

          </div>

        </div>

      </Card.Body>
    </Card>
  );
}

export default ProductCard;