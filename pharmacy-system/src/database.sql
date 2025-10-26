-- Drop old table if exists
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[admin_users]') AND type in (N'U'))
BEGIN
    DROP TABLE [dbo].[admin_users];
END
GO

-- Create new users table
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL,
    first_name NVARCHAR(100) NOT NULL,
    last_name NVARCHAR(100) NOT NULL,
    role NVARCHAR(50) NOT NULL,
    active BIT NOT NULL DEFAULT 1
);
GO

-- Create indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(active);
GO

-- Insert default users with BCrypt encoded passwords (password: admin123)
INSERT INTO users (username, password, email, first_name, last_name, role, active) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@pharmacy.com', 'System', 'Administrator', 'ADMIN', 1),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'manager@pharmacy.com', 'John', 'Manager', 'MANAGER', 1),
('pharmacist1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'pharmacist1@pharmacy.com', 'Dr. Sarah', 'Wilson', 'PHARMACIST', 1),
('inventory1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'inventory@pharmacy.com', 'Mike', 'Johnson', 'INVENTORY_MANAGER', 1),
('supplier1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'supplier@pharmacy.com', 'Lisa', 'Brown', 'SUPPLIER', 1),
('delivery1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'delivery@pharmacy.com', 'Tom', 'Davis', 'DELIVERY_STAFF', 1),
('itofficer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'it@pharmacy.com', 'Alex', 'Smith', 'IT_OFFICER', 1);
GO

-- Verify the data
SELECT id, username, email, first_name, last_name, role, active FROM users;
GO
