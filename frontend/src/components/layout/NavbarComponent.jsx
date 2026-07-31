import { Container, Nav, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function NavbarComponent() {
  const navigate = useNavigate();

  const {
    isAuthenticated,
    hasAnyRole,
    logout,
  } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
      <Container>
        <Navbar.Brand
          as={Link}
          to={isAuthenticated ? "/" : "/login"}
        >
          E-Commerce
        </Navbar.Brand>

        <Navbar.Toggle />

        <Navbar.Collapse>
          <Nav className="ms-auto">

            {isAuthenticated ? (
              <>
                <Nav.Link as={Link} to="/">
                  Home
                </Nav.Link>

                {hasAnyRole(["ROLE_TENANT"]) && (
                  <Nav.Link as={Link} to="/dashboard">
                    Dashboard
                  </Nav.Link>
                )}

                <Nav.Link as={Link} to="/favorites">
                  Favorites
                </Nav.Link>

                <Nav.Link as={Link} to="/orders">
                  Orders
                </Nav.Link>

                <Nav.Link onClick={handleLogout} role="button">
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