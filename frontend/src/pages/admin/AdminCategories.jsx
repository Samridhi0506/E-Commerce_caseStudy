import { useEffect, useState } from "react";
import { Badge, Button, Card, Col, Container, Form, Row, Spinner, Table } from "react-bootstrap";
import { toast } from "react-toastify";
import FixedPagination from "../../components/common/FixedPagination";
import { createCategory, deleteCategory, getCategories, updateCategory } from "../../services/categoryService";

function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [categoryForm, setCategoryForm] = useState({ categoryName: "" });
  const [editingCategoryId, setEditingCategoryId] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;
  const [loading, setLoading] = useState(true);

  const loadCategories = async () => {
    try {
      const response = await getCategories();
      setCategories(response.data || []);
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to load categories.");
    }
  };

  useEffect(() => {
    const init = async () => {
      await loadCategories();
      setLoading(false);
    };
    init();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      if (editingCategoryId) {
        await updateCategory(editingCategoryId, categoryForm);
        toast.success("Category updated successfully.");
      } else {
        await createCategory(categoryForm);
        toast.success("Category created successfully.");
      }

      setCategoryForm({ categoryName: "" });
      setEditingCategoryId(null);
      await loadCategories();
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to save category.");
    }
  };

  const handleEdit = (category) => {
    setEditingCategoryId(category.categoryId);
    setCategoryForm({ categoryName: category.categoryName });
  };

  const handleDelete = async (categoryId) => {
    try {
      await deleteCategory(categoryId);
      toast.success("Category deleted successfully.");
      await loadCategories();
    } catch (error) {
      toast.error(error.response?.data?.message || "Unable to delete category.");
    }
  };

  const filteredCategories = categories.filter((category) =>
    category.categoryName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredCount = filteredCategories.length;
  const pageCount = Math.max(Math.ceil(filteredCount / itemsPerPage), 1);
  const paginatedCategories = filteredCategories.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handleCategoryPageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-4">
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-start gap-3 mb-4">
        <div>
          <h2 className="mb-2">Category Management</h2>
        </div>
      </div>

      <Card className="shadow-sm mb-4 border-0">
        <Card.Body>
          <Form>
            <Row className="g-3 align-items-end">
              <Col xs={12} md={9}>
                <Form.Group>
                  <Form.Label>Category Name</Form.Label>
                  <Form.Control
                    value={categoryForm.categoryName}
                    onChange={(e) => setCategoryForm({ categoryName: e.target.value })}
                    placeholder="Enter category name"
                    required
                  />
                </Form.Group>
              </Col>
              <Col xs={12} md={3} className="d-grid">
                <Button onClick={handleSubmit} size="lg">
                  {editingCategoryId ? "Update Category" : "Create Category"}
                </Button>
              </Col>
            </Row>
            <Row className="mt-3">
              <Col>
                <small className="text-muted">
                  {editingCategoryId
                    ? "You are editing an existing category. Save changes to update it."
                    : "Add a new category to keep your storefront navigation organized."}
                </small>
              </Col>
            </Row>
          </Form>
        </Card.Body>
      </Card>

      <Card className="shadow-sm border-0">
        <Card.Body>
          <div className="d-flex flex-column flex-lg-row justify-content-between align-items-start align-items-lg-center gap-3 mb-3">
            <div>
              <h5 className="mb-1">Categories</h5>
            </div>
            <div className="d-flex gap-2 flex-wrap align-items-center">
              <Form.Control
                type="search"
                placeholder="Search categories"
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  setCurrentPage(1);
                }}
                style={{ minWidth: 220, maxWidth: 320 }}
              />
            </div>
          </div>

          <Table striped bordered hover responsive className="mt-3">
            <thead>
              <tr>
                <th>Name</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {paginatedCategories.map((category) => (
                <tr key={category.categoryId}>
                  <td>
                    <div className="fw-semibold">{category.categoryName}</div>
                  </td>
                  <td>
                    <Button size="sm" variant="outline-secondary" onClick={() => handleEdit(category)}>
                      Edit
                    </Button>{" "}
                    <Button size="sm" variant="outline-danger" onClick={() => handleDelete(category.categoryId)}>
                      Delete
                    </Button>
                  </td>
                </tr>
              ))}
              {paginatedCategories.length === 0 && (
                <tr>
                  <td colSpan={3} className="text-center text-muted py-4">
                    No categories found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table>
          <FixedPagination
            currentPage={currentPage}
            totalPages={pageCount}
            onPageChange={handleCategoryPageChange}
          />
        </Card.Body>
      </Card>
    </Container>
  );
}

export default AdminCategories;
