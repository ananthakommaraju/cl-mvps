-- Insert data into Team table
INSERT INTO team (id, team_name, description) VALUES
(1, 'Engineering Team', 'Team of engineers'),
(2, 'Sales Team', 'Team of Sales');

-- Insert data into Employee table for Engineering Team
INSERT INTO employee (id, employee_name, team_id) VALUES
(1, 'John Doe', 1),
(2, 'Alice Brown', 1),
(3, 'Jane Smith', 2),
(4, 'Bob Johnson', 2);

-- Insert data into Objective table for John Doe
INSERT INTO objective (id, description, target_date, status, employee_id) VALUES
(1, 'Complete advanced Java course', '2024-12-31', 'COMPLETED', 1),
(2, 'Contribute to open-source project', '2025-06-30', 'IN_PROGRESS', 1);

-- Insert data into Objective table for Alice Brown
INSERT INTO objective (id, description, target_date, status, employee_id) VALUES
(3, 'Learn Spring Boot framework', '2025-03-31', 'NOT_STARTED', 2),
(4, 'Attend tech conferences', '2025-11-30', 'IN_PROGRESS', 2);

-- Insert data into Objective table for Jane Smith
INSERT INTO objective (id, description, target_date, status, employee_id) VALUES
(5, 'Increase lead conversion rate', '2024-11-30', 'IN_PROGRESS', 3),
(6, 'Expand market reach', '2025-04-30', 'NOT_STARTED', 3);

-- Insert data into Objective table for Bob Johnson
INSERT INTO objective (id, description, target_date, status, employee_id) VALUES
(7, 'Improve customer onboarding process', '2025-05-31', 'IN_PROGRESS', 4),
(8, 'Collect customer feedback', '2025-09-30', 'NOT_STARTED', 4);
