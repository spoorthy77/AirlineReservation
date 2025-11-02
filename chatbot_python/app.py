"""
Main Streamlit Chatbot Application
Airline Reservation Chatbot with MySQL Integration
"""
import streamlit as st
import pandas as pd
from datetime import datetime
from db_connection import DatabaseConnection
from nlp_engine import NLPEngine
from response_formatter import ResponseFormatter

# Page configuration
st.set_page_config(
    page_title="✈️ Airline Chatbot",
    page_icon="🤖",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Custom CSS
st.markdown("""
<style>
    .main {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        padding: 20px;
    }
    .stChatMessage {
        background: #f0f2f6;
        padding: 15px;
        border-radius: 10px;
        margin: 10px 0;
    }
</style>
""", unsafe_allow_html=True)

# Initialize session state
if "messages" not in st.session_state:
    st.session_state.messages = []

if "nlp_engine" not in st.session_state:
    st.session_state.nlp_engine = NLPEngine()

# Header
st.markdown("# ✈️ Airline Reservation Chatbot")
st.markdown("*Powered by AI & MySQL Database*")

# Sidebar - Information
with st.sidebar:
    st.markdown("### 📋 Quick Guide")
    st.markdown("""
    **Try asking:**
    - "Show flights from DEL to BOM"
    - "Check booking status for PNR ABC1234"
    - "List all airlines"
    - "Show my boarding pass for XYZ9876"
    - "Show payment status for PNR ABC1234"
    
    **Supported Queries:**
    - ✅ Flight Search
    - ✅ Booking Status
    - ✅ Boarding Pass
    - ✅ Payment Details
    - ✅ Airline Info
    - ✅ Customer Search
    """)
    
    st.divider()
    
    st.markdown("### 🔧 System Status")
    db_status = DatabaseConnection.execute_query("SELECT 1")
    if db_status is not None:
        st.success("✅ Database Connected")
    else:
        st.error("❌ Database Disconnected")
    
    st.markdown("### 📞 Contact")
    st.markdown("Support: admin@airline.com")

# Main chat area
st.markdown("### 💬 Chat")

# Display chat history
for message in st.session_state.messages:
    with st.chat_message(message["role"]):
        st.markdown(message["content"])

# Chat input
user_input = st.chat_input("Type your question about flights, bookings, or payments...")

if user_input:
    # Add user message to history
    st.session_state.messages.append({
        "role": "user",
        "content": user_input
    })
    
    with st.chat_message("user"):
        st.markdown(user_input)
    
    # Process user input
    with st.chat_message("assistant"):
        with st.spinner("🤔 Processing your query..."):
            # Step 1: NLP Processing
            nlp_result = st.session_state.nlp_engine.process_query(user_input)
            
            if nlp_result["success"]:
                intent = nlp_result["intent"]
                sql_query = nlp_result["sql"]
                
                # Step 2: Execute SQL
                if isinstance(sql_query, dict):
                    # Special case for actions like booking
                    response = sql_query.get("message", "Processing your request...")
                else:
                    results = DatabaseConnection.execute_query(sql_query)
                    
                    if results is not None:
                        # Step 3: Format Response
                        response = ResponseFormatter.format_response(intent, results)
                    else:
                        response = "❌ Error executing query. Please try again."
            else:
                response = nlp_result.get("message", "❌ I didn't understand that. Please try again.")
            
            st.markdown(response)
    
    # Add bot message to history
    st.session_state.messages.append({
        "role": "assistant",
        "content": response
    })

# Footer
st.divider()
st.markdown("""
<div style='text-align: center; color: gray; font-size: 12px;'>
    🚀 Airline Chatbot v1.0 | Powered by Streamlit & MySQL | © 2025
</div>
""", unsafe_allow_html=True)
