import { useState } from "react";
import { Button, Card, Col, Container, Form, Row, Spinner } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { login as loginUser } from "../../services/authService";
import { useAuth } from "../../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);

    try {
      const response = await loginUser(formData);

      login(response.data.accessToken);

      localStorage.setItem(
        "refreshToken",
        response.data.refreshToken
      );

      toast.success("Login successful!");

      setTimeout(() => {
        navigate("/");
      }, 1000);

    } catch (error) {
      toast.error(
        error.response?.data?.message || "Invalid username or password."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="mt-5">
      <ToastContainer />

      <Row className="justify-content-center">
        <Col md={5}>
          <Card className="shadow-lg p-4">

            <h2 className="text-center mb-4">
              Login
            </h2>

            <Form onSubmit={handleSubmit}>

              <Form.Group className="mb-3">
                <Form.Label>Username</Form.Label>

                <Form.Control
                  type="text"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  required
                />
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Label>Password</Form.Label>

                <Form.Control
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  required
                />
              </Form.Group>

              <Button
                className="w-100"
                type="submit"
                disabled={loading}
              >
                {loading ? (
                  <Spinner
                    animation="border"
                    size="sm"
                  />
                ) : (
                  "Login"
                )}
              </Button>

            </Form>

            <div className="text-center mt-3">
              Don't have an account?{" "}
              <Link to="/signup">
                Signup
              </Link>
            </div>

          </Card>
        </Col>
      </Row>

    </Container>
  );
}

export default Login;