-- Create user
CREATE USER appuser WITH PASSWORD 'password123';

-- Create databases
CREATE DATABASE grades_db OWNER appuser;
CREATE DATABASE billing_db OWNER appuser;
CREATE DATABASE students_db OWNER appuser;
CREATE DATABASE auth_db OWNER appuser;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE grades_db TO appuser;
GRANT ALL PRIVILEGES ON DATABASE billing_db TO appuser;
GRANT ALL PRIVILEGES ON DATABASE students_db TO appuser;
GRANT ALL PRIVILEGES ON DATABASE auth_db TO appuser;

-- Show results
\l
