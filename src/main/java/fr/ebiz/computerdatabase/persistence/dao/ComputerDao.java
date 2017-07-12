package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.model.Computer;

import java.util.List;
import java.util.Optional;

public interface ComputerDao {
    /**
     * Get the computer from the database.
     *
     * @param id The id of the computer to get
     * @return The computer if it exists or Optional.empty() if it does not
     */
    Optional<Computer> get(int id);

    /**
     * Get the computers from the database paginated.
     *
     * @param request    The request parameters
     * @return The paginated computers
     */
    List<Computer> getAll(GetAllComputersRequest request);

    /**
     * Count the number of elements in the database.
     *
     * @param query name to look for
     * @return the total number of elements
     */
    int count(String query);

    /**
     * Insert a computer in the database.
     *
     * @param model The computer to insert
     * @return true if the computer was inserted
     */
    boolean insert(Computer model);

    /**
     * Update a computer in the database.
     *
     * @param model The computer to update
     * @return true if the computer was updated
     */
    boolean update(Computer model);

    /**
     * Delete a computer from the database.
     *
     * @param id The computer's id to delete
     * @return true if the computer was deleted
     */
    boolean delete(Integer id);

    /**
     * Delete computers introduced by a company.
     *
     * @param companyId The id of the company
     * @return true if any computer was deleted
     */
    boolean deleteByCompanyId(Integer companyId);

    enum SortColumn {
        NAME("computer.name"), INTRODUCED("computer.introduced, computer.name"), DISCONTINUED("computer.discontinued, computer.name"), COMPANY_NAME("company.name, computer.name"), COMPANY_ID("company.id, computer.name");

        String field;

        /**
         * Constructor.
         *
         * @param field The field to use in DB queries
         */
        SortColumn(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}
