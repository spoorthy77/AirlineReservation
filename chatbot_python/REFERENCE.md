# 📚 Python Chatbot - Complete Reference Guide

## 🎯 What Was Created

A **production-ready Python airline chatbot** with:
- ✅ Streamlit web interface
- ✅ Rule-based NLP (no API costs)
- ✅ MySQL database integration
- ✅ Real-time flight/booking queries
- ✅ Formatted responses with emojis
- ✅ 8+ supported query types
- ✅ Security best practices
- ✅ Error handling

---

## 📂 Complete File Overview

### Core Application Files

**app.py** - Main Streamlit application
- Web UI with chat interface
- Message history tracking
- Database status display
- NLP processing pipeline
- Real-time response formatting

**db_connection.py** - Database module
- MySQL connection management
- Connection pooling
- Query execution (SELECT)
- Update execution (INSERT/UPDATE/DELETE)
- Error handling and logging

**nlp_engine.py** - Natural language processing
- Intent detection
- Entity extraction (airports, PNR, Aadhaar, dates, prices)
- SQL query generation
- 8 intent handlers
- Pattern matching

**response_formatter.py** - Response formatting
- Flight results → readable format
- Booking status → card format
- Payment info → transaction list
- Airline list → rating display
- Generic table formatting

**custom_queries.py** - Advanced queries (optional)
- Upcoming flights
- Premium airlines
- Route analytics
- Customer lookup
- Monthly revenue reports
- Monthly revenue
- And more...

### Configuration Files

**.env** - Credentials (EDIT THIS!)
```
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=password
MYSQL_DATABASE=airline_db
MYSQL_PORT=3306
```

**requirements.txt** - Python dependencies
```
streamlit==1.28.1
mysql-connector-python==8.2.0
pandas==2.1.3
python-dotenv==1.0.0
requests==2.31.0
```

### Utility Files

**quick_start.py** - Setup verification
- Python version check
- Dependency validation
- Database connection test
- Auto-launch app

### Documentation

**README.md** - Features & usage
**SETUP_GUIDE.md** - Detailed setup
**WELCOME.md** - Quick overview
**REFERENCE.md** - This file

---

## 🚀 Complete Setup Steps

### 1. Navigate to chatbot folder
```powershell
cd c:\Users\m6793\OneDrive\Pictures\Documents\NetBeansProjects\AirlineReservation\chatbot_python
```

### 2. Create & activate virtual environment
```powershell
# Create
python -m venv venv

# Activate
venv\Scripts\activate
```

### 3. Install dependencies
```powershell
pip install -r requirements.txt
```

### 4. Configure database credentials
Edit `.env` file:
```
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_DATABASE=airline_db
MYSQL_PORT=3306
```

### 5. Verify setup
```powershell
python quick_start.py
```

### 6. Run chatbot
```powershell
streamlit run app.py
```

### 7. Open browser
- Automatically opens at http://localhost:8501
- Or manually visit http://localhost:8501

---

## 💬 Query Examples by Category

### ✈️ Flight Search

```
"Show flights from Delhi to Mumbai"
→ Intent: search_flights
→ Extracts: source=DEL, destination=BOM

"Find flights from BLR to HYD under 3000"
→ Intent: search_flights
→ Extracts: source=BLR, destination=HYD, price=3000

"Indigo flights from Chennai to Goa"
→ Intent: search_flights
→ Extracts: airline=Indigo, source=MAA, destination=GOI
```

### 📋 Booking Status

```
"Check my booking status for PNR 9XYZ12"
→ Intent: check_booking
→ Extracts: pnr=9XYZ12

"Show booking for ABC1234"
→ Intent: check_booking
→ Extracts: pnr=ABC1234
```

### 🎫 Boarding Pass

```
"Show my boarding pass for PNR 9XYZ12"
→ Intent: show_boarding_pass
→ Extracts: pnr=9XYZ12

"Boarding pass for ABC1234"
→ Intent: show_boarding_pass
→ Extracts: pnr=ABC1234
```

### 💳 Payments

```
"Show my payment status for PNR ABC1234"
→ Intent: show_payments
→ Extracts: pnr=ABC1234

"Payment history"
→ Intent: show_payments
→ Returns: All recent payments
```

### 🏢 Airlines

```
"List all airlines"
→ Intent: list_airlines

"Airlines with high rating"
→ Intent: list_airlines
→ Returns: Airlines sorted by rating
```

---

## 🧠 NLP Processing Flow

```
User Query Input
        ↓
1. Intent Detection
   - Pattern matching on keywords
   - Maps to 8 supported intents
        ↓
2. Entity Extraction
   - Airport codes (3-letter)
   - PNR codes (7 alphanumeric)
   - Aadhaar numbers (12 digits)
   - Dates (YYYY-MM-DD)
   - Prices (₹ or $ format)
   - Flight class (Economy/Business)
   - Airline names
        ↓
3. SQL Generation
   - Creates parameterized SQL
   - Prevents SQL injection
   - Handles multiple conditions
        ↓
4. Database Query
   - Connects to MySQL
   - Executes query
   - Returns results
        ↓
5. Response Formatting
   - Converts data to readable format
   - Adds emojis and styling
   - Formats as Markdown
        ↓
6. Chat Display
   - Shows in Streamlit chat
   - Adds to message history
```

---

## 🗄️ Database Schema

Your chatbot works with these tables:

### airlines
```
- id (PK)
- airline_name
- rating
- created_at
```

### flight
```
- id (PK)
- flight_code (UNIQUE)
- flight_name
- source
- destination
- airline_id (FK)
- departure_time
- arrival_time
- price
- seats_available
- total_seats
- created_at
```

