"""
Database connection module for MySQL airline_db
"""
import mysql.connector
from mysql.connector import Error
import os
from dotenv import load_dotenv

load_dotenv()

class DatabaseConnection:
    """Manages MySQL database connections"""
    
    @staticmethod
    def get_connection():
        """Create and return a MySQL connection"""
        try:
            connection = mysql.connector.connect(
                host=os.getenv("MYSQL_HOST", "localhost"),
                user=os.getenv("MYSQL_USER", "root"),
                password=os.getenv("MYSQL_PASSWORD", ""),
                database=os.getenv("MYSQL_DATABASE", "airline_db"),
                port=int(os.getenv("MYSQL_PORT", "3306"))
            )
            if connection.is_connected():
                return connection
        except Error as e:
            print(f"❌ Database connection error: {e}")
            return None
    
    @staticmethod
    def execute_query(query, params=None):
        """Execute a SELECT query and return results"""
        connection = DatabaseConnection.get_connection()
        if not connection:
            return None
        
        try:
            cursor = connection.cursor(dictionary=True)
            if params:
                cursor.execute(query, params)
            else:
                cursor.execute(query)
            results = cursor.fetchall()
            cursor.close()
            return results
        except Error as e:
            print(f"❌ Query error: {e}")
            return None
        finally:
            connection.close()
    
    @staticmethod
    def execute_update(query, params=None):
        """Execute INSERT/UPDATE/DELETE query"""
        connection = DatabaseConnection.get_connection()
        if not connection:
            return False
        
        try:
            cursor = connection.cursor()
            if params:
                cursor.execute(query, params)
            else:
                cursor.execute(query)
            connection.commit()
            affected_rows = cursor.rowcount
            cursor.close()
            return affected_rows > 0
        except Error as e:
            print(f"❌ Update error: {e}")
            connection.rollback()
            return False
        finally:
            connection.close()


if __name__ == "__main__":
    # Test connection
    conn = DatabaseConnection.get_connection()
    if conn:
        print("✅ Database connection successful!")
        conn.close()
    else:
        print("❌ Database connection failed!")
