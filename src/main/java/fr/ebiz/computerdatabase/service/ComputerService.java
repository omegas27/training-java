package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;

import java.util.Optional;

public interface ComputerService {

    /**
     * Get the computer with it's id.
     *
     * @param id The id of the computer to get
     * @return The computer if it exists or Optional.empty() if it does not
     */
    Optional<ComputerDto> get(int id);

    /**
     * Get the computers with pagination.
     *
     * @param query    The name to look for in the list of computers
     * @param pageable The pageable data
     * @return The paginated computers
     */
    Page<ComputerDto> getAll(String query, Pageable pageable);

    /**
     * Assert a computer is valid and insert it if it is.
     *
     * @param computer The computer to insert
     */
    void insert(ComputerDto computer);

    /**
     * Assert a computer is valid and update it if it is.
     *
     * @param computer The computer to update
     */
    void update(ComputerDto computer);

    /**
     * Assert a computer is valid and delete it if it is.
     *
     * @param computer The computer to delete
     */
    void delete(ComputerDto computer);


}