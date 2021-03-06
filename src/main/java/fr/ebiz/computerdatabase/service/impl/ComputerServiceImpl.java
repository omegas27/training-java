package fr.ebiz.computerdatabase.service.impl;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.PagingUtils;
import fr.ebiz.computerdatabase.mapper.ComputerMapper;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.persistence.dao.ComputerDao;
import fr.ebiz.computerdatabase.service.ComputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
public class ComputerServiceImpl implements ComputerService {

    private final ComputerDao computerDao;
    private final ComputerMapper computerMapper;

    /**
     * Constructor.
     *
     * @param computerDao       The computer dao to inject
     * @param computerMapper    The computer mapper to inject
     */
    @Autowired
    public ComputerServiceImpl(ComputerDao computerDao, ComputerMapper computerMapper) {
        this.computerDao = computerDao;
        this.computerMapper = computerMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ComputerDto> get(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be > 0");
        }

        return computerDao.get(id).map(c -> ComputerMapper.getInstance().toDto(c));

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings(value = "unchecked")
    @Override
    public Page<ComputerDto> getAll(GetAllComputersRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Pagination object is null");
        }

        if (request.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be > 0");
        }

        Integer numberOfComputers = computerDao.count(request.getQuery());

        Integer totalPage = PagingUtils.countPages(request.getPageSize(), numberOfComputers);

        if (request.getPage() < 0 || request.getPage() > totalPage) {
            throw new IllegalArgumentException("Page number must be [0-" + totalPage + "]");
        }

        List<Computer> computers;
        if (totalPage == 0) {
            computers = Collections.emptyList();
        } else {
            computers = computerDao.getAll(request.getQuery(), request.getPageSize(), request.getOffset(), request.getColumn(), request.getOrder());
        }

        return Page.builder()
                .currentPage(request.getPage())
                .totalPages(totalPage)
                .totalElements(numberOfComputers)
                .elements(ComputerMapper.getInstance().toDto(computers))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void insert(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        if (dto.getId() != null) {
            throw new IllegalArgumentException("Computer should not have an id");
        }

        computerDao.insert(computerMapper.toEntity(dto));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void update(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);

        computerDao.update(computerMapper.toEntity(dto));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void delete(ComputerDto dto) {
        assertComputerIsNotNull(dto);
        assertComputerIdIsNotNullAndExists(dto);

        computerDao.delete(dto.getId());
    }

    @Transactional
    @Override
    public void deleteByCompanyId(int companyId) {
        computerDao.deleteByCompanyId(companyId);
    }

    @Transactional
    @Override
    public void deleteComputers(List<Integer> ids) {
        if (!ids.isEmpty()) {
            computerDao.deleteComputers(ids);
        }
    }

    /**
     * Assert the computer object is not null, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerIsNotNull(ComputerDto computer) {
        if (computer == null) {
            throw new IllegalArgumentException("Computer object is null");
        }
    }

    /**
     * Assert the computer object has an id and exists in the database, throws an {@link IllegalArgumentException} otherwise.
     *
     * @param computer The computer to test
     */
    private void assertComputerIdIsNotNullAndExists(ComputerDto computer) {
        if (computer.getId() == null || !computerDao.get(computer.getId()).isPresent()) {
            throw new IllegalArgumentException("Computer should have an id and exist in the db");
        }
    }

}
