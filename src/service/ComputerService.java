package service;

import dao.ComputerDAO;
import model.Computer;
import ui.TableRefreshManager;
import java.util.List;

/**
 * Service class for managing computers in the LIMS system.
 * Handles all business logic related to computer management.
 */
public class ComputerService {
    private ComputerDAO computerDAO;

    public ComputerService() {
        computerDAO = new ComputerDAO();

        // Test database connection
        if (!computerDAO.testConnection()) {
            System.err.println("⚠️ Warning: Database connection failed. Some features may not work properly.");
        }
    }

    public List<Computer> getAllComputers() {
        return computerDAO.findAll();
    }

    public List<Computer> getComputersByLab(String lab) {
        // Filter by lab using DAO results - need to convert lab name to lab ID
        return getAllComputers().stream()
            .filter(computer -> computer.getLab().equals(lab))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Computer> getComputersByStatus(String status) {
        return computerDAO.findByStatus(status);
    }

    public void addComputer(Computer computer) {
        boolean success = computerDAO.insert(computer);

        if (success) {
            // Refresh the dashboard table immediately
            TableRefreshManager.getInstance().refreshTable("computers");
        }
    }

    public void updateComputer(Computer computer) {
        boolean success = computerDAO.update(computer);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("computers");
        }
    }

    public void deleteComputer(String computerId) {
        boolean success = computerDAO.delete(computerId);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("computers");
        }
    }

    public Computer getComputerById(String computerId) {
        return computerDAO.findById(computerId);
    }
}