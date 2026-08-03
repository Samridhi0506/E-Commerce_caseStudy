import { useEffect, useState } from "react";
import {
  Alert,
  Col,
  Container,
  Form,
  Row,
  Spinner,
} from "react-bootstrap";

import { getTenantsPage } from "../../services/tenantService";
import { getMarketplaceProducts } from "../../services/productService";
import { getFavorites } from "../../services/favoriteService";
import TenantCard from "../../components/ui/TenantCard";
import ProductCard from "../../components/ui/ProductCard";
import { useAuth } from "../../context/AuthContext";
import FixedPagination from "../../components/common/FixedPagination";


function Home() {
  const [products, setProducts] = useState([]);
  const [tenants, setTenants] = useState([]);
  const [productSearchQuery, setProductSearchQuery] = useState("");
  const [tenantSearchQuery, setTenantSearchQuery] = useState("");
  const [currentProductPage, setCurrentProductPage] = useState(1);
  const [currentTenantPage, setCurrentTenantPage] = useState(1);
  const [tenantPageInfo, setTenantPageInfo] = useState({
    totalPages: 1,
    totalElements: 0,
  });
  const itemsPerPage = 6;

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const { userId, tenantName, hasAnyRole } = useAuth();
  const isAdmin = hasAnyRole(["ROLE_ADMIN"]);

  const handleFavoriteToggle = (productId) => {
    setProducts((prevProducts) =>
      prevProducts.map((product) =>
        product.productId === productId
          ? {
              ...product,
              isFavorite: !product.isFavorite,
            }
          : product
      )
    );
  };

  useEffect(() => {
    loadData();

    const onFavoritesUpdated = () => {
      loadData();
    };

    window.addEventListener("favoritesUpdated", onFavoritesUpdated);

    return () => {
      window.removeEventListener("favoritesUpdated", onFavoritesUpdated);
    };
  }, [currentTenantPage, tenantSearchQuery, userId, tenantName, isAdmin]);

  const loadData = async () => {
    try {
      const [tenantsResponse, productsResponse] = await Promise.all([
        getTenantsPage(currentTenantPage - 1, itemsPerPage, tenantSearchQuery),
        isAdmin ? Promise.resolve(null) : getMarketplaceProducts(0, 1000),
      ]);

      setTenants(tenantsResponse?.data?.content || []);
      setTenantPageInfo({
        totalPages: tenantsResponse?.data?.totalPages || 1,
        totalElements: tenantsResponse?.data?.totalElements || 0,
      });

      if (!isAdmin && productsResponse) {
        let updatedProducts = productsResponse.data.content;

        if (userId) {
          const favoritesResponse = await getFavorites(
            tenantName || "global",
            userId
          );

          const favoriteIds = new Set(
            (favoritesResponse.data || []).map((favorite) => favorite.productId)
          );

          updatedProducts = updatedProducts.map((product) => ({
            ...product,
            isFavorite: favoriteIds.has(product.productId),
          }));
        }

        setProducts(updatedProducts);
      }
    } catch (err) {
      console.error(err);
      setError("Unable to load marketplace.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center mt-5">
        <Spinner animation="border" />
      </div>
    );
  }

  if (error) {
    return <Alert variant="danger">{error}</Alert>;
  }

  const filteredProducts = products.filter((product) =>
    [product.productName, product.description, product.category, product.tenant]
      .join(" ")
      .toLowerCase()
      .includes(productSearchQuery.toLowerCase())
  );

  const productPageCount = Math.max(
    Math.ceil(filteredProducts.length / itemsPerPage),
    1
  );
  const tenantPageCount = Math.max(tenantPageInfo.totalPages || 1, 1);

  const paginatedProducts = filteredProducts.slice(
    (currentProductPage - 1) * itemsPerPage,
    currentProductPage * itemsPerPage
  );
  const paginatedTenants = tenants;

  return (
    <Container className="mt-4">
      {isAdmin ? (
        <>
          <h2 className="mb-4 fw-bold">Explore Stores</h2>

          <Row className="mb-3">
            <Col md={8}>
              <Form.Control
                type="search"
                placeholder="Search stores"
                value={tenantSearchQuery}
                onChange={(e) => {
                  setTenantSearchQuery(e.target.value);
                  setCurrentTenantPage(1);
                }}
              />
            </Col>
          </Row>
          <Row>
            {paginatedTenants.length === 0 ? (
              <Alert variant="info">No stores available.</Alert>
            ) : (
              paginatedTenants.map((tenant) => (
                <Col
                  key={tenant.tenantId}
                  lg={4}
                  md={6}
                  className="mb-4"
                >
                  <TenantCard tenant={tenant} />
                </Col>
              ))
            )}
          </Row>
          <FixedPagination
            currentPage={currentTenantPage}
            totalPages={tenantPageCount}
            onPageChange={setCurrentTenantPage}
          />
        </>
      ) : (
        <>
          <h2 className="mb-4 fw-bold">
            Featured Products
          </h2>

          <Row className="mb-3">
            <Col md={8}>
              <Form.Control
                type="search"
                placeholder="Search products"
                value={productSearchQuery}
                onChange={(e) => {
                  setProductSearchQuery(e.target.value);
                  setCurrentProductPage(1);
                }}
              />
            </Col>
          </Row>
          <Row className="mb-5">
            {filteredProducts.length === 0 ? (
              <Alert variant="info">
                No products available.
              </Alert>
            ) : (
              paginatedProducts.map((product) => (
                <Col
                  key={product.productId}
                  lg={4}
                  md={6}
                  className="mb-4"
                >
                  <ProductCard
                    product={product}
                    onFavoriteToggle={handleFavoriteToggle}
                  />
                </Col>
              ))
            )}
          </Row>
          <FixedPagination
            currentPage={currentProductPage}
            totalPages={productPageCount}
            onPageChange={setCurrentProductPage}
          />

          <h2 className="mb-4 fw-bold">
            Explore Stores
          </h2>

          <Row className="mb-3">
            <Col md={8}>
              <Form.Control
                type="search"
                placeholder="Search stores"
                value={tenantSearchQuery}
                onChange={(e) => {
                  setTenantSearchQuery(e.target.value);
                  setCurrentTenantPage(1);
                }}
              />
            </Col>
          </Row>

          <Row>
            {paginatedTenants.length === 0 ? (
              <Alert variant="info">
                No stores available.
              </Alert>
            ) : (
              paginatedTenants.map((tenant) => (
                <Col
                  key={tenant.tenantId}
                  lg={4}
                  md={6}
                  className="mb-4"
                >
                  <TenantCard tenant={tenant} />
                </Col>
              ))
            )}
          </Row>
          <FixedPagination
            currentPage={currentTenantPage}
            totalPages={tenantPageCount}
            onPageChange={setCurrentTenantPage}
          />
        </>
      )}
    </Container>
  );
}

export default Home;