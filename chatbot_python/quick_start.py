#!/usr/bin/env python
"""
Quick Start Script - Test database connection and run chatbot
"""
import os
import sys
from pathlib import Path

def check_python_version():
    """Check if Python 3.8+ is installed"""
    if sys.version_info < (3, 8):
        print("❌ Python 3.8+ required!")
        return False
    print(f"✅ Python {sys.version_info.major}.{sys.version_info.minor} detected")
    return True

def check_dependencies():
    """Check if required packages are installed"""
    required = ['streamlit', 'mysql', 'pandas', 'dotenv']
    missing = []
    
    for package in required:
        try:
            __import__(package)
            print(f"✅ {package} installed")
        except ImportError:
            print(f"❌ {package} NOT installed")
            missing.append(package)
    
    if missing:
        print(f"\n📦 Install missing packages:")
        print(f"   pip install {' '.join(missing)}")
        return False
    return True

def test_database_connection():
    """Test MySQL database connection"""
    try:
        from db_connection import DatabaseConnection
        print("\n🔌 Testing database connection...")
        
        conn = DatabaseConnection.get_connection()
        if conn:
            print("✅ Database connection successful!")
            conn.close()
            
            # Test a simple query
            result = DatabaseConnection.execute_query("SELECT COUNT(*) as count FROM flight")
            if result:
                count = result[0]['count']
                print(f"✅ Database query successful! Found {count} flights in database.")
                return True
        else:
            print("❌ Database connection failed!")
            print("   Check your .env file credentials")
            return False
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def run_streamlit():
    """Run Streamlit application"""
    os.system("streamlit run app.py")

def main():
    print("=" * 60)
    print("  ✈️  AIRLINE CHATBOT - QUICK START")
    print("=" * 60)
    
    # Step 1: Check Python
    if not check_python_version():
        return
    
    # Step 2: Check dependencies
    print("\n📦 Checking dependencies...")
    if not check_dependencies():
        print("\n💡 Run: pip install -r requirements.txt")
        return
    
    # Step 3: Test database
    if not test_database_connection():
        print("\n💡 Configure .env file with correct MySQL credentials")
        return
    
    # Step 4: Start chatbot
    print("\n" + "=" * 60)
    print("  🚀 Starting Airline Chatbot...")
    print("=" * 60)
    print("\n📱 Opening browser at http://localhost:8501")
    print("💡 Press Ctrl+C to stop\n")
    
    run_streamlit()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\n\n👋 Chatbot stopped. Goodbye!")
