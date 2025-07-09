import logging
import numpy as np
from typing import List
from ..embedding.embedder_service import embed_text

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
