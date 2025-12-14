#!/usr/bin/env python3
"""
Simple database setup script
"""
import psycopg2
from psycopg2 import sql

# Connection parameters
POSTGRES_HOST = "localhost"
POSTGRES_PORT = 5432
POSTGRES_USER = "postgres"
POSTGRES_PASSWORD = "postgres"  # Change if you set a different password

# Databases to create
DATABASES = ["grades_db", "billing_db", "students_db", "auth_db"]
DB_USER = "user"
DB_PASSWORD = "password"

def create_databases():
    """Create databases and users"""
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
        
        print("Creating user and databases...")
        
        # Create user if not exists
        try:
            cursor.execute(f"CREATE USER {DB_USER} WITH PASSWORD '{DB_PASSWORD}'")
            print(f"✓ User '{DB_USER}' created")
        except psycopg2.Error:
            print(f"✓ User '{DB_USER}' already exists")
        
        # Create databases
        for db_name in DATABASES:
            try:
                cursor.execute(f"CREATE DATABASE {db_name} OWNER {DB_USER}")
                print(f"✓ Database '{db_name}' created")
            except psycopg2.Error:
                print(f"✓ Database '{db_name}' already exists")
        
        # Grant privileges
        for db_name in DATABASES:
            cursor.execute(f"GRANT ALL PRIVILEGES ON DATABASE {db_name} TO {DB_USER}")
            print(f"✓ Privileges granted for '{db_name}'")
        
        cursor.close()
        conn.close()
        
        print("\n" + "="*60)
        print("Database setup complete!")
        print("="*60)
        print(f"\nConnection strings:")
        for db in DATABASES:
            print(f"  {db}: postgresql://{DB_USER}:{DB_PASSWORD}@localhost:5432/{db}")
        
    except Exception as e:
        print(f"Error: {e}")
        print("\nMake sure PostgreSQL is running and password is correct")

if __name__ == "__main__":
    create_databases()
