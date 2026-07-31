import { useEffect, useState } from "react";
import { Container, Table, Spinner } from "react-bootstrap";
import { getFavorites } from "../../services/favoriteService";
import { useAuth } from "../../context/AuthContext";

function FavoritesPage() {
  const [favorites, setFavorites] = useState([]);
  const [loading, setLoading] = useState(true);
  const { tenantName } = useAuth();
  const userId = 1;

  useEffect(() => {
    const fetch = async () => {
      try {
        const response = await getFavorites(tenantName, userId);
        setFavorites(response.data || []);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetch();
  }, []);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <h2>Favorites</h2>
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Product ID</th>
            <th>Product Name</th>
          </tr>
        </thead>
        <tbody>
          {favorites.map((favorite) => (
            <tr key={favorite.favoriteId}>
              <td>{favorite.favoriteId}</td>
              <td>{favorite.productId}</td>
              <td>{favorite.productName}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
}

export default FavoritesPage;
