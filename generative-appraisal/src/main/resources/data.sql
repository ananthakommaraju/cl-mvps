-- schema.sql

CREATE TABLE IF NOT EXISTS team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_name VARCHAR(255),
    description VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS goal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    start_date DATE,
    end_date DATE,
    team_id BIGINT,
    FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_name VARCHAR(255),
    team_id BIGINT,
    FOREIGN KEY (team_id) REFERENCES team(id)
);
CREATE TABLE IF NOT EXISTS objective (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    target_date DATE,
    status VARCHAR(255),
    employee_id BIGINT,
        goal_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employee(id),
        FOREIGN KEY (goal_id) REFERENCES goal(id)

);
CREATE TABLE IF NOT EXISTS accomplishment(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    date DATE,
    employee_id BIGINT,
    objective_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (objective_id) REFERENCES objective(id)
);
CREATE TABLE IF NOT EXISTS kpi (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    target_value double,
    actual_value double,
    employee_id BIGINT,
        FOREIGN KEY (employee_id) REFERENCES employee(id)
);
CREATE TABLE IF NOT EXISTS summary_report(
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        period VARCHAR(255),
        summary VARCHAR(255),
        employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employee(id)

);

-- data.sql
INSERT INTO team (team_name, description) VALUES
('Team A', 'Team A Description'),
('Team B', 'Team B Description');

-- Insert Goals
INSERT INTO goal (description, start_date, end_date, team_id) VALUES
('Goal 1 for Team A', '2024-01-01', '2024-12-31', 1),
('Goal 2 for Team B', '2024-01-01', '2024-12-31', 2);

-- Insert employees (employee_name, team_id)
INSERT INTO employee (employee_name, team_id) VALUES
('Employee 1', 1),
('Employee 2', 1),
('Employee 3', 2),
('Employee 4', 2);

-- Insert objectives for Employee 1
INSERT INTO objective (description, target_date, status, employee_id,goal_id) VALUES
('Objective 1 for Employee 1', '2024-12-31', 'IN_PROGRESS', 1,1),
('Objective 2 for Employee 1', '2024-11-30', 'NOT_STARTED', 1,1);

-- Insert objectives for Employee 2
INSERT INTO objective (description, target_date, status, employee_id,goal_id) VALUES
('Objective 1 for Employee 2', '2024-10-31', 'COMPLETED', 2,1),
('Objective 2 for Employee 2', '2024-12-31', 'BLOCKED', 2,1);

-- Insert objectives for Employee 3
INSERT INTO objective (description, target_date, status, employee_id,goal_id) VALUES
('Objective 1 for Employee 3', '2024-11-15', 'IN_PROGRESS', 3,2),
('Objective 2 for Employee 3', '2024-12-15', 'NOT_STARTED', 3,2);

-- Insert objectives for Employee 4
INSERT INTO objective (description, target_date, status, employee_id,goal_id) VALUES
('Objective 1 for Employee 4', '2024-10-15', 'COMPLETED', 4,2),
('Objective 2 for Employee 4', '2024-11-15', 'BLOCKED', 4,2);

--Insert accomplishments for employee 1
INSERT INTO accomplishment (description, date, employee_id,objective_id) VALUES
('Accomplishment 1 for Employee 1', '2024-12-31', 1,1),
('Accomplishment 2 for Employee 1', '2024-11-30', 1,1);

--Insert accomplishments for employee 2
INSERT INTO accomplishment (description, date, employee_id,objective_id) VALUES
('Accomplishment 1 for Employee 2', '2024-12-31', 2,2),
('Accomplishment 2 for Employee 2', '2024-11-30', 2,2);
--Insert accomplishments for employee 3
INSERT INTO accomplishment (description, date, employee_id,objective_id) VALUES
('Accomplishment 1 for Employee 3', '2024-12-31', 3,3),
('Accomplishment 2 for Employee 3', '2024-11-30', 3,3);
--Insert accomplishments for employee 4
INSERT INTO accomplishment (description, date, employee_id,objective_id) VALUES
('Accomplishment 1 for Employee 4', '2024-12-31', 4,4),
('Accomplishment 2 for Employee 4', '2024-11-30', 4,4);

-- Insert kpis for employee 1
INSERT INTO kpi(description, target_value, actual_value, employee_id) VALUES
('kpi 1 for employee 1', 100.00, 105.00,1),
('kpi 2 for employee 1', 90.00, 95.00,1);
-- Insert kpis for employee 2
INSERT INTO kpi(description, target_value, actual_value, employee_id) VALUES
('kpi 1 for employee 2', 100.00, 105.00,2),
('kpi 2 for employee 2', 90.00, 95.00,2);
-- Insert kpis for employee 3
INSERT INTO kpi(description, target_value, actual_value, employee_id) VALUES
('kpi 1 for employee 3', 100.00, 105.00,3),
('kpi 2 for employee 3', 90.00, 95.00,3);
-- Insert kpis for employee 4
INSERT INTO kpi(description, target_value, actual_value, employee_id) VALUES
('kpi 1 for employee 4', 100.00, 105.00,4),
('kpi 2 for employee 4', 90.00, 95.00,4);

--Insert summary report for employee 1
INSERT INTO summary_report(period, summary, employee_id) VALUES
('Q1', 'Good', 1),
('Q2', 'bad', 1);
--Insert summary report for employee 2
INSERT INTO summary_report(period, summary, employee_id) VALUES
('Q1', 'Good', 2),
('Q2', 'bad', 2);
--Insert summary report for employee 3
INSERT INTO summary_report(period, summary, employee_id) VALUES
('Q1', 'Good', 3),
('Q2', 'bad', 3);
--Insert summary report for employee 4
INSERT INTO summary_report(period, summary, employee_id) VALUES
('Q1', 'Good', 4),
('Q2', 'bad', 4);
