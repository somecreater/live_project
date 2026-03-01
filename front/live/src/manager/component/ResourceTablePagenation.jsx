import { Pagination } from 'react-bootstrap';

function ResourceTablePagenation({
    page,
    totalPage,
    handlePageChange,
}) {

    return (<>
        {totalPage > 1 && (
            <div className="d-flex justify-content-center mt-4">
                <Pagination>
                    <Pagination.First onClick={() => handlePageChange(0)} disabled={page === 0} />
                    <Pagination.Prev onClick={() => handlePageChange(page - 1)} disabled={page === 0} />
                    {Array.from({ length: totalPage }, (_, index) => (
                        <Pagination.Item key={index} active={index === page} onClick={() => handlePageChange(index)}>
                            {index + 1}
                        </Pagination.Item>
                    ))}
                    <Pagination.Next onClick={() => handlePageChange(page + 1)} disabled={page === totalPage - 1} />
                    <Pagination.Last onClick={() => handlePageChange(totalPage - 1)} disabled={page === totalPage - 1} />
                </Pagination>
            </div>
        )}
    </>
    );
}

export default ResourceTablePagenation;
