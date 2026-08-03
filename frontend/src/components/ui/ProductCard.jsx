import { Badge, Button, Card } from "react-bootstrap";
import { FaHeart, FaShoppingCart, FaEye, FaMinus, FaPlus } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import { addFavorite } from "../../services/favoriteService";
import { toast } from "react-toastify";
import getProductImageByCategory from "../../utils/productImageUtils";

const FALLBACK_IMAGE = `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(`
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 500">
    <defs>
      <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
        <stop offset="0%" stop-color="#5b21b6" />
        <stop offset="100%" stop-color="#2563eb" />
      </linearGradient>
    </defs>
    <rect width="800" height="500" fill="url(#g)" />
    <rect x="40" y="40" width="720" height="420" rx="24" fill="rgba(255,255,255,0.12)" stroke="rgba(255,255,255,0.3)" />
    <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="Arial, sans-serif" font-size="34" fill="white">Product Image</text>
  </svg>
`)}`;

const getProductPlaceholderImage = (category) => {
  const normalized = String(category || "").toLowerCase();
  const images = {
    electronics:
      "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=60",
    fashion:
      "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=900&q=60",
    home:
      "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=60",
    groceries:
      "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=900&q=60",
    sports:
      "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=900&q=60",
    furniture:
      "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=60",
  };

  return images[normalized] ||
    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=60";
};

function ProductCard({ product, onFavoriteToggle, showBuyerActions = true }) {
  const { userId, tenantName } = useAuth();

  const {
    addToCart,
    increaseQuantity,
    decreaseQuantity,
    getCartQuantity,
  } = useCart();

  const favoriteTenant = product.tenant || tenantName || "global";

  const navigate = useNavigate();

  const isFavorite = product.isFavorite;
  const quantity = getCartQuantity(product.productId);
  const resolvedImage = product.imageUrl || getProductImageByCategory(product.category) || FALLBACK_IMAGE;

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

  const stockBadge = (() => {
    if (product.stock > 10) {
      return <Badge bg="success">In Stock ({product.stock})</Badge>;
    }

    if (product.stock > 0) {
      return <Badge bg="warning" text="dark">Low Stock ({product.stock})</Badge>;
    }

    return <Badge bg="danger">Out of Stock</Badge>;
  })();

  const cartAction = (() => {
    if (product.stock === 0) {
      return <Button variant="secondary" disabled>Out of Stock</Button>;
    }

    if (quantity === 0) {
      return (
        <Button variant="warning" onClick={handleAddToCart}>
          <FaShoppingCart className="me-2" />
          Add to Cart
        </Button>
      );
    }

    return (
      <div className="d-flex justify-content-between align-items-center border rounded px-2 py-2">
        <Button variant="outline-danger" size="sm" onClick={() => decreaseQuantity(product.productId)}>
          <FaMinus />
        </Button>

        <strong>{quantity}</strong>

        <Button variant="outline-success" size="sm" onClick={() => increaseQuantity(product.productId)}>
          <FaPlus />
        </Button>
      </div>
    );
  })();

  const handleAddFavorite = async () => {
    try {
      await addFavorite(
        favoriteTenant,
        userId,
        product.productId
      );

      const newFavoriteState = !isFavorite;

      onFavoriteToggle(product.productId);

      // notify other pages to refresh favorite state
      try {
        window.dispatchEvent(
          new CustomEvent("favoritesUpdated", {
            detail: {
              productId: product.productId,
              isFavorite: newFavoriteState,
            },
          })
        );
      } catch (err) {
        console.warn("favoritesUpdated event dispatch failed", err);
      }

      toast.success(
        newFavoriteState
          ? "Added to favorites!"
          : "Removed from favorites!"
      );
    } catch (error) {
      toast.error(
        error.response?.data?.message ||
          "Unable to update favorite."
      );
    }
  };

  return (
    <Card
      className="h-100 shadow-sm border-0"
      style={{ borderRadius: "16px" }}
    >
      <div style={{ position: "relative", minHeight: 180 }}>
        <Card.Img
          variant="top"
          src={resolvedImage}
          alt={product.productName}
          onError={(event) => {
            event.currentTarget.onerror = null;
            event.currentTarget.src = FALLBACK_IMAGE;
          }}
          style={{ height: 180, objectFit: "cover", borderTopLeftRadius: 16, borderTopRightRadius: 16 }}
        />
        <Badge
          bg="dark"
          style={{ position: "absolute", top: 12, right: 12, opacity: 0.92 }}
        >
          {product.category || "General"}
        </Badge>
      </div>

      <Card.Body className="d-flex flex-column">
        <div className="d-flex justify-content-between align-items-start">
          <Card.Title className="fw-bold fs-5 mb-0">
            {product.productName}
          </Card.Title>
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
            {stockBadge}
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

            {showBuyerActions && (
              <>
                {cartAction}

                <Button
                  variant={isFavorite ? "danger" : "outline-danger"}
                  onClick={handleAddFavorite}
                >
                  <FaHeart className="me-2" />
                  {isFavorite ? "Favorited" : "Add to Favorites"}
                </Button>
              </>
            )}
          </div>
        </div>
      </Card.Body>
    </Card>
  );
}

export default ProductCard;