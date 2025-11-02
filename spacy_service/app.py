"""
Flask Microservice for spaCy NLP Processing
Integrates with Java Airline Chatbot for natural language understanding
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import spacy
import json
from datetime import datetime
import logging

# Initialize Flask app
app = Flask(__name__)
CORS(app)  # Enable CORS for Java HTTP requests

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Load spaCy NLP model
nlp = None
try:
    # Try loading by package name first (for installed packages)
    nlp = spacy.load("en_core_web_sm")
    logger.info("✅ spaCy model loaded successfully from package")
except (OSError, ImportError):
    try:
        # Try loading from site-packages directory (spaCy 3.x style)
        import pkg_resources
        import sys
        packages = pkg_resources.working_set
        for package in packages:
            if package.project_name == "en-core-web-sm":
                model_path = package.location + "/en_core_web_sm"
                nlp = spacy.load(model_path)
                logger.info(f"✅ spaCy model loaded successfully from {model_path}")
                break
    except Exception as e:
        logger.error(f"⚠️  Failed to load spaCy model: {e}")
        logger.info("Attempting to load model from site-packages...")
        
        # Last resort - list installed packages
        import pkgutil
        for importer, modname, ispkg in pkgutil.iter_modules(spacy.util.get_model_path(".").__str__().split()):
            if "en_core" in modname:
                try:
                    nlp = spacy.load(modname)
                    logger.info(f"✅ spaCy model '{modname}' loaded successfully")
                    break
                except:
                    pass

if nlp is None:
    logger.warning("❌ spaCy model not loaded. Will use rule-based NLP only")

# Intent patterns mapping
INTENT_PATTERNS = {
    "book_flight": ["book", "reserve", "buy", "purchase", "flight"],
    "cancel_booking": ["cancel", "refund", "delete", "remove", "booking", "ticket"],
    "view_booking": ["show", "view", "check", "list", "my", "booking", "reservation"],
    "flight_status": ["status", "delay", "info", "information", "flight"],
    "boarding_pass": ["boarding", "pass", "ticket", "download", "generate"],
    "customer_lookup": ["customer", "profile", "aadhaar", "aadhar", "details"],
    "check_availability": ["available", "flights", "check", "search", "from", "to"],
    "payment_info": ["payment", "summary", "cost", "price", "fare"],
    "help": ["help", "menu", "guide", "what can you do", "commands"]
}

# City/Airport mappings
CITY_SYNONYMS = {
    "delhi": ["delhi", "new delhi", "dxb", "ndls"],
    "mumbai": ["mumbai", "bombay", "bom"],
    "bangalore": ["bangalore", "bengaluru", "blr"],
    "kolkata": ["kolkata", "calcutta", "ccu"],
    "hyderabad": ["hyderabad", "hyd"],
    "pune": ["pune", "puneh", "pnq"],
    "goa": ["goa", "goi"],
    "jaipur": ["jaipur", "jai", "jpr"],
}

def normalize_city(city_name):
    """Normalize city names to standard format"""
    city_lower = city_name.lower().strip()
    for standard, synonyms in CITY_SYNONYMS.items():
        if city_lower in synonyms:
            return standard.title()
    return city_name.title()

def extract_entities(doc=None, text=""):
    """Extract named entities and custom entities from text"""
    entities = {
        "locations": [],
        "dates": [],
        "organizations": [],
        "money": [],
        "person": [],
        "custom_locations": []
    }
    
    # Extract spaCy entities if model is available
    if doc is not None:
        for ent in doc.ents:
            if ent.label_ == "GPE":  # Geopolitical entity (location)
                entities["locations"].append({
                    "text": ent.text,
                    "normalized": normalize_city(ent.text),
                    "type": "location"
                })
            elif ent.label_ == "DATE":
                entities["dates"].append({
                    "text": ent.text,
                    "type": "date"
                })
            elif ent.label_ == "ORG":
                entities["organizations"].append({
                    "text": ent.text,
                    "type": "organization"
                })
            elif ent.label_ == "MONEY":
                entities["money"].append({
                    "text": ent.text,
                    "type": "money"
                })
            elif ent.label_ == "PERSON":
                entities["person"].append({
                    "text": ent.text,
                    "type": "person"
                })
        text = doc.text
    
    # Extract custom locations from predefined mapping
    text_lower = text.lower() if isinstance(text, str) else (doc.text.lower() if doc else "")
    for standard, synonyms in CITY_SYNONYMS.items():
        for synonym in synonyms:
            if synonym in text_lower:
                normalized = normalize_city(synonym)
                if not any(e["normalized"] == normalized for e in entities["locations"]):
                    entities["custom_locations"].append({
                        "text": synonym,
                        "normalized": normalized,
                        "type": "location"
                    })
    
    return entities

def detect_intent(text, entities):
    """Detect user intent based on text and entities"""
    text_lower = text.lower()
    intent_scores = {}
    
    for intent, keywords in INTENT_PATTERNS.items():
        score = sum(1 for keyword in keywords if keyword in text_lower)
        if score > 0:
            intent_scores[intent] = score
    
    if not intent_scores:
        return {
            "primary_intent": "general_query",
            "confidence": 0.5,
            "all_intents": []
        }
    
    # Sort by score (descending)
    sorted_intents = sorted(intent_scores.items(), key=lambda x: x[1], reverse=True)
    primary_intent = sorted_intents[0][0]
    confidence = min(sorted_intents[0][1] / max(len(INTENT_PATTERNS.get(primary_intent, [])), 1), 1.0)
    
    return {
        "primary_intent": primary_intent,
        "confidence": round(confidence, 2),
        "all_intents": [{"intent": intent, "score": score} for intent, score in sorted_intents]
    }

def extract_route_from_text(text, entities):
    """Extract source and destination from text"""
    route = {
        "source": None,
        "destination": None
    }
    
    text_lower = text.lower()
    locations = entities.get("locations", []) + entities.get("custom_locations", [])
    
    if len(locations) >= 2:
        # Assume first location is source, second is destination
        route["source"] = locations[0]["normalized"]
        route["destination"] = locations[1]["normalized"]
    elif len(locations) == 1:
        # Check for "from/to" keywords
        if "from" in text_lower:
            route["source"] = locations[0]["normalized"]
        elif "to" in text_lower:
            route["destination"] = locations[0]["normalized"]
    
    return route

def extract_date_from_text(text):
    """Extract travel date from text"""
    # Simple date extraction - can be enhanced with more complex parsing
    import re
    
    # Pattern: YYYY-MM-DD
    date_pattern = r"\b(\d{4}-\d{2}-\d{2})\b"
    match = re.search(date_pattern, text)
    if match:
        return match.group(1)
    
    # Pattern: DD-MM-YYYY or DD/MM/YYYY
    date_pattern = r"\b(\d{1,2}[-/]\d{1,2}[-/]\d{4})\b"
    match = re.search(date_pattern, text)
    if match:
        return match.group(1)
    
    return None

def extract_travel_class(text):
    """Extract travel class (Economy/Business) from text"""
    text_lower = text.lower()
    
    if "business" in text_lower:
        return "Business"
    elif "economy" in text_lower or "eco" in text_lower:
        return "Economy"
    
    return None

def extract_aadhaar(text):
    """Extract Aadhaar number from text"""
    import re
    
    # Remove spaces/dashes and look for 12 consecutive digits
    digits_only = re.sub(r"[^\d]", "", text)
    
    if len(digits_only) >= 12:
        return digits_only[:12]
    
    return None

@app.route("/health", methods=["GET"])
def health():
    """Health check endpoint"""
    return jsonify({"status": "healthy", "service": "spaCy NLP Microservice"}), 200

@app.route("/process", methods=["POST"])
def process_text():
    """
    Main NLP processing endpoint
    Expects: {"text": "user input string"}
    Returns: {"intent": {...}, "entities": {...}, "extracted_data": {...}}
    """
    try:
        data = request.get_json()
        
        if not data or "text" not in data:
            return jsonify({"error": "Missing 'text' field in request"}), 400
        
        user_text = data.get("text", "").strip()
        
        if not user_text:
            return jsonify({"error": "Empty text provided"}), 400
        
        # Process with spaCy if available, otherwise use rule-based extraction
        doc = None
        if nlp is not None:
            doc = nlp(user_text)
        
        # Extract entities (works with or without spaCy model)
        entities = extract_entities(doc, user_text)
        
        # Detect intent
        intent = detect_intent(user_text, entities)
        
        # Extract specific data based on intent
        extracted_data = {
            "route": extract_route_from_text(user_text, entities),
            "travel_date": extract_date_from_text(user_text),
            "travel_class": extract_travel_class(user_text),
            "aadhaar": extract_aadhaar(user_text)
        }
        
        # POS tags for additional context
        pos_tags = [(token.text, token.pos_) for token in doc]
        
        response = {
            "success": True,
            "original_text": user_text,
            "intent": intent,
            "entities": entities,
            "extracted_data": {
                key: value for key, value in extracted_data.items() if value is not None
            },
            "pos_tags": pos_tags,
            "processed_at": datetime.now().isoformat()
        }
        
        logger.info(f"✅ Processed: '{user_text}' -> Intent: {intent['primary_intent']}")
        
        return jsonify(response), 200
    
    except Exception as e:
        logger.error(f"❌ Error processing text: {str(e)}")
        return jsonify({"error": str(e), "success": False}), 500

@app.route("/entities", methods=["POST"])
def extract_entities_only():
    """
    Extract only entities from text
    Expects: {"text": "user input string"}
    Returns: {"entities": {...}}
    """
    try:
        data = request.get_json()
        
        if not data or "text" not in data:
            return jsonify({"error": "Missing 'text' field in request"}), 400
        
        user_text = data.get("text", "").strip()
        
        if not user_text:
            return jsonify({"error": "Empty text provided"}), 400
        
        doc = nlp(user_text) if nlp is not None else None
        entities = extract_entities(doc, user_text)
        
        return jsonify({"success": True, "entities": entities}), 200
    
    except Exception as e:
        logger.error(f"❌ Error extracting entities: {str(e)}")
        return jsonify({"error": str(e), "success": False}), 500

@app.route("/intent", methods=["POST"])
def detect_intent_only():
    """
    Detect only intent from text
    Expects: {"text": "user input string"}
    Returns: {"intent": {...}}
    """
    try:
        data = request.get_json()
        
        if not data or "text" not in data:
            return jsonify({"error": "Missing 'text' field in request"}), 400
        
        user_text = data.get("text", "").strip()
        
        if not user_text:
            return jsonify({"error": "Empty text provided"}), 400
        
        doc = nlp(user_text) if nlp is not None else None
        entities = extract_entities(doc, user_text)
        intent = detect_intent(user_text, entities)
        
        return jsonify({"success": True, "intent": intent}), 200
    
    except Exception as e:
        logger.error(f"❌ Error detecting intent: {str(e)}")
        return jsonify({"error": str(e), "success": False}), 500

@app.route("/extract-route", methods=["POST"])
def extract_route():
    """
    Extract route (source, destination) from text
    Expects: {"text": "user input string"}
    Returns: {"route": {"source": "...", "destination": "..."}}
    """
    try:
        data = request.get_json()
        
        if not data or "text" not in data:
            return jsonify({"error": "Missing 'text' field in request"}), 400
        
        user_text = data.get("text", "").strip()
        
        if nlp is None:
            return jsonify({"error": "spaCy model not loaded"}), 500
        
        doc = nlp(user_text)
        entities = extract_entities(doc)
        route = extract_route_from_text(user_text, entities)
        
        return jsonify({"success": True, "route": route}), 200
    
    except Exception as e:
        logger.error(f"❌ Error extracting route: {str(e)}")
        return jsonify({"error": str(e), "success": False}), 500

if __name__ == "__main__":
    logger.info("🚀 Starting spaCy NLP Microservice...")
    logger.info("📡 Service running on http://localhost:5000")
    logger.info("Available endpoints:")
    logger.info("  - GET  /health")
    logger.info("  - POST /process")
    logger.info("  - POST /entities")
    logger.info("  - POST /intent")
    logger.info("  - POST /extract-route")
    
    app.run(host="0.0.0.0", port=5000, debug=False)
