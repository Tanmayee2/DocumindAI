from fastapi import APIRouter, HTTPException
from app.models.schemas import QueryRequest, QueryResponse, IndexRequest, IndexResponse, Source
from app.services.vector_store import VectorStoreService
from app.services.database import DatabaseService
from app.services.ai_service import AIService
from app.config import get_settings
import logging

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/query", tags=["query"])

# Initialize services
settings = get_settings()
vector_store = VectorStoreService(settings.chroma_db_path)
database = DatabaseService(settings.mongodb_uri)
ai_service = AIService(settings.anthropic_api_key)

@router.post("/index", response_model=IndexResponse)
async def index_document(request: IndexRequest):
    """Index a document's chunks into vector store"""
    try:
        # Get chunks from MongoDB
        chunks = database.get_chunks_by_document(request.document_id)
        
        if not chunks:
            raise HTTPException(status_code=404, detail="No chunks found for document")
        
        # Prepare chunks for vector store
        vector_chunks = [{
            'id': str(chunk['_id']),
            'content': chunk['content'],
            'document_id': chunk['documentId'],
            'chunk_index': chunk['chunkIndex']
        } for chunk in chunks]
        
        # Add to vector store
        vector_store.add_chunks(vector_chunks)
        
        return IndexResponse(
            status="success",
            document_id=request.document_id,
            chunks_indexed=len(chunks)
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error indexing document: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/ask", response_model=QueryResponse)
async def ask_question(request: QueryRequest):
    """Ask a question about documents"""
    try:
        # Search for relevant chunks
        search_results = vector_store.search(
            query=request.query,
            n_results=request.max_results,
            document_id=request.document_id
        )
        
        if not search_results['documents']:
            raise HTTPException(
                status_code=404,
                detail="No relevant information found"
            )
        
        # Generate answer with Claude
        ai_response = ai_service.generate_answer(
            query=request.query,
            context_chunks=search_results['documents']
        )
        
        # Check for hallucinations
        context_text = "\n\n".join(search_results['documents'])
        hallucination_check = ai_service.detect_hallucination(
            answer=ai_response['answer'],
            context=context_text
        )
        
        # Prepare sources
        sources = [
            Source(
                chunk_id=chunk_id,
                content=content,
                document_id=metadata['document_id'],
                relevance_score=1.0 - distance  # Convert distance to similarity
            )
            for chunk_id, content, metadata, distance in zip(
                search_results['ids'],
                search_results['documents'],
                search_results['metadatas'],
                search_results['distances']
            )
        ]
        
        return QueryResponse(
            answer=ai_response['answer'],
            sources=sources,
            model=ai_response['model'],
            hallucination_check=hallucination_check,
            usage=ai_response['usage']
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing query: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))