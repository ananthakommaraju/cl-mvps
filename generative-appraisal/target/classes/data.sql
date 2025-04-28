-- Insert data into Employee table
INSERT INTO employee (id, department, name) VALUES
(1, 'Engineering', 'John Doe'),
(2, 'Sales', 'Jane Smith');

-- Insert data into Goal table for John Doe
INSERT INTO goal (id, summary, employee_id) VALUES
(1, 'Improve software development skills', 1),
(2, 'Learn new technologies', 1);

-- Insert data into Goal table for Jane Smith
INSERT INTO goal (id, summary, employee_id) VALUES
(3, 'Increase sales by 10%', 2),
(4, 'Improve customer satisfaction', 2);

-- Insert data into Objective table for Goal 1 (John Doe)
INSERT INTO objective (id, summary, goal_id) VALUES
(1, 'Complete advanced Java course', 1),
(2, 'Contribute to open-source project', 1);

-- Insert data into Objective table for Goal 2 (John Doe)
INSERT INTO objective (id, summary, goal_id) VALUES
(3, 'Learn Spring Boot framework', 2),
(4, 'Attend tech conferences', 2);

-- Insert data into Objective table for Goal 3 (Jane Smith)
INSERT INTO objective (id, summary, goal_id) VALUES
(5, 'Increase lead conversion rate', 3),
(6, 'Expand market reach', 3);

-- Insert data into Objective table for Goal 4 (Jane Smith)
INSERT INTO objective (id, summary, goal_id) VALUES
(7, 'Improve customer onboarding process', 4),
(8, 'Collect customer feedback', 4);

-- Insert data into Accomplishment table for Objective 1
INSERT INTO accomplishment (id, description, objective_id) VALUES
(1, 'Finished Java course with high grade', 1),
(2, 'Got new skills in Java', 1);

-- Insert data into Accomplishment table for Objective 2
INSERT INTO accomplishment (id, description, objective_id) VALUES
(3, 'Contributed to a GitHub project', 2),
(4, 'Helped to improve the code in GitHub project', 2);

-- Insert data into Accomplishment table for Objective 3
INSERT INTO accomplishment (id, description, objective_id) VALUES
(5, 'Completed the Spring boot tutorial', 3),
(6, 'Created an application with spring boot', 3);

-- Insert data into Accomplishment table for Objective 4
INSERT INTO accomplishment (id, description, objective_id) VALUES
(7, 'Attended Devoxx conference', 4),
(8, 'Learned new tips and tricks in tech conference', 4);

-- Insert data into Accomplishment table for Objective 5
INSERT INTO accomplishment (id, description, objective_id) VALUES
(9, 'Increased conversion rate by 5%', 5),
(10, 'Improved the lead process', 5);

-- Insert data into Accomplishment table for Objective 6
INSERT INTO accomplishment (id, description, objective_id) VALUES
(11, 'Created partnership with new company', 6),
(12, 'Got a new market reach', 6);

-- Insert data into Accomplishment table for Objective 7
INSERT INTO accomplishment (id, description, objective_id) VALUES
(13, 'Simplified the onboarding process', 7),
(14, 'Reduced the onboarding time', 7);

-- Insert data into Accomplishment table for Objective 8
INSERT INTO accomplishment (id, description, objective_id) VALUES
(15, 'Collected feedback from all customers', 8),
(16, 'Organized a session to get feedback', 8);