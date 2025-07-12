import chromadb
from chromadb.config import Settings as ChromaSettings
from typing import List, Dict
import logging

logger = logging.getLogger(__name__)

class VectorStoreService:
    def __init__(self, persist_directory: str):
        self.client = chromadb.Client(ChromaSettings(persist_directory=persist_directory,anonymized_telemetry=False))
        self.collection = self.client.get_or_create_collection(
        name="document_chunks",
        metadata={"hnsw:space": "cosine"})
        logger.info("ChromaDB initialized successfully")

    def add_chunks(self, chunks: List[Dict]):
    # Add document chunks to vector store
        try:
            ids = [chunk['id'] for chunk in chunks]
            documents = [chunk['content'] for chunk in chunks]
            metadatas = [{
                'document_id': chunk['document_id'],
                'chunk_index': chunk['chunk_index']
            } for chunk in chunks]
            
            self.collection.add(
                ids=ids,
                documents=documents,
                metadatas=metadatas
            )
            logger.info(f"Added {len(chunks)} chunks to vector store")
            return True
        except Exception as e:
            logger.error(f"Error adding chunks: {str(e)}")
            raise

    def search(self, query: str, n_results: int = 5, document_id: str = None):
        # Search for relevant chunks
        try:
            where_filter = {"document_id": document_id} if document_id else None
            
            results = self.collection.query(
                query_texts=[query],
                n_results=n_results,
                where=where_filter
            )
            
            return {
                'ids': results['ids'][0] if results['ids'] else [],
                'documents': results['documents'][0] if results['documents'] else [],
                'metadatas': results['metadatas'][0] if results['metadatas'] else [],
                'distances': results['distances'][0] if results['distances'] else []
            }
        except Exception as e:
            logger.error(f"Error searching: {str(e)}")
            raise

    def delete_document_chunks(self, document_id: str):
        # Delete all chunks for a document
        try:
            self.collection.delete(where={"document_id": document_id})
            logger.info(f"Deleted chunks for document {document_id}")
        except Exception as e:
            logger.error(f"Error deleting chunks: {str(e)}")
            raise