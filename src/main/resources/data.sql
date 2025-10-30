-- Check if the admin table is empty before inserting
-- This prevents duplicate entries on subsequent restarts if ddl-auto is not 'create-drop'

INSERT INTO admin (email_id, password)
SELECT 'admin@example.com', 'Admin@123'
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE email_id = 'admin@example.com');
-- ------------------------------
-- 1. Single entry for FACULTY table (Only insert if EMAIL doesn't exist)
-- ------------------------------
INSERT INTO FACULTY (DEPARTMENT, EMAIL, NAME, PASSWORD)
SELECT * FROM (VALUES ('CSE', 'prof@example.com', 'Dr. Alice Smith', 'pass123')) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM FACULTY WHERE EMAIL = 'prof@example.com'
);


-- ------------------------------
-- 2. Single entry for STUDENTS table (Only insert if EMAIL doesn't exist)
-- ------------------------------
INSERT INTO STUDENTS (DEPARTMENT, EMAIL, NAME, PASSWORD)
SELECT * FROM (VALUES ('CSE', 'student@example.com', 'Santhosh Kommireddy', 'pass123')) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM STUDENTS WHERE EMAIL = 'student@example.com'
);