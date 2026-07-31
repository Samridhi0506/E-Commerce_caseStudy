import { useEffect, useState } from "react";
import { Button, Container, Form, Spinner, Table } from "react-bootstrap";
import {
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct,
} from "../../services/productService";
import { useAuth } from "../../context/AuthContext";
import { getCategories } from "../../services/categoryService";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function ManageProducts() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [productName, setProductName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState(0);
  const [stock, setStock] = useState(0);
  const [categoryId, setCategoryId] = useState("");
  const [categories, setCategories] = useState([]);
  const [editingProductId, setEditingProductId] = useState(null);
  const { tenantName } = useAuth();


  const loadProducts = async () => {
  const response = await getProducts(tenantName);
  setProducts(response.data.content || []);
};

  useEffect(() => {
  if (!tenantName) return;

  const fetchData = async () => {
    try {
      const [productResponse, categoryResponse] = await Promise.all([
  getProducts(tenantName),
  getCategories(tenantName),
]);

const productList = productResponse.data.content || [];
setProducts(productList);

const categoryList = categoryResponse.data || [];
setCategories(categoryList);

// Automatically select the first category
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

  // Find the category from dropdown list
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

    await loadProducts();
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

      <Table striped bordered hover>
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
          {products.map((product) => (
            <tr key={product.productId}>
              <td>{product.productName}</td>
              <td>{product.description}</td>
              <td>{product.price}</td>
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
          ))}
        </tbody>
      </Table>
    </Container>
  );
}

export default ManageProducts;
