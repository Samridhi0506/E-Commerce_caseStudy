import { useEffect, useState } from "react";
import { Button, Container, Form, Spinner, Table } from "react-bootstrap";
import { getProducts, createProduct } from "../../services/productService";
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
  const { tenantName } = useAuth();

  useEffect(() => {
  if (!tenantName) return;

  const fetchData = async () => {
    try {
      const [productResponse, categoryResponse] = await Promise.all([
  getProducts(tenantName),
  getCategories(tenantName),
]);

setProducts(productResponse.data.content || []);

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

  const handleCreate = async () => {
  try {

    if (!categoryId) {
    toast.error("Please select a category.");
    return;
}

    await createProduct(tenantName, {
      productName,
      description,
      price,
      stock,
      categoryId: Number(categoryId),
    });

    toast.success("Product created successfully.");

    const response = await getProducts(tenantName);
    setProducts(response.data.content || []);

    setProductName("");
    setDescription("");
    setPrice(0);
    setStock(0);

    if (categories.length > 0) {
      setCategoryId(categories[0].categoryId);
    }

  } catch (error) {
    console.error(error);
    toast.error("Unable to create product.");
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
        <Button onClick={handleCreate}>Create Product</Button>
      </Form>

      <Table striped bordered hover>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Category</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => (
            <tr key={product.productId}>
              <td>{product.productId}</td>
              <td>{product.productName}</td>
              <td>{product.description}</td>
              <td>{product.price}</td>
              <td>{product.stock}</td>
              <td>{product.category}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
}

export default ManageProducts;
