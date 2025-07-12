from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import query
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="DocuMind AI Service",
    description="AI-powered document Q&A service",
    version="1.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(query.router)

@app.get("/")
async def root():
    return {"message": "DocuMind AI Service", "status": "running"}

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "documind-ai"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)