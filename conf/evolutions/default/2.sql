# --- !Ups
CREATE TABLE contracts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    contract_type VARCHAR(50) NOT NULL,
    employment_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    hours_per_week INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee
      FOREIGN KEY (employee_id)
      REFERENCES employees(id)
      ON DELETE CASCADE
);

# --- !Downs
DROP TABLE IF EXISTS contracts;