# Airline Reservation System

A comprehensive airline reservation system built with Java and integrated with an AI-powered chatbot for enhanced user experience.

## Features

- **Flight Search & Booking**: Search and book flights with multiple filter options
- **AI Chatbot Integration**: Interactive chatbot powered by spaCy NLP for customer support
- **User Authentication**: Secure login and registration system
- **Booking Management**: View, modify, and cancel bookings
- **Payment Processing**: Integrated payment system for flight bookings
- **Session Management**: User session tracking for personalized experience
- **Admin Dashboard**: Administrative features for managing flights, bookings, and users

## Project Structure

```
AirlineReservation/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mycompany/
│       │           └── airlinereservation/
│       │               ├── HomePage.java
│       │               ├── LoginPage.java
│       │               ├── RegisterPage.java
│       │               ├── SearchFlights.java
│       │               ├── BookingPage.java
│       │               ├── ViewBookings.java
│       │               ├── UserDashboard.java
│       │               ├── AdminPanel.java
│       │               ├── database/
│       │               │   └── DatabaseConnection.java
│       │               └── utils/
│       │                   └── (utility classes)
│       └── resources/
│           └── (configuration files)
├── spacy_service/
│   ├── app.py
│   ├── requirements.txt
│   └── venv/ (Python virtual environment)
├── chatbot_python/
│   └── (Python chatbot integration files)
├── pom.xml
└── README.md
```

## Technology Stack

- **Backend**: Java with Maven
- **Frontend**: Java Swing (GUI)
- **Database**: SQL (configurable - MySQL/PostgreSQL)
- **NLP**: Python spaCy for chatbot processing
- **Build Tool**: Maven

## Prerequisites

- Java Development Kit (JDK 11 or higher)
- Maven 3.6 or higher
- Python 3.8 or higher
- MySQL/PostgreSQL database
- Git

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/AirlineReservation.git
cd AirlineReservation
```

### 2. Install Java Dependencies

```bash
mvn clean install
```

### 3. Set Up Python Environment (for NLP/Chatbot)

```bash
cd spacy_service
python -m venv venv
venv\Scripts\activate  # On Windows
# or
source venv/bin/activate  # On macOS/Linux

pip install -r requirements.txt
python -m spacy download en_core_web_sm
```

### 4. Configure Database

Update `src/main/java/com/mycompany/airlinereservation/database/DatabaseConnection.java` with your database credentials:

```java
private static final String URL = "jdbc:mysql://localhost:3306/airline_reservation";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

Create the database schema by running the provided SQL scripts.

### 5. Start the NLP Service

```bash
cd spacy_service
python app.py  # Starts Flask server on port 5000
```

### 6. Run the Application

```bash
mvn exec:java -Dexec.mainClass=com.mycompany.airlinereservation.HomePage
```

## Usage

### Main Features

1. **Search Flights**: Enter departure/arrival cities and dates
2. **Book Flight**: Select a flight and complete payment
3. **View Bookings**: Manage your existing reservations
4. **Chat with Bot**: Use the integrated chatbot for support
5. **Admin Panel**: Manage flights and user data (admin only)

## API Endpoints (NLP Service)

### Health Check
```
GET http://localhost:5000/health
```

### Text Processing
```
POST http://localhost:5000/process
Content-Type: application/json

{
  "text": "I want to book a flight to New York"
}
```

## Building & Deployment

### Build JAR File

```bash
mvn clean package
```

The executable JAR will be created in the `target/` directory.

### Run JAR File

```bash
java -jar target/AirlineReservation-1.0-SNAPSHOT.jar
```

## Database Schema

The system uses the following main tables:
- `users` - User account information
- `flights` - Available flights
- `bookings` - User bookings
- `payments` - Payment transactions
- `tickets` - Ticket information

## Configuration

### Environment Variables

Create a `.env` file in the project root:

```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=airline_reservation
DB_USER=root
DB_PASSWORD=your_password
CHATBOT_API_URL=http://localhost:5000
```

## Testing

Run unit tests:

```bash
mvn test
```

## Troubleshooting

### Database Connection Issues
- Ensure MySQL/PostgreSQL is running
- Verify database credentials in configuration
- Check database name and schema

### NLP Service Not Starting
- Verify Python virtual environment is activated
- Check Flask is installed: `pip install flask`
- Ensure port 5000 is not in use

### GUI Issues
- Ensure Java Swing dependencies are available
- Check display settings on Linux systems

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see LICENSE.md file for details.

## Contact & Support

For questions or support, please create an issue on GitHub or contact the development team.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and updates.

## Acknowledgments

- Built with Java, Maven, and Python
- NLP powered by spaCy
- Database integration with JDBC
