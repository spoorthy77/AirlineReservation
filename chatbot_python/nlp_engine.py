"""
NLP Engine for converting user queries to SQL
Uses rule-based pattern matching and intent detection
"""
import re
from datetime import datetime, timedelta

class NLPEngine:
    """Converts natural language queries to SQL"""
    
    def __init__(self):
        self.intents = {
            "search_flights": self.search_flights,
            "check_booking": self.check_booking,
            "show_boarding_pass": self.show_boarding_pass,
            "show_payments": self.show_payments,
            "list_airlines": self.list_airlines,
            "book_flight": self.book_flight,
            "show_customers": self.show_customers,
            "get_flight_details": self.get_flight_details,
        }
    
    def detect_intent(self, user_query):
        """Detect user's intent from query"""
        query_lower = user_query.lower()
        
        if any(keyword in query_lower for keyword in ["search", "find", "show", "available", "flights", "from", "to"]):
            return "search_flights"
        
        if any(keyword in query_lower for keyword in ["booking", "status", "pnr", "check", "my booking"]):
            return "check_booking"
        
        if any(keyword in query_lower for keyword in ["boarding", "pass", "gate", "seat"]):
            return "show_boarding_pass"
        
        if any(keyword in query_lower for keyword in ["payment", "paid", "transaction", "invoice"]):
            return "show_payments"
        
        if any(keyword in query_lower for keyword in ["airline", "rating", "all airlines"]):
            return "list_airlines"
        
        if any(keyword in query_lower for keyword in ["book", "reserve", "ticket", "aadhaar"]):
            return "book_flight"
        
        if any(keyword in query_lower for keyword in ["customer", "user", "passenger", "list"]):
            return "show_customers"
        
        if any(keyword in query_lower for keyword in ["flight", "detail", "information"]):
            return "get_flight_details"
        
        return None
    
    def extract_entities(self, query):
        """Extract key entities from query"""
        entities = {}
        
        # Extract airport codes (3-letter codes)
        airports = re.findall(r'\b([A-Z]{3})\b', query.upper())
        if airports:
            entities['airports'] = airports
        
        # Extract PNR (typically 7 alphanumeric characters)
        pnr = re.search(r'\b([A-Z0-9]{7})\b', query.upper())
        if pnr:
            entities['pnr'] = pnr.group(1)
        
        # Extract Aadhaar (12 digits)
        aadhaar = re.search(r'\b(\d{12})\b', query)
        if aadhaar:
            entities['aadhaar'] = aadhaar.group(1)
        
        # Extract dates
        date_pattern = r'\b(\d{4}-\d{2}-\d{2}|\d{1,2}[-/]\d{1,2}[-/]\d{4})\b'
        dates = re.findall(date_pattern, query)
        if dates:
            entities['dates'] = dates
        
        # Extract price
        price = re.search(r'[₹\$](\d+(?:,\d{3})*(?:\.\d{2})?)', query)
        if price:
            entities['price'] = price.group(1)
        
        # Extract class (Economy, Business, etc.)
        if 'economy' in query.lower():
            entities['class'] = 'Economy'
        elif 'business' in query.lower():
            entities['class'] = 'Business'
        elif 'premium' in query.lower():
            entities['class'] = 'Premium'
        
        # Extract airline name
        airlines = ['indigo', 'air india', 'spicejet', 'goair', 'vistara', 'emirates', 'united']
        for airline in airlines:
            if airline in query.lower():
                entities['airline'] = airline.title()
        
        return entities
    
    def search_flights(self, query, entities):
        """Generate SQL for flight search"""
        conditions = []
        
        if 'airports' in entities and len(entities['airports']) >= 2:
            source, destination = entities['airports'][0], entities['airports'][1]
            conditions.append(f"f.source = '{source}' AND f.destination = '{destination}'")
        
        if 'price' in entities:
            price_limit = entities['price'].replace(',', '')
            conditions.append(f"f.price <= {price_limit}")
        
        if 'airline' in entities:
            conditions.append(f"a.airline_name LIKE '%{entities['airline']}%'")
        
        where_clause = " AND ".join(conditions) if conditions else "1=1"
        
        sql = f"""
        SELECT 
            f.flight_code, f.flight_name, a.airline_name, f.source, f.destination,
            f.departure_time, f.arrival_time, f.price, f.seats_available
        FROM flight f
        JOIN airlines a ON f.airline_id = a.id
        WHERE {where_clause}
        ORDER BY f.price ASC
        LIMIT 10
        """
        return sql
    
    def check_booking(self, query, entities):
        """Generate SQL for booking status check"""
        if 'pnr' not in entities:
            return None
        
        sql = f"""
        SELECT 
            b.pnr, b.username, b.flight_code, b.booking_date, b.date_of_travel,
            b.class, b.status, f.flight_name, f.source, f.destination
        FROM booking b
        JOIN flight f ON b.flight_code = f.flight_code
        WHERE b.pnr = '{entities['pnr']}'
        """
        return sql
    
    def show_boarding_pass(self, query, entities):
        """Generate SQL for boarding pass"""
        if 'pnr' not in entities:
            return None
        
        sql = f"""
        SELECT 
            bp.pnr, bp.passenger_name, bp.flight_code, bp.boarding_time,
            bp.gate_number, bp.seat_number, bp.created_at
        FROM boarding_pass bp
        WHERE bp.pnr = '{entities['pnr']}'
        """
        return sql
    
    def show_payments(self, query, entities):
        """Generate SQL for payment information"""
        if 'pnr' in entities:
            sql = f"""
            SELECT 
                p.payment_id, p.pnr, p.amount, p.payment_method,
                p.payment_status, p.transaction_date
            FROM payments p
            WHERE p.pnr = '{entities['pnr']}'
            """
        else:
            sql = """
            SELECT 
                p.payment_id, p.pnr, p.amount, p.payment_method,
                p.payment_status, p.transaction_date
            FROM payments p
            ORDER BY p.transaction_date DESC
            LIMIT 20
            """
        return sql
    
    def list_airlines(self, query, entities):
        """Generate SQL to list airlines"""
        sql = """
        SELECT 
            airline_name, rating, id
        FROM airlines
        ORDER BY rating DESC
        """
        return sql
    
    def book_flight(self, query, entities):
        """Generate SQL for flight booking"""
        # This would typically return an instruction rather than a query
        return {
            "action": "book_flight",
            "extracted": entities,
            "message": "Booking requires multiple steps. Please provide: Flight Code, Aadhaar, Travel Date, and Class."
        }
    
    def show_customers(self, query, entities):
        """Generate SQL to show customers"""
        sql = """
        SELECT 
            customer_id, name, nationality, phone, aadhar_no, gender
        FROM customer
        LIMIT 20
        """
        return sql
    
    def get_flight_details(self, query, entities):
        """Generate SQL for flight details"""
        if 'airports' in entities and len(entities['airports']) >= 2:
            source, destination = entities['airports'][0], entities['airports'][1]
            sql = f"""
            SELECT 
                f.flight_code, f.flight_name, a.airline_name, f.source, f.destination,
                f.departure_time, f.arrival_time, f.price, f.seats_available, f.total_seats
            FROM flight f
            JOIN airlines a ON f.airline_id = a.id
            WHERE f.source = '{source}' AND f.destination = '{destination}'
            ORDER BY f.departure_time ASC
            """
            return sql
        return None
    
    def process_query(self, user_query):
        """Main method to process user query"""
        intent = self.detect_intent(user_query)
        entities = self.extract_entities(user_query)
        
        if intent and intent in self.intents:
            handler = self.intents[intent]
            sql = handler(user_query, entities)
            return {
                "intent": intent,
                "entities": entities,
                "sql": sql,
                "success": True
            }
        
        return {
            "intent": None,
            "entities": entities,
            "sql": None,
            "success": False,
            "message": "❌ Could not understand your query. Please try:\n\n- 'Show flights from DEL to BOM'\n- 'Check booking status for PNR ABC1234'\n- 'Show boarding pass for XYZ9876'"
        }


if __name__ == "__main__":
    nlp = NLPEngine()
    test_queries = [
        "Show me flights from DEL to BOM",
        "Check my booking status for PNR 9XYZ12",
        "List all airlines",
        "Find flights under 5000",
    ]
    
    for query in test_queries:
        result = nlp.process_query(query)
        print(f"\n📝 Query: {query}")
        print(f"🎯 Intent: {result['intent']}")
        print(f"🔍 Entities: {result['entities']}")
        print(f"💾 SQL: {result['sql']}")
