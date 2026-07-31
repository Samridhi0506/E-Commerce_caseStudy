import { useEffect, useState } from "react";
import { Container, ListGroup, Spinner } from "react-bootstrap";
import { getCategories } from "../../services/categoryService";
import { useAuth } from "../../context/AuthContext";

function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const { tenantName } = useAuth();

  useEffect(() => {
  if (!tenantName) return;

  const fetch = async () => {
    try {
      const response = await getCategories(tenantName);
      setCategories(response.data || []);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  fetch();
}, [tenantName]);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <h2>Categories</h2>
      <ListGroup>
        {categories.map((category) => (
          <ListGroup.Item key={category.categoryId}>
            {category.categoryName}
          </ListGroup.Item>
        ))}
      </ListGroup>
    </Container>
  );
}

export default CategoriesPage;
