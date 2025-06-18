package controller;

import model.Computer;
import service.ComputerService;
import java.util.List;
import java.util.Date;

public class ComputerController extends BaseController {
    private final ComputerService computerService;
    
    public ComputerController(ComputerService computerService) {
        this.computerService = computerService;
    }
    
    public void addComputer(String lab, String computerName, String ipAddress, 
                          String specifications, String status) {
        // Validate inputs
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (!isValidString(computerName, 2, 50)) {
            throw new IllegalArgumentException("Invalid computer name");
        }
        if (!isValidIPAddress(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP address");
        }
        if (!isValidString(specifications, 5, 200)) {
            throw new IllegalArgumentException("Invalid specifications");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        lab = sanitizeInput(lab);
        computerName = sanitizeInput(computerName);
        ipAddress = sanitizeInput(ipAddress);
        specifications = sanitizeInput(specifications);
        status = sanitizeInput(status);
        
        // Create computer object
        Computer computer = new Computer(
            "PC-" + System.currentTimeMillis(),
            lab,
            computerName,
            ipAddress,
            specifications,
            status,
            new Date().toString(),
            ""
        );
        
        computerService.addComputer(computer);
    }
    
    public void updateComputer(String id, String lab, String computerName, 
                             String ipAddress, String specifications, String status) {
        // Validate ID
        if (!isValidString(id, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        
        // Validate other inputs
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (!isValidString(computerName, 2, 50)) {
            throw new IllegalArgumentException("Invalid computer name");
        }
        if (!isValidIPAddress(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP address");
        }
        if (!isValidString(specifications, 5, 200)) {
            throw new IllegalArgumentException("Invalid specifications");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        id = sanitizeInput(id);
        lab = sanitizeInput(lab);
        computerName = sanitizeInput(computerName);
        ipAddress = sanitizeInput(ipAddress);
        specifications = sanitizeInput(specifications);
        status = sanitizeInput(status);
        
        // Create computer object
        Computer computer = new Computer(
            id,
            lab,
            computerName,
            ipAddress,
            specifications,
            status,
            new Date().toString(),
            ""
        );
        
        computerService.updateComputer(computer);
    }
    
    public void updateMaintenance(String computerId, String maintenanceNotes) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (!isValidString(maintenanceNotes, 5, 500)) {
            throw new IllegalArgumentException("Invalid maintenance notes");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        maintenanceNotes = sanitizeInput(maintenanceNotes);
        
        Computer computer = computerService.getComputerById(computerId);
        if (computer != null) {
            computer.setLastMaintenance(new Date());
            computerService.updateComputer(computer);
        }
    }
    
    public List<Computer> getAllComputers() {
        return computerService.getAllComputers();
    }
    
    public List<Computer> getComputersByLab(String lab) {
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        lab = sanitizeInput(lab);
        return computerService.getComputersByLab(lab);
    }
    
    public List<Computer> getComputersByStatus(String status) {
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        status = sanitizeInput(status);
        return computerService.getComputersByStatus(status);
    }
    
    public void deleteComputer(String computerId) {
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        computerId = sanitizeInput(computerId);
        computerService.deleteComputer(computerId);
    }

    public Computer getComputerById(String computerId) {
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        computerId = sanitizeInput(computerId);
        return computerService.getComputerById(computerId);
    }
} 