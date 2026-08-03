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
import { toast } from "react-toastify";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import { addFavorite, getFavorites } from "../../services/favoriteService";
import { getProductById } from "../../services/productService";
import getProductImageByCategory from "../../utils/productImageUtils";

function ProductDetails() {
  const { productId } = useParams();
  const { userId, tenantName, hasAnyRole } = useAuth();
  const { addToCart, getCartQuantity } = useCart();
  const isAdmin = hasAnyRole(["ROLE_ADMIN"]);

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isFavorite, setIsFavorite] = useState(false);

  useEffect(() => {
    loadProduct();
  }, [productId, userId, tenantName]);

  const loadProduct = async () => {
    try {
      const response = await getProductById(productId);
      setProduct(response.data);

      if (userId) {
        const favoritesResponse = await getFavorites(tenantName || "global", userId);
        const favoriteExists = (favoritesResponse.data || []).some(
          (favorite) => favorite.productId === Number(productId)
        );
        setIsFavorite(favoriteExists);
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async () => {
    try {
      await addToCart(product.productId);
      toast.success("Product added to cart!");
    } catch (error) {
      toast.error(
        error.response?.data?.message || "Unable to add product to cart."
      );
    }
  };

  const handleFavoriteToggle = async () => {
    try {
      if (!userId) {
        toast.error("Please log in to manage favorites.");
        return;
      }

      const favoriteTenant = product?.tenant || tenantName || "global";

      await addFavorite(favoriteTenant, userId, product.productId);
      const newFavoriteState = !isFavorite;
      setIsFavorite(newFavoriteState);
      toast.success(
        newFavoriteState
          ? "Added to favorites!"
          : "Removed from favorites!"
      );

      // notify other pages (Home) to refresh favorite state
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
    } catch (error) {
      toast.error(
        error.response?.data?.message || "Unable to update favorite."
      );
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
            <Card.Img
              variant="top"
              src={product.imageUrl || getProductImageByCategory(product.category)}
              alt={product.productName}
              style={{ height: 420, objectFit: "cover" }}
            />
            <Card.Body className="p-4">

              <div className="d-flex flex-column flex-sm-row justify-content-between align-items-start gap-3 mb-3">
                <Badge bg="primary" className="me-auto">
                  {product.category}
                </Badge>

                {isAdmin && (
                  <div className="text-end fs-5 fw-semibold">
                    {product.tenant || "Global"}
                  </div>
                )}
              </div>

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
                {(() => {
                  if (product.stock > 10) {
                    return <Badge bg="success">In Stock ({product.stock})</Badge>;
                  }
                  if (product.stock > 0) {
                    return <Badge bg="warning" text="dark">Low Stock ({product.stock})</Badge>;
                  }
                  return <Badge bg="danger">Out of Stock</Badge>;
                })()}
              </div>

              {!isAdmin && (
                <div className="d-grid gap-2">

                  <Button
                    variant="warning"
                    disabled={product.stock === 0}
                    onClick={handleAddToCart}
                  >
                    <FaShoppingCart className="me-2" />
                    {(() => {
                      if (product.stock === 0) {
                        return "Out of Stock";
                      }
                      const currentQuantity = getCartQuantity(product.productId);
                      return currentQuantity > 0
                        ? `Add More (${currentQuantity})`
                        : "Add to Cart";
                    })()}
                  </Button>

                  <Button
                    variant={isFavorite ? "danger" : "outline-danger"}
                    onClick={handleFavoriteToggle}
                  >
                    <FaHeart className="me-2" />
                    {isFavorite ? "Favorited" : "Add to Favorites"}
                  </Button>

                </div>
              )}

            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default ProductDetails;