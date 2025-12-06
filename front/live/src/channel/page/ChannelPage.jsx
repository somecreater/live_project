import React, { useEffect, useState } from 'react';
import { Container, Row, Col, Alert, Spinner, Form, Button, InputGroup, Pagination } from "react-bootstrap";
import ChannelList from "../component/ChannelList";
import ApiService from "../../common/api/ApiService";

function ChannelPage() {
    const [channels, setChannels] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [totalPages, setTotalPages] = useState(0);

    const [searchParams, setSearchParams] = useState({
      page: 0,
      size: 9, 
      type: 'NAME',
      keyword: ''
    });

    const [formType, setFormType] = useState('NAME');
    const [formKeyword, setFormKeyword] = useState('');

    const fetchChannels = async (params) => {
      setLoading(true);
      setError(null);
      try {
        const response = await ApiService.channel.search(params);
        if (response.data && response.data.result) {
          const pageData = response.data.channelPage;
          if (pageData && pageData.content) {
            setChannels(pageData.content);
            setTotalPages(pageData.totalPages);
          } else {
            setChannels([]);
            setTotalPages(0);
          }
        } else {
          setChannels([]);
          setTotalPages(0);
        }
      } catch (err) {
        console.error("Failed to fetch channels", err);
        setError("채널 목록을 불러오는데 실패했습니다.");
        setChannels([]);
      } finally {
        setLoading(false);
      }
    };

    useEffect(() => {
      fetchChannels(searchParams);
    }, [searchParams]);

    const handleSearch = (e) => {
      e.preventDefault();
      setSearchParams({
        ...searchParams,
        page: 0, 
        type: formType,
        keyword: formKeyword
      });
    };

    const handlePageChange = (pageNumber) => {
      setSearchParams({
        ...searchParams,
        page: pageNumber
      });
    };

    const renderPaginationItems = () => {
      let items = [];
      const currentPage = searchParams.page;
      const maxButtons = 5;

      let startPage = Math.max(0, currentPage - Math.floor(maxButtons / 2));
      let endPage = Math.min(totalPages - 1, startPage + maxButtons - 1);

      if (endPage - startPage + 1 < maxButtons) {
        startPage = Math.max(0, endPage - maxButtons + 1);
      }

      for (let number = startPage; number <= endPage; number++) {
        items.push(
          <Pagination.Item key={number} active={number === currentPage} onClick={() => handlePageChange(number)}>
            {number + 1}
          </Pagination.Item>,
        );
      }
      return items;
    };

    const inputWidth = Math.min(600, Math.max(300, formKeyword.length * 20)) + 'px';

    return (
        <Container className="mt-4">
            <h2 className="mb-4">채널 목록</h2>

            {/* Search Form */}
            <Form onSubmit={handleSearch} className="mb-4">
                <Row className="justify-content-md-center">
                    <Col md="auto">
                        <InputGroup>
                            <Form.Select
                                style={{ maxWidth: '150px' }}
                                value={formType}
                                onChange={(e) => setFormType(e.target.value)}
                            >
                                <option value="NAME">이름</option>
                                <option value="DESCRIPTION">설명</option>
                                <option value="USER_LOGIN_ID">채널 소유주 ID</option>
                            </Form.Select>
                            <Form.Control
                                type="text"
                                placeholder="검색어를 입력하세요"
                                value={formKeyword}
                                onChange={(e) => setFormKeyword(e.target.value)}
                                style={{ width: inputWidth, transition: 'width 0.2s ease-in-out' }}
                            />
                            <Button variant="primary" type="submit">
                                검색
                            </Button>
                        </InputGroup>
                    </Col>
                </Row>
            </Form>

            {loading ? (
                <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '30vh' }}>
                    <Spinner animation="border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </Spinner>
                </Container>
            ) : error ? (
                <Alert variant="danger">{error}</Alert>
            ) : (
                <>
                    {channels.length === 0 ? (
                        <Alert variant="info" className="text-center">검색 결과가 없습니다.</Alert>
                    ) : (
                        <ChannelList channels={channels} />
                    )}

                    {/* Pagination */}
                    {totalPages > 1 && (
                        <div className="d-flex justify-content-center mt-4">
                            <Pagination>
                                <Pagination.First onClick={() => handlePageChange(0)} disabled={searchParams.page === 0} />
                                <Pagination.Prev onClick={() => handlePageChange(searchParams.page - 1)} disabled={searchParams.page === 0} />
                                {renderPaginationItems()}
                                <Pagination.Next onClick={() => handlePageChange(searchParams.page + 1)} disabled={searchParams.page === totalPages - 1} />
                                <Pagination.Last onClick={() => handlePageChange(totalPages - 1)} disabled={searchParams.page === totalPages - 1} />
                            </Pagination>
                        </div>
                    )}
                </>
            )}
        </Container>
    );
}

export default ChannelPage;