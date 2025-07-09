import logging
import numpy as np
from typing import List
from ..embedding.embedder_service import embed_text
from ..embedding import embedder_service
logger = logging.getLogger(__name__)

CATEGORIES = [
    "Programming & Development",
    "Data Science & Analytics",
    "Web Development",
    "Mobile Development",
    "DevOps & Cloud",
    "Cybersecurity",
    "Design & UX",
    "Business & Marketing",
    "Artificial Intelligence",
    "Blockchain & Cryptocurrency",
]

CATEGORY_EMBEDDINGS = {cat: embed_text(cat) for cat in CATEGORIES}

def _retrieve_context(query: str, k: int = 5) -> List[str]:
    docs = embedder_service.query_similar_chunks(query_text=query, limit=k)
    return [d.content for d in docs.results]