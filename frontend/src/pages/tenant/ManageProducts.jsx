import { useEffect, useState } from "react";
import { Button, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import {
  getProducts,
  getProductsByCategory,
  searchProducts,
  createProduct,
  updateProduct,
  deleteProduct,
} from "../../services/productService";
import { useAuth } from "../../context/AuthContext";
import { getCategories } from "../../services/categoryService";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import FixedPagination from "../../components/common/FixedPagination";

function ManageProducts() {
  const [products, setProducts] = useState([]);
  const [allProducts, setAllProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [productName, setProductName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState(0);
  const [stock, setStock] = useState(0);
  const [categoryId, setCategoryId] = useState("");
  const [categories, setCategories] = useState([]);
  const [editingProductId, setEditingProductId] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterCategoryId, setFilterCategoryId] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const itemsPerPage = 8;
  const { tenantName } = useAuth();


  const loadProducts = async (page = 0, query = "", filterCatId = "") => {
    try {
      if (query.trim()) {
        const response = await searchProducts(tenantName, query.trim());
        const results = response.data || [];
        setAllProducts(results);
        const pageItems = results.slice(page * itemsPerPage, page * itemsPerPage + itemsPerPage);
        setProducts(pageItems);
        setTotalPages(Math.max(Math.ceil(results.length / itemsPerPage), 1));
        setCurrentPage(page + 1);
        return;
      }

      if (filterCatId) {
        const response = await getProductsByCategory(tenantName, Number(filterCatId));
        const filteredItems = response.data || [];
        setAllProducts(filteredItems);
        const pageItems = filteredItems.slice(page * itemsPerPage, page * itemsPerPage + itemsPerPage);
        setProducts(pageItems);
        setTotalPages(Math.max(Math.ceil(filteredItems.length / itemsPerPage), 1));
        setCurrentPage(page + 1);
        return;
      }

      const response = await getProducts(tenantName, page, itemsPerPage);
      const payload = response.data;
      const pageContent = payload.content || [];
      setProducts(pageContent);
      setAllProducts([]);
      setTotalPages(payload.totalPages ?? Math.max(Math.ceil(pageContent.length / itemsPerPage), 1));
      setCurrentPage(page + 1);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load products.");
    }
  };

  useEffect(() => {
    if (!tenantName) return;

    const fetchData = async () => {
      try {
        const [productResponse, categoryResponse] = await Promise.all([
          getProducts(tenantName, 0, itemsPerPage),
          getCategories(tenantName),
        ]);

        const productList = productResponse.data.content || [];
        setProducts(productList);
        setTotalPages(productResponse.data.totalPages || 1);

        const categoryList = categoryResponse.data || [];
        setCategories(categoryList);

        if (categoryList.length > 0) {
          setCategoryId(categoryList[0].categoryId);
        }
      } catch (error) {
        console.error(error);
        toast.error("Failed to load data.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [tenantName]);

  const handleSubmit = async () => {
  try {

    if (!categoryId) {
    toast.error("Please select a category.");
    return;
}

    if (editingProductId) {
  await updateProduct(tenantName, editingProductId, {
    productName,
    description,
    price,
    stock,
    categoryId: Number(categoryId),
  });

  toast.success("Product updated successfully.");
} else {
  await createProduct(tenantName, {
    productName,
    description,
    price,
    stock,
    categoryId: Number(categoryId),
  });

  toast.success("Product created successfully.");
}

    await loadProducts();

    setProductName("");
    setDescription("");
    setPrice(0);
    setStock(0);

    if (categories.length > 0) {
      setCategoryId(categories[0].categoryId);
    }

    setEditingProductId(null);

  } catch (error) {
    console.error(error);
    toast.error("Unable to create product.");
  }
};

const handleEdit = (product) => {
  setEditingProductId(product.productId);

  setProductName(product.productName);
  setDescription(product.description);
  setPrice(product.price);
  setStock(product.stock);

  const selectedCategory = categories.find(
    (category) => category.categoryName === product.category
  );

  if (selectedCategory) {
    setCategoryId(selectedCategory.categoryId.toString());
  }

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

const handleDelete = async (productId) => {
  const confirmed = window.confirm(
    "Are you sure you want to delete this product?"
  );

  if (!confirmed) return;

  try {
    await deleteProduct(tenantName, productId);

    toast.success("Product deleted successfully.");

    await loadProducts(currentPage - 1, searchQuery, filterCategoryId);
  } catch (error) {
    console.error(error);
    toast.error("Unable to delete product.");
  }
};

if (loading) {
  return (
    <Container className="text-center mt-5">
      <Spinner animation="border" />
    </Container>
  );
}

  return (
    <Container className="mt-5">

      <ToastContainer position="top-right" autoClose={3000} />

      <h2>Manage Products</h2>

      <Form className="mb-4">
        <Row className="g-3 mb-3">
          <Col md={4}>
            <Form.Control
              type="search"
              placeholder="Search products..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  setFilterCategoryId("");
                  loadProducts(0, e.target.value, "");
                }
              }}
            />
          </Col>
          <Col md={4}>
            <Form.Select
              value={filterCategoryId}
              onChange={async (e) => {
                const selected = e.target.value;
                setFilterCategoryId(selected);
                setSearchQuery("");
                await loadProducts(0, "", selected);
              }}
            >
              <option value="">Filter by category</option>
              {categories.map((category) => (
                <option key={category.categoryId} value={category.categoryId}>
                  {category.categoryName}
                </option>
              ))}
            </Form.Select>
          </Col>
          <Col md={4} className="d-flex gap-2">
            <Button
              variant="secondary"
              onClick={async () => {
                setSearchQuery("");
                setFilterCategoryId("");
                setAllProducts([]);
                await loadProducts(0);
              }}
            >
              Reset
            </Button>
            <Button
              variant="outline-primary"
              onClick={async () => {
                setFilterCategoryId("");
                await loadProducts(0, searchQuery, "");
              }}
            >
              Search
            </Button>
          </Col>
        </Row>
        <Form.Group className="mb-2">
          <Form.Label>Name</Form.Label>
          <Form.Control
            value={productName}
            onChange={(e) => setProductName(e.target.value)}
          />
        </Form.Group>
        <Form.Group className="mb-2">
          <Form.Label>Description</Form.Label>
          <Form.Control
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </Form.Group>
        <Form.Group className="mb-2">
          <Form.Label>Price</Form.Label>
          <Form.Control
            type="number"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
          />
        </Form.Group>
        <Form.Group className="mb-2">
          <Form.Label>Stock</Form.Label>
          <Form.Control
            type="number"
            value={stock}
            onChange={(e) => setStock(Number(e.target.value))}
          />
        </Form.Group>
        <Form.Group className="mb-4">
          <Form.Label>Category</Form.Label>

          <Form.Select
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
          >
            <option value="">Select Category</option>

            {categories.map((category) => (
              <option
                key={category.categoryId}
                value={category.categoryId}
              >
                {category.categoryName}
              </option>
            ))}
          </Form.Select>
        </Form.Group>
        <Button
          variant="primary"
          onClick={handleSubmit}
          disabled={categories.length === 0}
        >
          {editingProductId ? "Update Product" : "Create Product"}
        </Button>
      </Form>

      <Table striped bordered hover responsive>
        <thead>
          <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Category</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.length === 0 ? (
            <tr>
              <td colSpan="6" className="text-center py-4 text-muted">
                No products found.
              </td>
            </tr>
          ) : (
            products.map((product) => (
              <tr key={product.productId}>
                <td>{product.productName}</td>
                <td>{product.description}</td>
                <td>₹ {product.price}</td>
                <td>{product.stock}</td>
                <td>{product.category}</td>
                <td>
                  <Button
                    variant="warning"
                    size="sm"
                    className="me-2"
                    onClick={() => handleEdit(product)}
                  >
                    Edit
                  </Button>

                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => handleDelete(product.productId)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </Table>

      <FixedPagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={(page) => {
          setCurrentPage(page);
          if (searchQuery.trim() || filterCategoryId) {
            loadProducts(page - 1, searchQuery, filterCategoryId);
          } else {
            loadProducts(page - 1);
          }
        }}
      />
    </Container>
  );
}

export default ManageProducts;