### booking
```
- id (PK)
- pnr (UNIQUE) ⭐
- username
- flight_code (FK)
- booking_date
- date_of_travel
- class
- aadhaar
- status
```

### ticket
```
- id (PK)
- pnr (FK)
- customer_aadhar
- customer_name
- flight_code (FK)
- date_of_travel
- booking_date
```

### boarding_pass
```
- id (PK)
- pnr (UNIQUE, FK)
- passenger_name
- flight_code
- boarding_time
- gate_number
- seat_number
- created_at
```

### payments
```
- id (PK)
- pnr (FK)
- amount
- payment_method
- payment_status
- transaction_date
```

### customer
```
- customer_id (PK)
- name
- nationality
- phone
- address
- aadhar_no
- gender
```

### users
```
- id (PK)
- username
- password
- role
```

---

## 🔧 Customization Guide

### Add a New Intent

**In nlp_engine.py:**

```python
# Step 1: Add method
def my_custom_query(self, query, entities):
    sql = """
    SELECT * FROM table_name WHERE condition = ?
    """
    return sql

# Step 2: Register in __init__
self.intents["my_intent"] = self.my_custom_query

# Step 3: Add to detect_intent
if any(keyword in query_lower for keyword in ["keyword1", "keyword2"]):
    return "my_intent"

# Step 4: Add formatter (optional)
# In response_formatter.py
elif intent == "my_intent":
    return ResponseFormatter.format_my_results(results)
```

### Add a Custom Response Formatter

**In response_formatter.py:**

```python
@staticmethod
def format_my_results(results):
    """Format my custom results"""
    if not results:
        return "❌ No results found."
    
    response = "📋 **My Custom Results:**\n\n"
    for row in results:
        response += f"• {row['column1']} - {row['column2']}\n"
    
    return response
```

### Add Advanced Queries

**Use custom_queries.py:**

```python
@staticmethod
def my_advanced_query():
    """My custom query"""
    sql = """
    SELECT ... FROM ...
    """
    results = DatabaseConnection.execute_query(sql)
    return ResponseFormatter.format_my_results(results)
```

---

## 🔒 Security Best Practices

✅ **Parameterized Queries**
- Prevents SQL injection
- Used in db_connection.py

✅ **Environment Variables**
- Credentials in .env
- Not hardcoded in source

✅ **Error Messages**
- No sensitive info exposed
- User-friendly error text

✅ **Connection Management**
- Proper connection closing
- Exception handling

✅ **Input Validation**
- Entity extraction validates format
- Regex patterns prevent malicious input

---

## 🐛 Debugging

### Enable Detailed Logging

**In app.py, add:**
```python
import logging
logging.basicConfig(level=logging.DEBUG)
```

### Check Database Connection

```powershell
python -c "from db_connection import DatabaseConnection; print(DatabaseConnection.execute_query('SELECT 1'))"
```

### Test NLP Engine

```powershell
python nlp_engine.py
```

### View SQL Queries

**In db_connection.py, add:**
```python
print(f"Executing: {query}")
```

### Check Streamlit Logs

```
Terminal output from `streamlit run app.py`
Shows all errors and debug info
```

---

## 📊 Performance Tips

1. **Database Indexing**
   - Add indexes on frequently queried columns
   - e.g., flight_code, pnr, username, aadhaar

2. **Query Optimization**
   - Use JOINs instead of multiple queries
   - Limit results (already done)
   - Use WHERE conditions

3. **Caching** (Advanced)
   ```python
   @st.cache_data
   def get_flights():
       return DatabaseConnection.execute_query("...")
   ```

4. **Connection Pooling**
   - Already implemented in db_connection.py
   - Get new connection for each query

---

## 🚀 Deployment Checklist

- [ ] All .env variables configured
- [ ] MySQL server accessible
- [ ] Database airline_db exists
- [ ] All tables created
- [ ] Test data inserted
- [ ] `streamlit run app.py` works locally
- [ ] All queries tested
- [ ] Error messages checked
- [ ] Performance acceptable
- [ ] Ready for production

---

## 📞 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "ModuleNotFoundError" | `pip install -r requirements.txt` |
| "MySQL connection failed" | Check .env credentials, MySQL running |
| "Unknown column error" | Verify database schema matches |
| "No results found" | Check if data exists in database |
| "Streamlit not responding" | Kill process (Ctrl+C), restart |
| "Port 8501 already in use" | Change: `streamlit run app.py --server.port 8502` |

---

## 🎓 Learning Resources

### Inside the Chatbot
- **nlp_engine.py** - Learn NLP pattern matching
- **db_connection.py** - Learn MySQL integration
- **response_formatter.py** - Learn data formatting
- **app.py** - Learn Streamlit development

### External Resources
- Streamlit Docs: https://docs.streamlit.io
- MySQL Docs: https://dev.mysql.com/doc
- Python Docs: https://docs.python.org

---

## ✨ Quick Command Reference

```powershell
# Setup
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt

# Run
streamlit run app.py

# Test
python quick_start.py
python nlp_engine.py

# Debug
python db_connection.py
python custom_queries.py

# Clean
deactivate
# Then delete venv folder
```

---

## 🎉 You're All Set!

Everything is configured and ready to use:
1. ✅ Core chatbot application
2. ✅ Database connection module
3. ✅ NLP engine with 8 intents
4. ✅ Response formatter
5. ✅ Custom queries library
6. ✅ Setup scripts
7. ✅ Complete documentation

**Start chatting:** `streamlit run app.py`

**Questions?** Check SETUP_GUIDE.md or README.md

---

**Made with ❤️ for your Airline Reservation System**
