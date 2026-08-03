import { useEffect, useState } from "react";
import {
  Container,
  Spinner,
  Row,
  Col,
  Form,
  Button,
} from "react-bootstrap";
import ProductCard from "../../components/ui/ProductCard";
import FixedPagination from "../../components/common/FixedPagination";
import {
  getProducts,
  searchProducts,
  getProductsByCategory,
} from "../../services/productService";
import { getCategories } from "../../services/categoryService";

import { useParams } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [categories, setCategories] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalProducts, setTotalProducts] = useState(0);
  const [isBackendPagination, setIsBackendPagination] = useState(false);
  const itemsPerPage = 10;
  const { tenantName } = useParams();
  const { hasAnyRole } = useAuth();
  const isAdmin = hasAnyRole(["ROLE_ADMIN"]);

  const loadProducts = async (page = 0) => {
    try {
      const response = await getProducts(tenantName, page, 1000);
      const payload = response.data;
      const pageContent = payload.content || payload;
      const totalCount = pageContent.length;

      setProducts(pageContent);
      setTotalProducts(totalCount);
      setTotalPages(Math.max(Math.ceil(totalCount / itemsPerPage), 1));
      setCurrentPage(page + 1);
      setIsBackendPagination(false);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const response = await getCategories(tenantName);
      setCategories(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handleSearch = async () => {
    setSelectedCategory(null);
    setCurrentPage(1);

    if (!keyword.trim()) {
      loadProducts(0);
      return;
    }

    try {
      const response = await searchProducts(tenantName, keyword);
      const results = response.data || [];
      setProducts(results);
      setTotalPages(Math.max(Math.ceil(results.length / itemsPerPage), 1));
      setTotalProducts(results.length);
      setIsBackendPagination(false);
    } catch (error) {
      console.error(error);
    }
  };

  const filterByCategory = async (categoryId) => {
    setKeyword("");
    setCurrentPage(1);
    setSelectedCategory(categoryId);

    if (categoryId === null) {
      loadProducts(0);
      return;
    }

    try {
      const response = await getProductsByCategory(tenantName, categoryId);
      const results = response.data || [];
      setProducts(results);
      setTotalPages(Math.max(Math.ceil(results.length / itemsPerPage), 1));
      setTotalProducts(results.length);
      setIsBackendPagination(false);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    setLoading(true);
    loadProducts(0);
    loadCategories();
  }, [tenantName]);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

  const pageCount = totalPages;
  const paginatedProducts = products.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  return (
    <Container className="mt-5">
      <div className="mb-4">
        <h2 className="fw-bold">
          {tenantName.charAt(0).toUpperCase() + tenantName.slice(1)} Store
        </h2>
        <p className="text-muted">Browse products available from this store.</p>
      </div>

      <Form
        className="mb-4"
        onSubmit={(e) => {
          e.preventDefault();
          handleSearch();
        }}
      >
        <div className="d-flex gap-2">
          <Form.Control
            type="text"
            placeholder="Search products..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <Button type="submit">Search</Button>
        </div>
      </Form>

      <div className="mb-4">
        <Button
          className="me-2 mb-2"
          variant={selectedCategory === null ? "dark" : "outline-dark"}
          onClick={() => filterByCategory(null)}
        >
          All
        </Button>

        {categories.map((category) => (
          <Button
            key={category.categoryId}
            className="me-2 mb-2"
            variant={selectedCategory === category.categoryId ? "primary" : "outline-primary"}
            onClick={() => filterByCategory(category.categoryId)}
          >
            {category.categoryName}
          </Button>
        ))}
      </div>

      <p className="text-muted mb-4">
        Showing <span className="fw-bold mx-2">{paginatedProducts.length}</span> of <span className="fw-bold mx-2">{totalProducts}</span> Products
      </p>

      {products.length === 0 ? (
        <div className="text-center py-5">
          <h4>No products found</h4>
          <p className="text-muted">Try another search or category.</p>
        </div>
      ) : (
        <>
          <Row>
            {paginatedProducts.map((product) => (
              <Col key={product.productId} lg={4} md={6} className="mb-4">
                <ProductCard product={product} showBuyerActions={!isAdmin} />
              </Col>
            ))}
          </Row>

          <FixedPagination
            currentPage={currentPage}
            totalPages={pageCount}
            onPageChange={(page) => {
              setCurrentPage(page);
            }}
          />
        </>
      )}
    </Container>
  );
}

export default ProductsPage;
