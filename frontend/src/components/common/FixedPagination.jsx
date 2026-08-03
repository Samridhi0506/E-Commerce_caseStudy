import { Pagination } from "react-bootstrap";

function FixedPagination({ currentPage, totalPages, onPageChange, maxVisiblePages = 5 }) {
  if (totalPages <= 1) {
    return null;
  }

  const half = Math.floor(maxVisiblePages / 2);
  let startPage = Math.max(1, currentPage - half);
  let endPage = startPage + maxVisiblePages - 1;

  if (endPage > totalPages) {
    endPage = totalPages;
    startPage = Math.max(1, endPage - maxVisiblePages + 1);
  }

  const pageNumbers = [];
  for (let page = startPage; page <= endPage; page += 1) {
    pageNumbers.push(page);
  }

  const hasPreviousRange = startPage > 1;
  const hasNextRange = endPage < totalPages;

  const goToPreviousRange = () => onPageChange(Math.max(1, startPage - maxVisiblePages));
  const goToNextRange = () => onPageChange(Math.min(totalPages, endPage + 1));

  return (
    <Pagination className="justify-content-center mt-3">
      <Pagination.First
        disabled={currentPage === 1}
        onClick={() => onPageChange(1)}
      />

      <Pagination.Prev
        disabled={currentPage === 1}
        onClick={() => onPageChange(currentPage - 1)}
      />

      {hasPreviousRange && (
        <Pagination.Item onClick={goToPreviousRange}>&laquo;</Pagination.Item>
      )}

      {pageNumbers.map((page) => (
        <Pagination.Item
          key={page}
          active={page === currentPage}
          onClick={() => onPageChange(page)}
        >
          {page}
        </Pagination.Item>
      ))}

      {hasNextRange && (
        <Pagination.Item onClick={goToNextRange}>&raquo;</Pagination.Item>
      )}

      <Pagination.Next
        disabled={currentPage === totalPages}
        onClick={() => onPageChange(currentPage + 1)}
      />

      <Pagination.Last
        disabled={currentPage === totalPages}
        onClick={() => onPageChange(totalPages)}
      />
    </Pagination>
  );
}

export default FixedPagination;
