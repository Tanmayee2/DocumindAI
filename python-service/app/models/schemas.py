from pydantic import BaseModel
from typing import List, Optional, Dict

class QueryRequest(BaseModel):
    query: str
    document_id: Optional[str] = None
    max_results: int = 5

class Source(BaseModel):
    chunk_id: str
    content: str
    document_id: str
    relevance_score: float

class QueryResponse(BaseModel):
    answer: str
    sources: List[Source]
    model: str
    hallucination_check: Optional[Dict] = None
    usage: Optional[Dict] = None

class IndexRequest(BaseModel):
    document_id: str

class IndexResponse(BaseModel):
    status: str
    document_id: str
    chunks_indexed: int