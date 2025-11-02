# 🎉 Python Airline Chatbot - Complete Package

## 📦 What You Got

I've created a **complete Python-based airline chatbot** with Streamlit frontend connected to your MySQL `airline_db` database!

### ✨ Key Features

✅ **Natural Language Understanding** - Asks for flights, bookings, payments in plain English
✅ **Rule-Based NLP** - No expensive API calls needed (free!)
✅ **MySQL Integration** - Real-time data from your airline database
✅ **Beautiful UI** - Streamlit web interface with chat history
✅ **Formatted Responses** - Readable, formatted data tables and cards
✅ **Multi-Intent Support** - Handles 8+ different query types
✅ **Error Handling** - Graceful error messages and suggestions

---

## 🗂️ File Structure

```
chatbot_python/
├── app.py                   # Main Streamlit chatbot app
├── db_connection.py         # MySQL connection module
├── nlp_engine.py           # Natural language processor
├── response_formatter.py    # Format SQL results
├── quick_start.py          # Setup verification script
├── requirements.txt        # Python dependencies
├── .env                    # Database credentials (EDIT THIS!)
├── README.md              # Full documentation
└── SETUP_GUIDE.md         # Setup instructions
```

---

## ⚡ Quick Start (3 Steps)

### Step 1: Setup Environment
```powershell
cd chatbot_python
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
```

### Step 2: Configure Database
Edit `.env` with your MySQL credentials:
```
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_DATABASE=airline_db
MYSQL_PORT=3306
```

### Step 3: Run Chatbot
```powershell
streamlit run app.py
```

✅ Open browser at **http://localhost:8501**

---

## 💬 Example Conversations

### Query 1: Search Flights
```
You: "Show me flights from Delhi to Mumbai"

Bot: ✈️ Available Flights:
   1. AI302 - Air India
      📍 DEL → BOM
      🕐 08:30 → 10:30
      💰 ₹4500
      💺 45 seats available
```

### Query 2: Check Booking
```
You: "Check my booking status for PNR 9XYZ12"

Bot: 📋 Booking Status:
   PNR: 9XYZ12
   ✈️ Flight: AI302
   📅 Travel Date: 2025-11-01
   💺 Class: Economy
   🔔 Status: Confirmed
```

### Query 3: Payment Info
```
You: "Show payment status for PNR ABC1234"

Bot: 💳 Payment History:
   ✅ Amount: ₹4500
   Method: Credit Card
   Status: Success
   Date: 2025-11-01
```

---

## 🎯 Supported Intents

| Intent | Example Query | What It Does |
|--------|--------------|-------------|
| `search_flights` | "Flights from DEL to BOM" | Find available flights |
| `check_booking` | "Check booking 9XYZ12" | Get booking details |
| `show_boarding_pass` | "Boarding pass for ABC1234" | Display boarding pass |
| `show_payments` | "Payment status for XYZ9876" | Show payment history |
| `list_airlines` | "List all airlines" | Show all airlines |
| `get_flight_details` | "Flight details DEL to BOM" | Get flight information |
| `show_customers` | "Show customers" | List customer data |
| `book_flight` | "Book flight with Aadhaar 123456789012" | Start booking process |

---

## 🧠 How It Works

```
User Input
    ↓
NLP Engine (Detect Intent & Extract Entities)
    ↓
Generate SQL Query
    ↓
Execute on MySQL Database
    ↓
Format Results
    ↓
Display in Chat
```

### Example Flow

```
Input: "Show flights from DEL to BOM under 5000"

1. Intent Detection → search_flights
2. Entity Extraction → {airports: ['DEL', 'BOM'], price: 5000}
3. SQL Generation → "SELECT * FROM flight WHERE source='DEL' AND destination='BOM' AND price <= 5000"
4. Query Execution → [Flight records from database]
5. Formatting → Beautiful flight cards
6. Display → Chat message with flights
```

---

## 🔒 Security Features

✅ **Parameterized Queries** - Prevents SQL injection
✅ **Credential Management** - Secrets in .env (not in code)
✅ **Connection Pooling** - Efficient DB access
✅ **Error Handling** - No sensitive info in error messages
✅ **Input Validation** - Entity extraction validates data

