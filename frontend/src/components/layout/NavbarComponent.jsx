import { Badge, Container, Nav, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { FaShoppingCart } from "react-icons/fa";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";

function NavbarComponent() {
  const navigate = useNavigate();

  const {
    isAuthenticated,
    hasAnyRole,
    logout,
  } = useAuth();

  const { cartCount } = useCart();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <Navbar
      bg="dark"
      variant="dark"
      expand="lg"
      sticky="top"
    >
      <Container>
        <Navbar.Brand
          as={Link}
          to={isAuthenticated ? "/" : "/login"}
        >
          E-Commerce
        </Navbar.Brand>

        <Navbar.Toggle />

        <Navbar.Collapse>
          <Nav className="ms-auto align-items-center">
            {isAuthenticated ? (
              <>
                <Nav.Link as={Link} to="/">
                  Home
                </Nav.Link>

                {hasAnyRole(["ROLE_ADMIN"]) && (
                  <Nav.Link as={Link} to="/admin">
                    Admin Dashboard
                  </Nav.Link>
                )}

                {hasAnyRole(["ROLE_TENANT"]) && (
                  <>
                    <Nav.Link as={Link} to="/dashboard">
                      Dashboard
                    </Nav.Link>

                    <Nav.Link as={Link} to="/tenant/products">
                      Products
                    </Nav.Link>

                    <Nav.Link as={Link} to="/tenant/orders">
                      Tenant Orders
                    </Nav.Link>
                  </>
                )}

                {hasAnyRole(["ROLE_USER", "ROLE_TENANT"]) && (
                  <>
                    <Nav.Link as={Link} to="/favorites">
                      Favorites
                    </Nav.Link>

                    <Nav.Link as={Link} to="/orders">
                      Orders
                    </Nav.Link>

                    <Nav.Link as={Link} to="/cart">
                      <FaShoppingCart className="me-1" />

                      Cart

                      {cartCount > 0 && (
                        <Badge
                          bg="danger"
                          pill
                          className="ms-2"
                        >
                          {cartCount}
                        </Badge>
                      )}
                    </Nav.Link>
                  </>
                )}

                <Nav.Link
                  onClick={handleLogout}
                  role="button"
                >
                  Logout
                </Nav.Link>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to="/login">
                  Login
                </Nav.Link>

                <Nav.Link as={Link} to="/signup">
                  Signup
                </Nav.Link>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default NavbarComponent;