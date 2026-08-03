import {
  Button,
  Card,
  Col,
  Container,
  Row,
  Table,
} from "react-bootstrap";
import { FaMinus, FaPlus, FaTrash } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import { checkout } from "../../services/orderService";


function CartPage() {

  const navigate = useNavigate();
  const { tenantName, userId } = useAuth();

  const {
  cartItems,
  loading,
  increaseQuantity,
  decreaseQuantity,
  removeItem,
  refreshCart,
} = useCart();

  const totalAmount = cartItems.reduce(
    (sum, item) => sum + Number(item.subtotal),
    0
  );

  const handleCheckout = async () => {
    if (!userId) {
      toast.error("Please log in to checkout.");
      return;
    }

    try {
      const tenantGroups = cartItems.reduce((acc, item) => {
        const key = item.tenant || tenantName || "global";

        if (!acc[key]) {
          acc[key] = [];
        }

        acc[key].push(item);
        return acc;
      }, {});

      for (const tenant of Object.keys(tenantGroups)) {
        await checkout(tenant, userId);
      }

      await refreshCart();
      toast.success("Order placed successfully!");
      navigate("/orders");
    } catch (error) {
      toast.error(
        error.response?.data?.message || "Unable to complete checkout."
      );
    }
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <h4>Loading Cart...</h4>
      </Container>
    );
  }

  if (cartItems.length === 0) {
    return (
      <Container className="mt-5 text-center">
        <h3>Your Cart is Empty 🛒</h3>
      </Container>
    );
  }

  return (
    <Container className="mt-4">

      <h2 className="mb-4">My Cart</h2>

      <Card className="shadow-sm">

        <Card.Body>

          <Table responsive hover>

            <thead>
              <tr>
                <th>Product</th>
                <th>Price</th>
                <th width="180">Quantity</th>
                <th>Subtotal</th>
                <th></th>
              </tr>
            </thead>

            <tbody>

              {cartItems.map((item) => (

                <tr key={item.cartId}>

                  <td>
                    <div className="fw-bold">
                      {item.productName}
                    </div>

                    <small className="text-muted">
                      {item.category}
                    </small>
                  </td>

                  <td>
                    ₹
                    {Number(item.price).toLocaleString(
                      "en-IN"
                    )}
                  </td>

                  <td>

                    <div className="d-flex align-items-center gap-2">

                      <Button
                        size="sm"
                        variant="outline-danger"
                        onClick={() =>
                          decreaseQuantity(item.productId)
                        }
                      >
                        <FaMinus />
                      </Button>

                      <strong>{item.quantity}</strong>

                      <Button
                        size="sm"
                        variant="outline-success"
                        onClick={() =>
                          increaseQuantity(item.productId)
                        }
                      >
                        <FaPlus />
                      </Button>

                    </div>

                  </td>

                  <td>
                    ₹
                    {Number(item.subtotal).toLocaleString(
                      "en-IN"
                    )}
                  </td>

                  <td>

                    <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => removeItem(item.productId)}
                        >
                        <FaTrash />
                    </Button>

                  </td>

                </tr>

              ))}

            </tbody>

          </Table>

        </Card.Body>

      </Card>

      <Row className="justify-content-end mt-4">

        <Col md={4}>

          <Card className="shadow">

            <Card.Body>

              <h4>
                Total :
                <span className="float-end text-success">
                  ₹
                  {totalAmount.toLocaleString("en-IN")}
                </span>
              </h4>

              <div className="d-grid mt-3">

                <Button
                  size="lg"
                  variant="success"
                  onClick={handleCheckout}
                >
                  Proceed to Checkout
                </Button>

              </div>

            </Card.Body>

          </Card>

        </Col>

      </Row>

    </Container>
  );
}

export default CartPage;