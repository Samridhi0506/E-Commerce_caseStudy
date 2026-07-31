import { Badge, Button, Card } from "react-bootstrap";
import { FaHeart, FaShoppingCart, FaEye, FaMinus, FaPlus } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import { addFavorite } from "../../services/favoriteService";
import { toast } from "react-toastify";

function ProductCard({ product, onFavoriteToggle }) {
  const { userId, tenantName } = useAuth();

  const {
    addToCart,
    increaseQuantity,
    decreaseQuantity,
    getCartQuantity,
  } = useCart();

  const navigate = useNavigate();

  const isFavorite = product.isFavorite;
  const quantity = getCartQuantity(product.productId);

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

  const handleAddToCart = async () => {
    try {
      await addToCart(product.productId);
      toast.success("Product added to cart!");
    } catch (error) {
      toast.error(
        error.response?.data?.message ||
          "Unable to add product to cart."
      );
    }
  };

  return (
    <Card
      className="h-100 shadow-sm border-0"
      style={{ borderRadius: "16px" }}
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

            {product.stock === 0 ? (
              <Button variant="secondary" disabled>
                Out of Stock
              </Button>
            ) : quantity === 0 ? (
              <Button
                variant="warning"
                onClick={handleAddToCart}
              >
                <FaShoppingCart className="me-2" />
                Add to Cart
              </Button>
            ) : (
              <div
                className="d-flex justify-content-between align-items-center border rounded px-2 py-2"
              >
                <Button
                  variant="outline-danger"
                  size="sm"
                  onClick={() =>
                    decreaseQuantity(product.productId)
                  }
                >
                  <FaMinus />
                </Button>

                <strong>{quantity}</strong>

                <Button
                  variant="outline-success"
                  size="sm"
                  onClick={() =>
                    increaseQuantity(product.productId)
                  }
                >
                  <FaPlus />
                </Button>
              </div>
            )}

            <Button
              variant={
                isFavorite
                  ? "danger"
                  : "outline-danger"
              }
              onClick={handleAddFavorite}
            >
              <FaHeart className="me-2" />
              {isFavorite
                ? "Favorited"
                : "Add to Favorites"}
            </Button>
          </div>
        </div>
      </Card.Body>
    </Card>
  );
}

export default ProductCard;