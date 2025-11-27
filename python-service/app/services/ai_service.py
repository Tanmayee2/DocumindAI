import anthropic
from typing import List, Dict
import logging

logger = logging.getLogger(__name__)

class AIService:
    def __init__(self, api_key: str):
        self.client = anthropic.Anthropic(api_key=api_key)
        self.model = "claude-sonnet-4-20250514"
        logger.info("Anthropic Claude API initialized")
    
    def generate_answer(self, query: str, context_chunks: List[str]) -> Dict:
        """Generate answer using Claude with context"""
        try:
            # Prepare context
            context = "\n\n".join([
                f"[Context {i+1}]:\n{chunk}" 
                for i, chunk in enumerate(context_chunks)
            ])
            
            # Create prompt
            prompt = f"""You are a helpful AI assistant analyzing documents. Based on the provided context, answer the user's question accurately and cite your sources.

            Context from documents:
            {context}

            User Question: {query}

            Instructions:
            1. Answer based ONLY on the provided context
            2. If the answer isn't in the context, say so
            3. Cite the specific context sections you used (e.g., "According to Context 1...")
            4. Be concise but comprehensive
            5. If you detect potential hallucinations or uncertainty, acknowledge it

            Answer:"""
            
            # Call Claude API
            message = self.client.messages.create(
                model=self.model,
                max_tokens=1024,
                messages=[
                    {"role": "user", "content": prompt}
                ]
            )
            
            answer = message.content[0].text
            
            return {
                "answer": answer,
                "model": self.model,
                "usage": {
                    "input_tokens": message.usage.input_tokens,
                    "output_tokens": message.usage.output_tokens
                }
            }
            
        except Exception as e:
            logger.error(f"Error generating answer: {str(e)}")
            raise
    
    def detect_hallucination(self, answer: str, context: str) -> Dict:
        """Simple hallucination detection"""
        try:
            prompt = f"""Analyze if this answer contains information not present in the context.

            Context:
            {context}

            Answer:
            {answer}

            Is there any information in the answer that's not supported by the context? Reply with:
            - "VALID" if all information is from context
            - "HALLUCINATION" if answer contains unsupported claims
            - Explain briefly

            Response:"""
            
            message = self.client.messages.create(
                model=self.model,
                max_tokens=256,
                messages=[
                    {"role": "user", "content": prompt}
                ]
            )
            
            result = message.content[0].text
            is_hallucination = "HALLUCINATION" in result.upper()
            
            return {
                "is_hallucination": is_hallucination,
                "explanation": result
            }
            
        except Exception as e:
            logger.error(f"Error detecting hallucination: {str(e)}")
            return {"is_hallucination": False, "explanation": "Detection failed"}