# 🎯 Floating Chat Widget Implementation Guide

## Overview

A **modern, non-intrusive floating chat bubble widget** has been successfully integrated into the Airline Reservation Dashboard. The chat widget provides real-time support and assistance to users while maintaining a clean, professional interface.

---

## ✨ Features

### 1. **Floating Bubble Icon**
- Appears in the **bottom-right corner** of the screen
- Small circular bubble (60x60 pixels) with a chat icon 💬
- **Always-on-top** by default to ensure visibility
- **Draggable** - users can move it anywhere on the screen
- Non-intrusive - doesn't interfere with dashboard functionality

### 2. **Expandable Chat Window**
- **Click the bubble** to expand into a full chat window (400x600 pixels)
- Smooth layout transition showing:
  - Chat header with title and control buttons
  - Scrollable message history
  - Text input field with send button
  - Rounded corners with modern styling

### 3. **Interactive Controls**
| Button | Function | Icon |
|--------|----------|------|
| **Minimize** | Collapse back to bubble | − |
| **Close** | Hide widget | ✕ |
| **Always On Top** | Toggle window layering | 📌 |

### 4. **Chat Functionality**
- Sends messages to the integrated ChatBot AI
- Displays conversation history with styled message bubbles
  - **User messages**: Right-aligned, light green bubbles
  - **Bot messages**: Left-aligned, white bubbles
- Auto-scrolls to latest messages
- Press **Enter** to send messages quickly

