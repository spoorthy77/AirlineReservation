# Airline Chatbot (Python Version)

A conversational AI chatbot built with **Python, Streamlit, and MySQL** for airline reservation queries.

## 🚀 Features

✅ **Natural Language Processing** - Understands user queries about flights, bookings, payments
✅ **SQL Query Generation** - Converts natural language to SQL automatically
✅ **Real-time Database Integration** - Connected to MySQL airline_db
✅ **Formatted Responses** - Beautiful, readable chat responses
✅ **Multi-intent Support** - Handles flights, bookings, boarding passes, payments, airlines
✅ **No API Costs** - Rule-based NLP (no OpenAI/Gemini required)

## 📋 Supported Queries

### Flight Search
```
"Show flights from DEL to BOM"
"Find flights under 5000"
"Flights by Indigo from Chennai to Hyderabad"
```

### Booking Status
```
"Check my booking status for PNR ABC1234"
"Show booking for PNR 9XYZ12"
```

### Boarding Pass
```
"Show my boarding pass for PNR ABC1234"
"Boarding pass details for XYZ9876"
```

### Payment Information
```
"Show my payment status for PNR ABC1234"
"Payment history"
```

### Airlines
```
"List all airlines"
"Airlines with rating above 4"
```

## 🛠️ Installation

### Prerequisites
- Python 3.8+
- MySQL Server running with `airline_db` database
- pip

### Step 1: Create Virtual Environment
```bash
cd chatbot_python
python -m venv venv

# Windows
venv\Scripts\activate

# macOS/Linux
source venv/bin/activate
```

### Step 2: Install Dependencies
```bash
pip install -r requirements.txt
```

### Step 3: Configure Database
Edit `.env` file with your MySQL credentials:
```
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_DATABASE=airline_db
MYSQL_PORT=3306
```

### Step 4: Run Streamlit App
```bash
streamlit run app.py
```

The app will open at `http://localhost:8501`

## 📁 Project Structure

```
chatbot_python/
├── app.py                    # Main Streamlit application
├── db_connection.py          # MySQL connection handler
├── nlp_engine.py            # Natural language processing
├── response_formatter.py     # Format SQL results
├── requirements.txt         # Python dependencies
├── .env                     # Database credentials
└── README.md                # This file
```

## 🗄️ Database Schema

The chatbot works with these tables:

| Table | Purpose |
|-------|---------|
| `airlines` | Airline information & ratings |
| `flight` | Flight details (code, route, price, etc.) |
| `booking` | Passenger bookings |
| `ticket` | Ticket information |
| `boarding_pass` | Boarding pass details |
| `payments` | Payment transactions |
| `customer` | Customer information |
| `users` | User accounts |

## 🧠 NLP Engine

The `NLPEngine` class handles:
- **Intent Detection** - Classifies user queries
- **Entity Extraction** - Pulls out airports, PNRs, Aadhaar, dates, prices
- **SQL Generation** - Creates parameterized SQL queries
- **Pattern Matching** - Uses regex for entity recognition

### Supported Intents
```python
- search_flights       → Find available flights
- check_booking       → Get booking status
- show_boarding_pass  → Display boarding pass
- show_payments       → Payment information
- list_airlines       → All airlines
- book_flight         → New flight booking
- show_customers      → Customer list
- get_flight_details  → Flight information
```

## 📊 Example Usage

### Query 1: Search Flights
```
User: "Show me flights from Delhi to Mumbai under 5000"

→ Intent: search_flights
→ Entities: source=DEL, destination=BOM, price=5000
→ SQL: SELECT * FROM flight WHERE source='DEL' AND destination='BOM' AND price <= 5000

Result:
✈️ Available Flights:
1. AI302 - Air India
   📍 Route: DEL → BOM
   🕐 Departure: 08:30 | Arrival: 10:30
   💰 Price: ₹4500
   💺 Seats: 45
```

### Query 2: Check Booking
```
User: "Check booking status for PNR 9XYZ12"

→ Intent: check_booking
→ Entities: pnr=9XYZ12
→ SQL: SELECT * FROM booking WHERE pnr='9XYZ12'

Result:
📋 Booking Status:
   PNR: 9XYZ12
   ✈️ Flight: AI302
   📅 Travel Date: 2025-11-01
   💺 Class: Economy
   🔔 Status: Confirmed
```

## 🔒 Security

✅ **SQL Injection Prevention** - Uses parameterized queries
✅ **Credential Management** - Secrets stored in .env
✅ **Connection Pooling** - Efficient database access
✅ **Error Handling** - Safe exception management

## 🚀 Deployment

### Deploy on Streamlit Cloud (FREE)
1. Push code to GitHub
2. Go to [Streamlit Cloud](https://streamlit.io/cloud)
3. Create new app → Connect to GitHub repo
4. Set secrets in dashboard (MySQL credentials)

### Deploy on Railway/Render
1. Create requirements.txt
2. Create Procfile: `web: streamlit run app.py`
3. Push to GitHub
4. Connect to Railway/Render

## 📈 Future Enhancements

- [ ] Add OpenAI/Gemini integration for better NLP
- [ ] Voice input using speech_recognition
- [ ] PNR QR code generator
- [ ] Email notifications on bookings
- [ ] Multi-language support
- [ ] User authentication & booking history
- [ ] Admin dashboard
- [ ] Flight recommendation engine

## 🤝 Contributing

Feel free to fork and submit pull requests!

## 📞 Support

For issues or questions, contact: admin@airline.com

---

**Built with ❤️ using Python, Streamlit & MySQL**
