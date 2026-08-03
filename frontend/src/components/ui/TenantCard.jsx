import { Card, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

function TenantCard({ tenant }) {
  const navigate = useNavigate();

  return (
    <Card className="shadow-sm h-100 border-0">
      <Card.Body className="text-center">

        <h3>🏪</h3>

        <Card.Title>{tenant.tenantName}</Card.Title>

        <Card.Text>{tenant.domain}</Card.Text>

        <Button
          onClick={() => navigate(`/products/${tenant.tenantName}`)}
        >
          Browse Products
        </Button>

      </Card.Body>
    </Card>
  );
}

export default TenantCard;