### 5. **Smart Design**
- Rounded corners with modern blue theme (#4AA6F0)
- Semi-transparent background (95% opacity)
- Handles screen boundaries (keeps widget within view)
- Focuses on text input when expanded
- Integrates with existing ChatBot conversation engine

---

## 🚀 Quick Start

### Starting the Widget

The floating chat widget **automatically initializes** when a user logs into the dashboard:

```java
// In UserDashboard.java - automatically created
floatingChatWidget = new FloatingChatWidget(username, this);
floatingChatWidget.setVisible(true);
```

### User Interactions

1. **See the Bubble**: A small 💬 icon appears in the bottom-right corner
2. **Click to Chat**: Click the bubble to expand the chat window
3. **Type & Send**: Type your message and press Enter or click "Send"
4. **Get Responses**: The ChatBot replies instantly
5. **Minimize**: Click the `−` button or close button to minimize back to bubble
6. **Move It**: Drag the bubble to any screen location
7. **Toggle Always On Top**: Click 📌 to prevent other windows from covering it

### Keyboard Shortcut

Users can toggle the widget visibility using:
```
Ctrl + Shift + C
```

---

## 🔧 Technical Implementation

### File Structure

```
src/main/java/com/mycompany/airlinereservation/
├── FloatingChatWidget.java      (NEW - Main widget class)
├── UserDashboard.java           (MODIFIED - Integrated widget)
├── ChatBot.java                 (EXISTING - Conversation engine)
└── ChatBotDialog.java           (EXISTING - Alternative modal dialog)
```

### Class: FloatingChatWidget

**Package**: `com.mycompany.airlinereservation`

**Key Methods**:
```java
// Constructor
FloatingChatWidget(String username, JFrame parent)

// Public Methods
showWidget()        // Show the widget
toggleWidget()      // Toggle visibility
clearChat()         // Clear chat history

// Private Methods
expandWidget()      // Expand bubble to full window
minimizeWidget()    // Minimize back to bubble
sendMessage()       // Process message sending
appendMessage()     // Add styled message to display
```

**Color Scheme**:
```java
PRIMARY_COLOR = RGB(74, 166, 240)         // Main blue
SECONDARY_COLOR = RGB(52, 152, 219)       // Darker blue (hover)
ACCENT_COLOR = RGB(240, 248, 255)         // Light blue background
TEXT_COLOR = RGB(30, 30, 30)              // Dark text
BOT_BUBBLE = RGB(255, 255, 255)           // White
USER_BUBBLE = RGB(220, 248, 198)          // Light green
```

### Integration Points

#### 1. **UserDashboard.java**
```java
// Field
private FloatingChatWidget floatingChatWidget;

// Initialization
floatingChatWidget = new FloatingChatWidget(username, this);
floatingChatWidget.setVisible(true);

// Keyboard shortcut
Ctrl+Shift+C toggles widget visibility
```

#### 2. **ChatBot.java** (Existing)
The FloatingChatWidget uses the existing ChatBot class:
- Processes natural language messages
- Queries database for flight/booking info
- Provides intelligent responses
- Handles multi-step booking flows

---

## 📱 User Experience Flow

```
Dashboard Load
    ↓
FloatingChatWidget initialized (hidden in bubble)
    ↓
User sees 💬 bubble in bottom-right
    ↓
User clicks bubble
    ↓
Widget expands to show chat window
    ↓
User types message & presses Enter
    ↓
Message displayed in right-aligned green bubble
    ↓
ChatBot processes & responds
    ↓
Bot response shown in left-aligned white bubble
    ↓
User can continue conversation or close widget
    ↓
Widget minimizes back to bubble
```

---

## 🎨 Customization Guide

### Change Colors

Edit the color constants in `FloatingChatWidget.java`:

```java
private static final Color PRIMARY_COLOR = new Color(74, 166, 240);
private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
private static final Color ACCENT_COLOR = new Color(240, 248, 255);
private static final Color TEXT_COLOR = new Color(30, 30, 30);
private static final Color BOT_BUBBLE = new Color(255, 255, 255);
private static final Color USER_BUBBLE = new Color(220, 248, 198);
```

### Change Bubble Size

```java
private static final int BUBBLE_SIZE = 60;        // Default: 60x60
private static final int EXPANDED_WIDTH = 400;    // Default: 400px
private static final int EXPANDED_HEIGHT = 600;   // Default: 600px
```

### Change Position

```java
// Default: bottom-right with 20px margin
bubbleLocation = new Point(
    screenBounds.width - BUBBLE_SIZE - 20,   // Change 20 for left margin
    screenBounds.height - BUBBLE_SIZE - 80   // Change 80 for top margin
);
```

### Change Welcome Message

Edit the initial message in `initializeUI()`:

```java
appendBotMessage("👋 Welcome! How can we help you today?\n\nYou can ask about:\n" +
                "✈️ Flight availability\n" +
                "🎫 Bookings\n" +
                "📋 Your reservations\n" +
                "💳 Payments");
```

---

## 🐛 Troubleshooting

### Widget Doesn't Appear
- Check that `FloatingChatWidget` is properly initialized in `UserDashboard`
- Ensure `setVisible(true)` is called
- Check that ChatBot username is passed correctly

### Messages Not Sending
- Verify ChatBot class is accessible
- Check database connection in `DBConnection.java`
- Ensure ChatBot.java changes (airlines table joins) are applied

### Widget Off-Screen
- The widget has boundary checking and will keep itself within screen bounds
- Try dragging it or restarting the application

### Chat Not Responding
- Check that airlines table is joined correctly in ChatBot queries
- Verify database contains valid airline_id references
- Check console for SQL errors

---

## 🔐 Security Considerations

1. **User Context**: Widget receives username to track conversations
2. **Database Queries**: All queries use prepared statements (SQL injection safe)
3. **Message Validation**: User input is escaped before HTML display
4. **Connection Pooling**: Uses centralized `DBConnection` class

---

## 📊 Database Integration

The FloatingChatWidget works with the existing airline reservation database:

**Key Tables**:
- `flight` - Flight information with airline_id
- `airlines` - Airline names and details
- `booking` - User reservations
- `customers` - Passenger information
- `payments` - Payment records

**Supported Queries**:
- Show available flights
- Book flights
- View bookings
- Cancel bookings
- Generate boarding passes
- Check payment status
- Aadhaar-based customer lookup

---

## 🎯 Future Enhancements

Potential improvements for future versions:

1. **Agent Transfer**: Route to human support agent
2. **File Upload**: Share boarding passes or documents
3. **Voice Chat**: Audio messaging capability
4. **Persistent History**: Save chat across sessions
5. **Multi-language Support**: Chat in different languages
6. **Sentiment Analysis**: Detect user frustration and escalate
7. **Rich Media**: Display flight images, maps, pricing charts
8. **Typing Indicators**: Show when bot is "thinking"
9. **Quick Replies**: Suggest common questions
10. **Analytics**: Track user satisfaction and common issues

---

## 📝 Example Conversations

### Example 1: Flight Search
```
User: Show flights from Delhi to Mumbai
Bot: ✈️ Available Flights:
     From: Delhi → To: Mumbai
     
     1. Flight: AI203 (Air India)
     Depart: 2024-10-30 08:00 | Arrive: 2024-10-30 10:15
     Duration: 2h 15m | Price: ₹3500
     Seats Available: 45
     
     💡 Reply with 'Book flight AI203' to book!
     
User: Book flight AI203
Bot: 🎫 Let's book a flight for you!
     Please tell me your departure city (source):
```

### Example 2: Booking Status
```
User: What are my bookings?
Bot: 📋 Your Bookings:
     
     1. PNR: IN45721
     Flight: AI203 (Air India)
     Route: Delhi → Mumbai
     Date: 2024-10-30
     Status: Confirmed
     Fare: ₹3500
```

---

## 🤝 Support

For issues or questions about the FloatingChatWidget:

1. Check the troubleshooting section above
2. Review the ChatBot.java changes for database queries
3. Verify DBConnection credentials
4. Check browser console for errors
5. Review application logs for SQL errors

---

## 📄 File Modifications Summary

### New Files
- ✅ `FloatingChatWidget.java` - Main widget implementation

### Modified Files
- ✅ `UserDashboard.java` - Added widget initialization and keyboard shortcut
- ✅ `ChatBot.java` - Fixed SQL queries to join airlines table

---

## ✅ Verification Checklist

- [ ] FloatingChatWidget.java created successfully
- [ ] UserDashboard.java modified with widget initialization
- [ ] ChatBot.java updated with airlines table joins
- [ ] Application compiles without errors
- [ ] User Dashboard loads with floating bubble in bottom-right
- [ ] Clicking bubble expands chat window
- [ ] Messages send and bot responds
- [ ] Bubble can be dragged around screen
- [ ] Minimize button collapses to bubble
- [ ] Close button hides widget completely
- [ ] Ctrl+Shift+C toggles widget visibility
- [ ] Chat history displays with proper bubble styling
- [ ] Widget stays within screen boundaries

---

**Version**: 1.0  
**Last Updated**: October 29, 2025  
**Status**: ✅ Ready for Production
