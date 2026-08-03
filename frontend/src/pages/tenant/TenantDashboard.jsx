import { useEffect, useState } from "react";
import { Button, Card, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import { useAuth } from "../../context/AuthContext";
import { getProducts, getProductsByCategory, searchProducts } from "../../services/productService";
import FixedPagination from "../../components/common/FixedPagination";

function TenantDashboard() {
  const { tenantName, username } = useAuth();

  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState([]);
  const [tenantProducts, setTenantProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterCategory, setFilterCategory] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalProducts, setTotalProducts] = useState(0);
  const itemsPerPage = 8;

  const loadDashboardProducts = async (page = 0, query = "", category = "") => {
    try {
      if (query.trim()) {
        const response = await searchProducts(tenantName, query.trim());
        const results = response.data || [];
        setFilteredProducts(results);
        const pageItems = results.slice(page * itemsPerPage, page * itemsPerPage + itemsPerPage);
        setProducts(pageItems);
        setTotalProducts(results.length);
        setTotalPages(Math.max(Math.ceil(results.length / itemsPerPage), 1));
        setCurrentPage(page + 1);
        return;
      }

      if (category) {
        const filteredItems = tenantProducts.filter((product) => product.category === category);
        setFilteredProducts(filteredItems);
        const pageItems = filteredItems.slice(page * itemsPerPage, page * itemsPerPage + itemsPerPage);
        setProducts(pageItems);
        setTotalProducts(filteredItems.length);
        setTotalPages(Math.max(Math.ceil(filteredItems.length / itemsPerPage), 1));
        setCurrentPage(page + 1);
        return;
      }

      const pagedItems = filteredProducts.length > 0 ? filteredProducts : tenantProducts;
      const pageItems = pagedItems.slice(page * itemsPerPage, page * itemsPerPage + itemsPerPage);
      setProducts(pageItems);
      setTotalProducts(pagedItems.length);
      setTotalPages(Math.max(Math.ceil(pagedItems.length / itemsPerPage), 1));
      setCurrentPage(page + 1);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    if (!tenantName) return;

    const fetchDashboard = async () => {
      try {
        const response = await getProducts(tenantName, 0, 1000);
        const productList = response.data.content || [];

        setTenantProducts(productList);
        setFilteredProducts([]);
        setProducts(productList.slice(0, itemsPerPage));
        setTotalProducts(productList.length);
        setTotalPages(Math.max(Math.ceil(productList.length / itemsPerPage), 1));
        setCurrentPage(1);

        const usedCategories = Array.from(
          new Set(productList.map((product) => product.category).filter(Boolean))
        );
        setCategories(usedCategories);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, [tenantName]);

  if (loading) {
    return (
      <Container className="text-center mt-5">
        <Spinner animation="border" />
      </Container>
    );
  }

const totalStock = tenantProducts.reduce((sum, product) => sum + (product.stock || 0), 0);
  const lowStockCount = tenantProducts.filter((product) => product.stock <= 5).length;

  const getStockBadgeClass = (stock) => {
    if (stock === 0) return "bg-danger";
    if (stock <= 5) return "bg-warning text-dark";
    return "bg-success";
  };

  return (
    <Container className="mt-4">
      <Row className="align-items-center mb-4">
        <Col>
          <h2 className="mb-1">Welcome back, {username}</h2>
          <p className="text-muted mb-0">
            Manage product listings and categories for <span className="fw-semibold">{tenantName}</span>.
          </p>
        </Col>
        <Col md="auto">
          <div className="bg-primary text-white rounded-3 px-4 py-3 text-center shadow-sm">
            <div className="fs-5 fw-semibold mt-1">{tenantName}</div>
          </div>
        </Col>
      </Row>

      <Row className="g-3 mb-4">
        <Col md={3}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Body>
              <Card.Title className="text-uppercase text-muted fs-7">Products</Card.Title>
              <div className="display-6 fw-bold">{totalProducts}</div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Body>
              <Card.Title className="text-uppercase text-muted fs-7">Categories</Card.Title>
              <div className="display-6 fw-bold">{categories.length}</div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Body>
              <Card.Title className="text-uppercase text-muted fs-7">Total stock</Card.Title>
              <div className="display-6 fw-bold">{totalStock}</div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Body>
              <Card.Title className="text-uppercase text-muted fs-7">Low stock</Card.Title>
              <div className="display-6 fw-bold">{lowStockCount}</div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="mb-4">
        <Col lg={6}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Header className="bg-white border-0 py-3">
              <div className="d-flex align-items-center justify-content-between">
                <div>
                  <Card.Title className="mb-0">Category overview</Card.Title>
                </div>
              </div>
            </Card.Header>
            <Card.Body>
              {categories.length === 0 ? (
                <p className="text-muted mb-0">No categories registered by this tenant.</p>
              ) : (
                <div className="d-flex flex-wrap gap-2">
                  {categories.map((category) => (
                    <span key={category} className="badge bg-secondary py-2 px-3">
                      {category}
                    </span>
                  ))}
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="g-3 mb-3 align-items-center">
        <Col md={4}>
          <Form.Control
            type="search"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                setFilterCategory("");
                loadDashboardProducts(0, e.target.value, "");
              }
            }}
          />
        </Col>
        <Col md={4}>
          <Form.Select
            value={filterCategory}
            onChange={(e) => {
              const selected = e.target.value;
              setFilterCategory(selected);
              setSearchQuery("");
              loadDashboardProducts(0, "", selected);
            }}
          >
            <option value="">Filter by category</option>
            {categories.map((category) => (
              <option key={category} value={category}>
                {category}
              </option>
            ))}
          </Form.Select>
        </Col>
        <Col md={4} className="d-flex gap-2">
          <Button
            variant="secondary"
            onClick={() => {
              setSearchQuery("");
              setFilterCategory("");
              setFilteredProducts([]);
              loadDashboardProducts(0);
            }}
          >
            Reset
          </Button>
          <Button
            variant="outline-primary"
            onClick={() => {
              setFilterCategory("");
              loadDashboardProducts(0, searchQuery, "");
            }}
          >
            Search
          </Button>
        </Col>
      </Row>

      <Card className="shadow-sm border-0">
        <Card.Header className="bg-white border-0 py-3">
          <div className="d-flex align-items-center justify-content-between">
            <div>
              <Card.Title className="mb-0">Recent products</Card.Title>
            </div>
          </div>
        </Card.Header>

        <Card.Body>
          <Table striped hover responsive className="mb-0">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
              </tr>
            </thead>

            <tbody>
              {products.length === 0 ? (
                <tr>
                  <td colSpan="4" className="text-center py-4 text-muted">
                    No products available yet.
                  </td>
                </tr>
              ) : (
                products.map((product) => (
                  <tr key={product.productId}>
                    <td>{product.productName}</td>
                    <td>{product.category}</td>
                    <td>₹ {Number(product.price).toLocaleString("en-IN")}</td>
                    <td>
                      <span className={`badge ${getStockBadgeClass(product.stock)}`}>
                        {product.stock}
                      </span>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </Table>
        </Card.Body>
      </Card>

      <FixedPagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={(page) => {
          loadDashboardProducts(page - 1, searchQuery, filterCategory);
        }}
      />
    </Container>
  );
}

export default TenantDashboard;