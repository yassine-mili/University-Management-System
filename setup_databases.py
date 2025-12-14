"""
Database Setup Script for University Management System
Creates necessary databases and tables
"""
import psycopg2
from psycopg2 import sql
import sys

# Database connection parameters
POSTGRES_HOST = "localhost"
POSTGRES_PORT = 5432
POSTGRES_USER = "postgres"
POSTGRES_PASSWORD = "postgres"  # Change this to your PostgreSQL password

# Databases to create
DATABASES = [
    {
        "name": "grades_db",
        "user": "user",
        "password": "password"
    },
    {
        "name": "students_db",
        "user": "user",
        "password": "password"
    },
    {
        "name": "auth_db",
        "user": "postgres",
        "password": "postgres"
    }
]

def create_database(db_name, db_user, db_password):
    """Create a database and user"""
    try:
        # Connect to default postgres database
        conn = psycopg2.connect(
            host=POSTGRES_HOST,
            port=POSTGRES_PORT,
            user=POSTGRES_USER,
            password=POSTGRES_PASSWORD,
            database="postgres"
        )
        conn.autocommit = True
        cursor = conn.cursor()

        # Check if user exists
        cursor.execute(f"SELECT 1 FROM pg_user WHERE usename = '{db_user}'")
        if not cursor.fetchone():
            print(f"Creating user: {db_user}")
            cursor.execute(sql.SQL("CREATE USER {} WITH PASSWORD %s").format(
                sql.Identifier(db_user)
            ), (db_password,))
        else:
            print(f"User {db_user} already exists")

        # Check if database exists
        cursor.execute(f"SELECT 1 FROM pg_database WHERE datname = '{db_name}'")
        if not cursor.fetchone():
            print(f"Creating database: {db_name}")
            cursor.execute(sql.SQL("CREATE DATABASE {} OWNER {}").format(
                sql.Identifier(db_name),
                sql.Identifier(db_user)
            ))
        else:
            print(f"Database {db_name} already exists")

        # Grant privileges
        cursor.execute(sql.SQL("GRANT ALL PRIVILEGES ON DATABASE {} TO {}").format(
            sql.Identifier(db_name),
            sql.Identifier(db_user)
        ))

        cursor.close()
        conn.close()
        print(f"✓ Database {db_name} setup complete\n")
        return True

    except psycopg2.Error as e:
        print(f"✗ Error setting up {db_name}: {e}\n")
        return False
    except Exception as e:
        print(f"✗ Unexpected error: {e}\n")
        return False

def main():
    print("=" * 60)
    print("University Management System - Database Setup")
    print("=" * 60)
    print(f"\nConnecting to PostgreSQL at {POSTGRES_HOST}:{POSTGRES_PORT}")
    print(f"Using user: {POSTGRES_USER}\n")

    # Test connection
    try:
        test_conn = psycopg2.connect(
            host=POSTGRES_HOST,
            port=POSTGRES_PORT,
            user=POSTGRES_USER,
            password=POSTGRES_PASSWORD,
            database="postgres"
        )
        test_conn.close()
        print("✓ PostgreSQL connection successful\n")
    except psycopg2.Error as e:
        print(f"✗ Cannot connect to PostgreSQL: {e}")
        print("\nPlease ensure:")
        print("1. PostgreSQL is installed and running")
        print("2. The password is correct (default: 'postgres')")
        print("3. PostgreSQL is accessible on localhost:5432")
        sys.exit(1)

    # Create databases
    print("Creating databases...\n")
    success_count = 0
    for db in DATABASES:
        if create_database(db["name"], db["user"], db["password"]):
            success_count += 1

    # Summary
    print("=" * 60)
    print(f"Setup complete: {success_count}/{len(DATABASES)} databases created")
    print("=" * 60)
    print("\nConnection strings:")
    print("- Grades:  postgresql://user:password@localhost:5432/grades_db")
    print("- Students: postgresql://user:password@localhost:5432/students_db")
    print("- Auth:    postgresql://postgres:postgres@localhost:5432/auth_db")

if __name__ == "__main__":
    main()
