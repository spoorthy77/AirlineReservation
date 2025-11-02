# ⚡ Quick Start Checklist - Python Airline Chatbot

## ✅ Pre-Flight Check (5 minutes)

### 1. Verify Python Installation
```powershell
python --version
# Should show: Python 3.8 or higher
```
- [ ] Python 3.8+ installed

### 2. Navigate to Chatbot Folder
```powershell
cd c:\Users\m6793\OneDrive\Pictures\Documents\NetBeansProjects\AirlineReservation\chatbot_python
```
- [ ] In correct directory

### 3. Create Virtual Environment
```powershell
python -m venv venv
venv\Scripts\activate
# You should see (venv) in terminal
```
- [ ] Virtual environment created
- [ ] Virtual environment activated (see `(venv)` prefix)

### 4. Install Dependencies
```powershell
pip install -r requirements.txt
# Wait for completion (1-2 minutes)
```
- [ ] All packages installed
- [ ] No error messages

---

## 🔧 Configuration (2 minutes)

### 5. Edit .env File
Open `.env` in text editor and configure:

```
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=your_password_here
MYSQL_DATABASE=airline_db
MYSQL_PORT=3306
```

- [ ] MYSQL_HOST set (usually `localhost`)
- [ ] MYSQL_USER set (usually `root`)
- [ ] MYSQL_PASSWORD set (your MySQL password)
- [ ] MYSQL_DATABASE set to `airline_db`
- [ ] MYSQL_PORT set (usually `3306`)

### 6. Verify MySQL is Running
```powershell
# Windows - Check MySQL service
Get-Service MySQL80
# Should show: "Running" status

# Or try to connect
mysql -u root -p
# Enter your password - should connect successfully
# Type: exit
```

- [ ] MySQL service running
- [ ] Can connect with credentials from .env

---

## 🧪 Verification (2 minutes)

### 7. Test Database Connection
```powershell
python -c "from db_connection import DatabaseConnection; result = DatabaseConnection.execute_query('SELECT COUNT(*) as count FROM flight'); print('✅ Success!' if result else '❌ Failed')"
```

- [ ] Database connection test passes
- [ ] Shows "✅ Success!"

### 8. Run Quick Start Script
```powershell
python quick_start.py
```

Should show:
```
✅ Python 3.x detected
✅ streamlit installed
✅ mysql installed
✅ pandas installed
✅ dotenv installed
✅ Database connection successful!
✅ Database query successful! Found X flights in database.
```

- [ ] All packages verified
- [ ] Database connection successful
- [ ] Flights found in database
- [ ] Auto-launches Streamlit

---

## 🚀 Launch Chatbot (1 minute)

### 9. Start Streamlit App
```powershell
streamlit run app.py
```

Expected output:
```
You can now view your Streamlit app in your browser.

Local URL: http://localhost:8501
Network URL: http://192.168.x.x:8501

Press Ctrl+C to stop
```

- [ ] Streamlit starts without errors
- [ ] Shows "Local URL: http://localhost:8501"

### 10. Open in Browser
- Browser auto-opens at `http://localhost:8501`
- Or manually visit: **http://localhost:8501**

- [ ] Chat interface loads
- [ ] See "✈️ Airline Reservation Chatbot"
- [ ] Message input box visible

---

## 💬 Test Chatbot (2 minutes)

### 11. Try Sample Queries

**Test 1: Search Flights**
```
Type: "Show flights from DEL to BOM"
Expected: See list of flights with prices and times
```
- [ ] Chatbot responds with flights

**Test 2: Check Booking**
```
Type: "Check booking status for PNR 9XYZ12"
Expected: See booking details (if PNR exists in database)
```
- [ ] Chatbot responds with booking info or "not found"

**Test 3: List Airlines**
```
Type: "List all airlines"
Expected: See airline list with ratings
```
- [ ] Chatbot responds with airlines

- [ ] All test queries work
- [ ] Getting reasonable responses

---

## 🎉 Success! (You're Done!)

If all checkboxes are checked:

✅ Python chatbot is **fully functional**
✅ Connected to MySQL **airline_db**
✅ Can query **flights, bookings, payments**
✅ Has **beautiful web interface**
✅ Ready for **production use**

---

## 📚 Next Steps

