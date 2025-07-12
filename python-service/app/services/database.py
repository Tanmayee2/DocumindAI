from pymongo import MongoClient
from typing import List, Dict, Optional
import logging

logger = logging.getLogger(__name__)

class DatabaseService:
    def __init__(self, mongodb_uri: str):
        self.client = MongoClient(mongodb_uri)
        self.db = self.client['documind']
        self.documents_collection = self.db['documents']
        self.chunks_collection = self.db['chunks']
        logger.info("MongoDB connection established")
    
    def get_document(self, document_id: str) -> Optional[Dict]:
        """Get document by ID"""
        return self.documents_collection.find_one({"_id": document_id})
    
    def get_chunks_by_document(self, document_id: str) -> List[Dict]:
        """Get all chunks for a document"""
        chunks = list(self.chunks_collection.find({"documentId": document_id}))
        return chunks
    
    def get_chunk_by_id(self, chunk_id: str) -> Optional[Dict]:
        """Get chunk by ID"""
        return self.chunks_collection.find_one({"_id": chunk_id})