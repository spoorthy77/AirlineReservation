"""
Custom Queries Module - Add your own query handlers here
"""
from db_connection import DatabaseConnection
from response_formatter import ResponseFormatter

class CustomQueries:
    """Custom SQL queries for specific business logic"""
    
    @staticmethod
    def get_upcoming_flights(days=7):
        """Get flights departing in the next N days"""
        sql = f"""
        SELECT 
            f.flight_code, f.flight_name, a.airline_name,
            f.source, f.destination, f.departure_time, f.price,
            f.seats_available
        FROM flight f
        JOIN airlines a ON f.airline_id = a.id
        WHERE DATE(f.departure_time) <= DATE_ADD(CURDATE(), INTERVAL {days} DAY)
        AND DATE(f.departure_time) >= CURDATE()
        ORDER BY f.departure_time ASC
        LIMIT 20
        """
        results = DatabaseConnection.execute_query(sql)
        return ResponseFormatter.format_flights(results) if results else "❌ No upcoming flights"
    
    @staticmethod
    def get_premium_airlines(min_rating=4.0):
        """Get highly-rated airlines"""
        sql = f"""
        SELECT airline_name, rating, id
        FROM airlines
        WHERE rating >= {min_rating}
        ORDER BY rating DESC
        """
        results = DatabaseConnection.execute_query(sql)
        return ResponseFormatter.format_airlines(results) if results else "❌ No premium airlines found"
    
    @staticmethod
    def get_available_flights_today():
        """Get all flights departing today"""
        sql = """
        SELECT 
            f.flight_code, f.flight_name, a.airline_name,
            f.source, f.destination, f.departure_time, f.arrival_time,
            f.price, f.seats_available
        FROM flight f
        JOIN airlines a ON f.airline_id = a.id
        WHERE DATE(f.departure_time) = CURDATE()
        ORDER BY f.departure_time ASC
        """
        results = DatabaseConnection.execute_query(sql)
        return ResponseFormatter.format_flights(results) if results else "❌ No flights today"
    
    @staticmethod
    def get_cheapest_flights(source, destination, limit=5):
        """Get cheapest flights on a route"""
        sql = f"""
        SELECT 
            f.flight_code, f.flight_name, a.airline_name,
            f.source, f.destination, f.departure_time, f.arrival_time,
            f.price, f.seats_available
        FROM flight f
        JOIN airlines a ON f.airline_id = a.id
        WHERE f.source = '{source}' AND f.destination = '{destination}'
        ORDER BY f.price ASC
        LIMIT {limit}
        """
        results = DatabaseConnection.execute_query(sql)
        return ResponseFormatter.format_flights(results) if results else f"❌ No flights from {source} to {destination}"
    
    @staticmethod
    def get_user_bookings(username):
        """Get all bookings for a user"""
        sql = f"""
        SELECT 
            b.pnr, b.flight_code, b.booking_date, b.date_of_travel,
            b.class, b.status, f.flight_name, f.source, f.destination
        FROM booking b
        JOIN flight f ON b.flight_code = f.flight_code
        WHERE b.username = '{username}'
        ORDER BY b.date_of_travel DESC
        """
        results = DatabaseConnection.execute_query(sql)
        if results:
            response = f"📋 **Bookings for {username}:**\n\n"
            for booking in results:
                response += f"""
**PNR: {booking['pnr']}**
   ✈️ Flight: {booking['flight_code']} ({booking['flight_name']})
   Route: {booking['source']} → {booking['destination']}
   📅 Travel Date: {booking['date_of_travel']}
   💺 Class: {booking['class']}
   Status: {booking['status']}

"""
            return response
        return f"❌ No bookings found for {username}"
    
    @staticmethod
    def get_flights_by_airline(airline_name):
        """Get all flights for a specific airline"""
        sql = f"""
        SELECT 
            f.flight_code, f.flight_name, f.source, f.destination,
            f.departure_time, f.arrival_time, f.price, f.seats_available
        FROM flight f
        JOIN airlines a ON f.airline_id = a.id
        WHERE a.airline_name LIKE '%{airline_name}%'
        ORDER BY f.departure_time ASC
        LIMIT 15
        """
        results = DatabaseConnection.execute_query(sql)
        return ResponseFormatter.format_flights(results) if results else f"❌ No flights found for {airline_name}"
    
    @staticmethod
    def get_route_analytics(source, destination):
        """Get analytics for a flight route"""
        sql = f"""
        SELECT 
            f.flight_code, a.airline_name, f.price,
            f.seats_available, f.total_seats,
            COUNT(b.id) as bookings
        FROM flight f
        JOIN airlines a ON f.airline_id = a.id
        LEFT JOIN booking b ON f.flight_code = b.flight_code
        WHERE f.source = '{source}' AND f.destination = '{destination}'
        GROUP BY f.flight_code, a.airline_name, f.price, f.seats_available, f.total_seats
        ORDER BY f.price ASC
        """
        results = DatabaseConnection.execute_query(sql)
        if results:
            response = f"📊 **Route Analytics: {source} → {destination}**\n\n"
            total_bookings = 0
            for row in results:
                occupancy = (row['total_seats'] - row['seats_available']) / row['total_seats'] * 100
                response += f"""
🔹 {row['flight_code']} - {row['airline_name']}
   Price: ₹{row['price']}
   Seats: {row['seats_available']}/{row['total_seats']} available
   Occupancy: {occupancy:.1f}%
   Bookings: {row['bookings']}

"""
                total_bookings += row['bookings']
            
            response += f"\n📈 Total Bookings on Route: {total_bookings}"
            return response
        return f"❌ No data for route {source} → {destination}"
    
    @staticmethod
    def get_customer_by_aadhaar(aadhaar):
        """Get customer details by Aadhaar"""
        sql = f"""
        SELECT 
            customer_id, name, nationality, phone, address,
            aadhar_no, gender
        FROM customer
        WHERE aadhar_no = '{aadhaar}'
        """
        results = DatabaseConnection.execute_query(sql)
        if results:
            customer = results[0]
            return f"""
👤 **Customer Details:**
   Name: {customer['name']}
   ID: {customer['customer_id']}
   Nationality: {customer['nationality']}
   Phone: {customer['phone']}
   Address: {customer['address']}
   Aadhaar: {customer['aadhar_no']}
   Gender: {customer['gender']}
"""
        return f"❌ No customer found with Aadhaar: {aadhaar}"
    
    @staticmethod
    def get_monthly_revenue():
        """Get monthly revenue from payments"""
        sql = """
        SELECT 
            DATE_FORMAT(transaction_date, '%Y-%m') as month,
            payment_status,
            COUNT(*) as transactions,
            SUM(amount) as total_amount
        FROM payments
        GROUP BY month, payment_status
        ORDER BY month DESC
        LIMIT 12
        """
        results = DatabaseConnection.execute_query(sql)
        if results:
            response = "💰 **Monthly Revenue Report:**\n\n"
            for row in results:
                response += f"""
📅 {row['month']} - {row['payment_status'].upper()}
   Transactions: {row['transactions']}
   Total: ₹{row['total_amount']}

"""
            return response
        return "❌ No payment data available"


# Example usage
if __name__ == "__main__":
    print("Testing Custom Queries...\n")
    
    # Test 1: Upcoming flights
    print(CustomQueries.get_upcoming_flights(7))
    print("\n" + "="*60 + "\n")
    
    # Test 2: Premium airlines
    print(CustomQueries.get_premium_airlines(4.0))
    print("\n" + "="*60 + "\n")
    
    # Test 3: Today's flights
    print(CustomQueries.get_available_flights_today())
    print("\n" + "="*60 + "\n")
    
    # Test 4: Cheapest flights
    print(CustomQueries.get_cheapest_flights('DEL', 'BOM', 5))
