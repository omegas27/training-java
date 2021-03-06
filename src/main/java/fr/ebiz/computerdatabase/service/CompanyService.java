package fr.ebiz.computerdatabase.service;

import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.dto.paging.Pageable;
import fr.ebiz.computerdatabase.model.Company;

import java.util.Optional;

public interface CompanyService {

    /**
     * Get the company with it's id.
     *
     * @param id The id of the company to get
     * @return The company if it exists or Optional.empty() if it does not
     */
    Optional<Company> get(int id);

    /**
     * Get the companies with pagination.
     *
     * @param pageable The pageable data
     * @return The paginated companies
     */
    Page<Company> getAll(Pageable pageable);

    /**
     * Delete a company and it's attached computers.
     *
     * @param company The company to delete
     */
    void delete(Company company);

    /**
     * Check whether a company exists.
     *
     * @param id The company id to check
     * @return True if the company exists
     */
    boolean exists(int id);
}
