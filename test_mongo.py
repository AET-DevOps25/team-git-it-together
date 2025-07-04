# genai/tests/services/storage/test_mongo_repo.py
import os
import sys
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# Add project root to Python path
sys.path.insert(0, os.path.abspath('.'))

try:
    # Import after setting Python path
    from genai.src.services.storage.mongo_repo import save_path, ensure_index, col
    
    # First ensure the index exists
    logging.info("Setting up MongoDB index...")
    ensure_index()
    
    # Try to save a test document
    logging.info("Testing save_path function...")
    test_id = save_path("test-user", "Test Goal", [{"step": "Step 1"}])
    
    logging.info(f"Success! Created document with ID: {test_id}")
    
    # Verify it exists
    doc = col.find_one({"_id": test_id})
    if doc:
        logging.info(f"Document retrieved: {doc}")
        
        # Clean up
        col.delete_one({"_id": test_id})
        logging.info("Test document deleted")
    else:
        logging.error("Document not found after creation!")
        
except Exception as e:
    logging.error(f"Test failed: {e}")
    sys.exit(1)

logging.info("Test completed successfully!")