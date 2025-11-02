"""
Chatbot Response Formatter - Converts SQL results to readable responses
"""
import pandas as pd
from datetime import datetime

class ResponseFormatter:
    """Formats database results into readable chatbot responses"""
    
    @staticmethod
    def format_flights(results):
        """Format flight search results"""
        if not results:
            return "❌ No flights found matching your criteria."
        
        response = "✈️ **Available Flights:**\n\n"
        for i, flight in enumerate(results, 1):
            response += f"""
**{i}. {flight['flight_code']} - {flight['flight_name']}**
   🏢 Airline: {flight['airline_name']}
   📍 Route: {flight['source']} → {flight['destination']}
   🕐 Departure: {flight['departure_time']} | Arrival: {flight['arrival_time']}
   💰 Price: ₹{flight['price']}
   💺 Seats Available: {flight['seats_available']}
"""
        return response
    
    @staticmethod
    def format_booking_status(results):
        """Format booking status"""
        if not results:
            return "❌ Booking not found."
        
        booking = results[0]
        response = f"""
📋 **Booking Status:**
   PNR: {booking['pnr']}
   User: {booking['username']}
   ✈️ Flight: {booking['flight_code']} ({booking['flight_name']})
   📅 Travel Date: {booking['date_of_travel']}
   🕐 Booked: {booking['booking_date']}
   💺 Class: {booking['class']}
   🔔 Status: {booking['status']}
   🗺️ Route: {booking['source']} → {booking['destination']}
"""
        return response
    
    @staticmethod
    def format_boarding_pass(results):
        """Format boarding pass details"""
        if not results:
            return "❌ Boarding pass not found."
        
        bp = results[0]
        response = f"""
🎫 **Boarding Pass:**
   PNR: {bp['pnr']}
   Passenger: {bp['passenger_name']}
   ✈️ Flight: {bp['flight_code']}
   🪑 Seat: {bp['seat_number']}
   🚪 Gate: {bp['gate_number']}
   🕐 Boarding Time: {bp['boarding_time']}
   📅 Created: {bp['created_at']}
"""
        return response
    
    @staticmethod
    def format_payments(results):
        """Format payment information"""
        if not results:
            return "❌ No payments found."
        
        response = "💳 **Payment History:**\n\n"
        total_amount = 0
        
        for payment in results:
            status_emoji = "✅" if payment['payment_status'].lower() == 'success' else "⏳"
            response += f"""
{status_emoji} PNR: {payment['pnr']}
   Amount: ₹{payment['amount']}
   Method: {payment['payment_method']}
   Status: {payment['payment_status']}
   Date: {payment['transaction_date']}

"""
            if payment['payment_status'].lower() == 'success':
                total_amount += payment['amount']
        
        response += f"\n💰 **Total Paid:** ₹{total_amount}"
        return response
    
    @staticmethod
    def format_airlines(results):
        """Format airline list"""
        if not results:
            return "❌ No airlines found."
        
        response = "🏢 **Airlines:**\n\n"
        for airline in results:
            stars = "⭐" * int(airline['rating']) if airline['rating'] else "No rating"
            response += f"• {airline['airline_name']} - Rating: {stars} ({airline['rating']})\n"
        
        return response
    
    @staticmethod
    def format_customers(results):
        """Format customer list"""
        if not results:
            return "❌ No customers found."
        
        response = "👥 **Customers:**\n\n"
        for customer in results:
            response += f"""
• {customer['name']} ({customer['customer_id']})
   Nationality: {customer['nationality']}
   Phone: {customer['phone']}
   Aadhaar: {customer['aadhar_no']}
   Gender: {customer['gender']}

"""
        return response
    
    @staticmethod
    def format_generic_table(results, columns=None):
        """Format generic SQL results as a table"""
        if not results:
            return "❌ No results found."
        
        df = pd.DataFrame(results)
        
        # Limit columns if specified
        if columns:
            df = df[[col for col in columns if col in df.columns]]
        
        return df.to_string()
    
    @staticmethod
    def format_response(intent, results):
        """Main formatter method"""
        if intent == "search_flights":
            return ResponseFormatter.format_flights(results)
        elif intent == "check_booking":
            return ResponseFormatter.format_booking_status(results)
        elif intent == "show_boarding_pass":
            return ResponseFormatter.format_boarding_pass(results)
        elif intent == "show_payments":
            return ResponseFormatter.format_payments(results)
        elif intent == "list_airlines":
            return ResponseFormatter.format_airlines(results)
        elif intent == "show_customers":
            return ResponseFormatter.format_customers(results)
        else:
            return ResponseFormatter.format_generic_table(results)


if __name__ == "__main__":
    # Test formatting
    test_flights = [
        {
            'flight_code': 'AI302',
            'flight_name': 'Air India',
            'airline_name': 'Air India',
            'source': 'DEL',
            'destination': 'BOM',
            'departure_time': '2025-11-01 08:30:00',
            'arrival_time': '2025-11-01 10:30:00',
            'price': 4500,
            'seats_available': 45
        }
    ]
    
    print(ResponseFormatter.format_flights(test_flights))
