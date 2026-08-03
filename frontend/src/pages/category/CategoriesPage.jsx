import { useEffect, useState } from "react";
import { Badge, Card, Col, Container, Form, Row, Spinner } from "react-bootstrap";
import { getCategories } from "../../services/categoryService";
import { useAuth } from "../../context/AuthContext";
import FixedPagination from "../../components/common/FixedPagination";

function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;
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

  const filteredCategories = categories.filter((category) =>
    category.categoryName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const pageCount = Math.ceil(filteredCategories.length / itemsPerPage);
  const paginatedCategories = filteredCategories.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );
  const totalCategories = categories.length;
  const visibleCount = paginatedCategories.length;

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start gap-3 mb-4">
        <div>
          <h2 className="mb-2">Categories</h2>
          <p className="text-muted mb-0">
            Browse and manage the categories available for your tenant. Use search to find the category you want quickly.
          </p>
        </div>
        <Form.Control
          style={{ maxWidth: 320 }}
          type="search"
          placeholder="Search categories"
          value={searchQuery}
          onChange={(e) => {
            setSearchQuery(e.target.value);
            setCurrentPage(1);
          }}
        />
      </div>

      <Row className="g-3 mb-4">
        <Col md={12}>
          <Card className="shadow-sm h-100 border-0">
            <Card.Body>
              <Card.Title className="mb-2">Category overview</Card.Title>
              <Card.Text className="text-muted mb-0">
                Browse categories for your tenant. Enter a search term to filter the list.
              </Card.Text>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row xs={1} md={2} xl={3} className="g-3">
        {paginatedCategories.length > 0 ? (
          paginatedCategories.map((category) => (
            <Col key={category.categoryId}>
              <Card className="h-100 shadow-sm border-0">
                <Card.Body>
                  <div className="d-flex align-items-center justify-content-between mb-3">
                    <div>
                      <Card.Title className="mb-1">{category.categoryName}</Card.Title>
                      <Card.Text className="text-muted mb-0">Category ID: {category.categoryId}</Card.Text>
                    </div>
                    <div className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center" style={{ width: 42, height: 42 }}>
                      {category.categoryName.charAt(0).toUpperCase()}
                    </div>
                  </div>
                  <Badge bg="info">Category</Badge>
                </Card.Body>
              </Card>
            </Col>
          ))
        ) : (
          <Col>
            <Card className="shadow-sm border-0 text-center py-5">
              <Card.Body>
                <Card.Title>No categories found</Card.Title>
                <Card.Text className="text-muted">
                  There are no categories matching your search. Clear the search box or add a new category from the admin panel.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        )}
      </Row>

      <div className="mt-4">
        <FixedPagination
          currentPage={currentPage}
          totalPages={pageCount}
          onPageChange={setCurrentPage}
        />
      </div>
    </Container>
  );
}

export default CategoriesPage;