---

## 🚀 Testing the Setup

### Method 1: Auto-Test Script
```powershell
python quick_start.py
```

This will:
- ✅ Check Python version
- ✅ Verify packages installed
- ✅ Test database connection
- ✅ Count flights in database
- ✅ Start Streamlit app

### Method 2: Manual Test
```powershell
# Test database
python -c "from db_connection import DatabaseConnection; print(DatabaseConnection.execute_query('SELECT COUNT(*) FROM flight'))"

# Test NLP
python nlp_engine.py
```

---

## 🆘 Troubleshooting

### ❌ "ModuleNotFoundError"
```powershell
pip install -r requirements.txt
```

### ❌ "MySQL connection refused"
1. Check if MySQL is running
2. Verify credentials in `.env`
3. Test: `mysql -u root -p`

### ❌ "Unknown column errors"
This is the Java chatbot issue. Python chatbot has correct schema.

### ❌ "Streamlit command not found"
```powershell
pip install streamlit==1.28.1
```

---

## 📈 Next Steps / Enhancements

### Easy Additions
- [ ] Add more query types to `nlp_engine.py`
- [ ] Customize UI colors in `app.py`
- [ ] Add emoji reactions in chat
- [ ] Export chat history to CSV

### Medium Effort
- [ ] Add voice input (speech_recognition)
- [ ] Generate PNR QR codes
- [ ] Add email notifications
- [ ] User authentication

### Advanced
- [ ] Integrate OpenAI/Gemini for better NLP
- [ ] Add multi-language support
- [ ] Build admin dashboard
- [ ] Deploy to cloud (Streamlit Cloud, AWS, etc.)

---

## 🌐 Deployment

### Deploy to Streamlit Cloud (FREE)
1. Push code to GitHub
2. Visit streamlit.io/cloud
3. Connect GitHub repo
4. Add `.env` secrets in dashboard
5. Deploy! ✅

### Deploy to Your Server
```bash
# Install Python 3.9+
# Clone repo
# Setup .env
# Run: streamlit run app.py --server.port 80 --server.address 0.0.0.0
```

---

## 📊 Database Schema Check

Verify your database has these tables:
```sql
USE airline_db;

SHOW TABLES;
-- Should show:
-- airlines
-- boarding_pass
-- booking
-- customer
-- flight
-- payments
-- ticket
-- users
```

---

## 💡 Usage Tips

1. **Be Specific** - "Flights from DEL to BOM" works better than "I need a flight"
2. **Use Codes** - Airport codes (DEL, BOM, BLR, etc.)
3. **PNRs** - Format like "9XYZ12" (7 chars)
4. **Dates** - Format like "2025-11-01"
5. **Prices** - Use format "5000" without currency

---

## 📞 Support Resources

| Resource | Location |
|----------|----------|
| Setup Instructions | SETUP_GUIDE.md |
| Full Documentation | README.md |
| NLP Examples | nlp_engine.py (bottom) |
| Database Config | .env file |
| Database Queries | db_connection.py |
| Response Formatting | response_formatter.py |

---

## ✅ Verification Checklist

Before first use:
- [ ] Python 3.8+ installed
- [ ] Virtual environment created and activated
- [ ] `pip install -r requirements.txt` successful
- [ ] `.env` file configured with your MySQL credentials
- [ ] MySQL server is running
- [ ] `airline_db` database exists
- [ ] Quick start test passed: `python quick_start.py`
- [ ] Streamlit app launches: `streamlit run app.py`
- [ ] Can type messages in chat interface
- [ ] Chatbot responds with data

---

## 🎉 You're All Set!

Your airline chatbot is ready to use! 

**Next:** 
1. Configure `.env` with your MySQL credentials
2. Run `streamlit run app.py`
3. Ask it questions about flights and bookings
4. Enjoy the magic! ✨

---

**Questions?**
- Check SETUP_GUIDE.md for detailed setup
- Check README.md for features
- Review nlp_engine.py for query examples
- Check error messages in terminal

**Made with ❤️ for the Airline Reservation System**

🚀 Happy Chatting!
