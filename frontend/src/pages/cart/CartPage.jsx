import {
  Button,
  Card,
  Col,
  Container,
  Row,
  Table,
} from "react-bootstrap";
import { FaMinus, FaPlus, FaTrash } from "react-icons/fa";
import { useCart } from "../../context/CartContext";


function CartPage() {

  const {
  cartItems,
  loading,
  increaseQuantity,
  decreaseQuantity,
  removeItem,
} = useCart();

  const totalAmount = cartItems.reduce(
    (sum, item) => sum + Number(item.subtotal),
    0
  );

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