### 1. Customize Chatbot
- Edit `.env` to change database
- Edit `nlp_engine.py` to add new query types
- Edit `response_formatter.py` to change display format

### 2. Add More Data
```sql
-- Add test flights
INSERT INTO flight (flight_code, flight_name, source, destination, price)
VALUES ('TEST01', 'Test Flight', 'DEL', 'BOM', 5000);
```

### 3. Deploy to Cloud
- See SETUP_GUIDE.md → "Deployment Options"
- Push to GitHub
- Deploy to Streamlit Cloud (free!)

### 4. Learn the Code
- Read `app.py` - Understand Streamlit
- Read `nlp_engine.py` - Understand NLP
- Read `db_connection.py` - Understand SQL
- Read `ARCHITECTURE.md` - Understand flow

---

## 🆘 Troubleshooting

### ❌ "ModuleNotFoundError"
```powershell
pip install -r requirements.txt
```

### ❌ "MySQL connection failed"
- Check `.env` file credentials
- Verify MySQL is running
- Try: `mysql -u root -p`

### ❌ "No module named 'streamlit'"
```powershell
pip install streamlit==1.28.1
```

### ❌ "Port 8501 already in use"
```powershell
streamlit run app.py --server.port 8502
```

### ❌ "Unable to locate package"
```powershell
pip install --upgrade pip
pip install -r requirements.txt
```

### Still having issues?
- Check SETUP_GUIDE.md
- Check REFERENCE.md
- Review terminal error messages
- Verify all .env values are correct

---

## 📞 Files Reference

| File | Purpose |
|------|---------|
| `app.py` | Main chatbot application |
| `db_connection.py` | Database connection module |
| `nlp_engine.py` | Natural language processor |
| `response_formatter.py` | Response formatter |
| `custom_queries.py` | Advanced queries |
| `requirements.txt` | Python dependencies |
| `.env` | Configuration (EDIT THIS!) |
| `quick_start.py` | Setup verification |
| `WELCOME.md` | Welcome guide |
| `SETUP_GUIDE.md` | Detailed setup |
| `REFERENCE.md` | Complete reference |
| `ARCHITECTURE.md` | System architecture |
| `README.md` | Full documentation |

---

## ⏱️ Time Budget

- Setup: 5 minutes
- Configuration: 2 minutes
- Verification: 2 minutes
- Launch: 1 minute
- Testing: 2 minutes
- **Total: ~12 minutes**

---

## 🎯 What You Can Do Now

✅ Search flights by route
✅ Check booking status
✅ View boarding passes
✅ Check payment status
✅ List airlines
✅ Query flight details
✅ Search customers
✅ Add custom queries

---

## 🚀 You're Ready!

### To Start:
```powershell
cd chatbot_python
venv\Scripts\activate
streamlit run app.py
```

### To Stop:
```powershell
Ctrl+C  (in terminal)
```

### To Deactivate:
```powershell
deactivate
```

---

## 💡 Pro Tips

1. **Keep terminal open** - Streamlit runs in background
2. **Browser caches** - Try Ctrl+Shift+R if UI looks wrong
3. **MySQL must run** - Check service before starting
4. **Edit code safely** - Streamlit auto-reloads changes
5. **Check logs** - Terminal shows all errors/debug info

---

## ✨ Final Checklist

Before considering complete:

- [ ] Python 3.8+ installed
- [ ] Virtual environment working
- [ ] Dependencies installed (pip list shows all)
- [ ] MySQL running and accessible
- [ ] `.env` file configured correctly
- [ ] Database connection test passes
- [ ] Streamlit app launches without errors
- [ ] Browser opens to localhost:8501
- [ ] Chat interface visible and functional
- [ ] Test queries return results
- [ ] Can see formatted responses

---

## 🎉 Congratulations!

Your **Python Airline Chatbot** is now:

✅ **INSTALLED** - All files in place
✅ **CONFIGURED** - Database connected
✅ **TESTED** - All components working
✅ **READY** - Available at http://localhost:8501

---

**Questions?** Check the documentation files.
**Problems?** See "Troubleshooting" section above.
**Ready to chat?** Run `streamlit run app.py`

🚀 **Happy Chatting!**

---

*Made with ❤️ for your Airline Reservation System*
