-- LIMS Database Setup Script
-- Run this script in your PostgreSQL database (pgAdmin or command line)

-- Create the database (run this first, then connect to lims_db)
CREATE DATABASE lims_db;

-- Connect to lims_db database and run the following:

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('Admin', 'Teacher', 'Student', 'Lab Technician')),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Labs table
CREATE TABLE IF NOT EXISTS labs (
    lab_id SERIAL PRIMARY KEY,
    lab_name VARCHAR(100) NOT NULL UNIQUE,
    capacity INT NOT NULL CHECK (capacity > 0),
    equipment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active', 'Maintenance', 'Inactive', 'Under Construction')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Computers table
CREATE TABLE IF NOT EXISTS computers (
    id SERIAL PRIMARY KEY,
    computer_id VARCHAR(20) UNIQUE NOT NULL,
    lab_id INT REFERENCES labs(lab_id) ON DELETE SET NULL,
    computer_name VARCHAR(100) NOT NULL,
    ip_address VARCHAR(15),
    specifications TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'Available' CHECK (status IN ('Available', 'In Use', 'Maintenance', 'Retired')),
    install_date DATE,
    notes TEXT,
    last_maintenance TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Software table
CREATE TABLE IF NOT EXISTS software (
    id SERIAL PRIMARY KEY,
    software_name VARCHAR(100) NOT NULL,
    version VARCHAR(50),
    license_type VARCHAR(50) CHECK (license_type IN ('Open Source', 'Academic', 'Professional', 'Enterprise', 'Subscription')),
    installations INT DEFAULT 0 CHECK (installations >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active', 'Inactive', 'Expired', 'Pending')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Complaints table
CREATE TABLE IF NOT EXISTS complaints (
    id SERIAL PRIMARY KEY,
    computer_id VARCHAR(20),
    department VARCHAR(100),
    issue_type VARCHAR(50) CHECK (issue_type IN ('Hardware', 'Software', 'Equipment', 'Network', 'Other')),
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'Open' CHECK (status IN ('Open', 'In Progress', 'Resolved', 'Closed', 'Cancelled')),
    urgency VARCHAR(20) DEFAULT 'Medium' CHECK (urgency IN ('Low', 'Medium', 'High', 'Critical')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_to VARCHAR(100),
    resolved_at TIMESTAMP
);

-- Lab reservations table
CREATE TABLE IF NOT EXISTS lab_reservations (
    id SERIAL PRIMARY KEY,
    requester_name VARCHAR(100) NOT NULL,
    lab_name VARCHAR(100) NOT NULL,
    reservation_date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL,
    purpose VARCHAR(50) NOT NULL CHECK (purpose IN ('Class', 'Research', 'Workshop', 'Training', 'Meeting', 'Exam')),
    description TEXT,
    status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected', 'Completed', 'Cancelled')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Software requests table
CREATE TABLE IF NOT EXISTS software_requests (
    id SERIAL PRIMARY KEY,
    computer_id VARCHAR(20),
    software_name VARCHAR(100) NOT NULL,
    version VARCHAR(50),
    urgency VARCHAR(20) DEFAULT 'Medium' CHECK (urgency IN ('Low', 'Medium', 'High', 'Critical')),
    justification TEXT,
    status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'In Progress', 'Completed', 'Approved', 'Rejected')),
    requested_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User access control table
CREATE TABLE IF NOT EXISTS user_access (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('Admin', 'Teacher', 'Student', 'Lab Technician')),
    department VARCHAR(100),
    access_level VARCHAR(20) NOT NULL CHECK (access_level IN ('Full', 'Limited', 'Basic', 'Read Only')),
    status VARCHAR(20) NOT NULL DEFAULT 'Active' CHECK (status IN ('Active', 'Inactive', 'Pending', 'Suspended')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO labs (lab_name, capacity, equipment, status) VALUES
('CS Lab 1', 30, '30 Desktop PCs, 1 Projector, Whiteboard', 'Active'),
('CS Lab 2', 25, '25 Desktop PCs, 1 Projector, Smart Board', 'Active'),
('SE Lab 1', 35, '35 Desktop PCs, 2 Projectors, Conference Setup', 'Active'),
('AI Lab', 20, '20 High-Performance PCs, GPU Cluster, 2 Projectors', 'Active'),
('Data Lab', 28, '28 Desktop PCs, Server Rack, Analytics Software', 'Active')
ON CONFLICT (lab_name) DO NOTHING;

INSERT INTO users (name, email, password_hash, role, department) VALUES
('System Admin', 'admin@lims.edu', '$2a$10$example_hash_admin', 'Admin', 'IT'),
('John Teacher', 'john.teacher@lims.edu', '$2a$10$example_hash_teacher', 'Teacher', 'Computer Science'),
('Jane Student', 'jane.student@lims.edu', '$2a$10$example_hash_student', 'Student', 'Computer Science'),
('Mike Technician', 'mike.tech@lims.edu', '$2a$10$example_hash_tech', 'Lab Technician', 'IT')
ON CONFLICT (email) DO NOTHING;

INSERT INTO computers (computer_id, lab_id, computer_name, ip_address, specifications, status, install_date) VALUES
('PC-CS1001', 1, 'CS1-PC01', '192.168.1.101', 'Intel i5, 8GB RAM, 256GB SSD', 'Available', '2024-01-15'),
('PC-CS1002', 1, 'CS1-PC02', '192.168.1.102', 'Intel i5, 8GB RAM, 256GB SSD', 'Available', '2024-01-15'),
('PC-CS2001', 2, 'CS2-PC01', '192.168.1.201', 'Intel i7, 16GB RAM, 512GB SSD', 'Available', '2024-01-20'),
('PC-AI001', 4, 'AI-PC01', '192.168.1.301', 'Intel i9, 32GB RAM, 1TB SSD, RTX 4080', 'Available', '2024-02-01'),
('PC-DATA01', 5, 'DATA-PC01', '192.168.1.401', 'Intel i7, 16GB RAM, 512GB SSD', 'Maintenance', '2024-01-25')
ON CONFLICT (computer_id) DO NOTHING;

INSERT INTO software (software_name, version, license_type, installations, status) VALUES
('Microsoft Office', '2021', 'Academic', 150, 'Active'),
('Visual Studio Code', '1.85.0', 'Open Source', 200, 'Active'),
('IntelliJ IDEA', '2023.3', 'Academic', 75, 'Active'),
('MATLAB', 'R2023b', 'Academic', 50, 'Active'),
('Adobe Creative Suite', '2024', 'Professional', 25, 'Active');

INSERT INTO user_access (user_id, name, role, department, access_level, status) VALUES
('U001', 'System Admin', 'Admin', 'IT', 'Full', 'Active'),
('U002', 'John Teacher', 'Teacher', 'Computer Science', 'Limited', 'Active'),
('U003', 'Jane Student', 'Student', 'Computer Science', 'Basic', 'Active'),
('U004', 'Mike Technician', 'Lab Technician', 'IT', 'Limited', 'Active'),
('U005', 'Sarah Professor', 'Teacher', 'Software Engineering', 'Limited', 'Active');

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_computers_lab_id ON computers(lab_id);
CREATE INDEX IF NOT EXISTS idx_computers_status ON computers(status);
CREATE INDEX IF NOT EXISTS idx_complaints_status ON complaints(status);
CREATE INDEX IF NOT EXISTS idx_reservations_date ON lab_reservations(reservation_date);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_access_status ON user_access(status);

-- Display success message
SELECT 'LIMS Database setup completed successfully!' as message;
