import { useEffect, useState } from "react";
import { Alert, Badge, Col, Container, Row, Spinner } from "react-bootstrap";
import { getFavorites } from "../../services/favoriteService";
import { useAuth } from "../../context/AuthContext";
import ProductCard from "../../components/ui/ProductCard";
import FixedPagination from "../../components/common/FixedPagination";

function FavoritesPage() {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const { tenantName, userId } = useAuth();

  useEffect(() => {
    const fetch = async () => {
      try {
        const activeTenant = tenantName || "global";
        const response = await getFavorites(activeTenant, userId);
        setFavorites(response.data || []);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetch();
  }, [tenantName, userId]);

  const handleFavoriteToggle = (productId) => {
    setFavorites((currentFavorites) =>
      currentFavorites.filter((favorite) => favorite.productId !== productId)
    );
  };

  const totalPages = Math.max(Math.ceil(favorites.length / itemsPerPage), 1);
  const paginatedFavorites = favorites.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
          <h2 className="fw-bold mb-1">Favorites</h2>
          <p className="text-muted mb-0">Your saved products across stores</p>
        </div>
        <Badge bg="primary" pill>
          {favorites.length} {favorites.length === 1 ? "item" : "items"}
        </Badge>
      </div>

      {favorites.length === 0 ? (
        <Alert variant="info" className="shadow-sm rounded-4">
          No favorite products yet. Browse the marketplace and save a few items.
        </Alert>
      ) : (
        <>
          <Row className="g-4">
            {paginatedFavorites.map((favorite) => (
              <Col key={favorite.favoriteId} lg={4} md={6} sm={12}>
                <ProductCard
                  product={{
                    ...favorite,
                    isFavorite: true,
                  }}
                  onFavoriteToggle={handleFavoriteToggle}
                  showBuyerActions={true}
                />
              </Col>
            ))}
          </Row>

          <FixedPagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={setCurrentPage}
          />
        </>
      )}
    </Container>
  );
}

export default FavoritesPage;
