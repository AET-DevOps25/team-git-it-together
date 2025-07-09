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


def _infer_categories(course: Course, prompt: str = "") -> List[str]:
    if not CATEGORY_EMBEDDINGS:
        return ["Programming & Development"]

    try:
        title_vec  = np.array(embed_text(course.title))
        desc_vec   = np.array(embed_text(" ".join([course.description, *course.skills])))
        prompt_vec = np.array(embed_text(prompt)) if prompt else np.zeros_like(title_vec)

        course_vector = (
            0.35 * title_vec +
            0.25 * desc_vec +
            0.40 * prompt_vec
        )
        course_vector /= np.linalg.norm(course_vector)
    except Exception as e:
        logger.warning(f"Embedding error: {e}")
        return ["Programming & Development"]

    sims = [(cat, cosine_similarity(course_vector, vec)) for cat, vec in CATEGORY_EMBEDDINGS.items()]
    sims.sort(key=lambda x: x[1], reverse=True)

    threshold, delta = 0.40, 0.05
    top = sims[0][1]
    matches = [c for c, s in sims if s >= threshold or s >= top - delta] or [sims[0][0]]
    return matches[:4]
