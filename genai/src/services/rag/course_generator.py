import logging
import json
from typing import List

from .schemas import CourseGenerationRequest, Course
from ..embedding.embedder_service import embed_text, cosine_similarity
from ..llm.llm_service import generate_structured
import numpy as np
logger = logging.getLogger(__name__)


# ──────────────────────────────────────────────────────────────────────────
# Load categories and compute embeddings
# ──────────────────────────────────────────────────────────────────────────
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
    "Blockchain & Cryptocurrency"
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

def generate_course(req: CourseGenerationRequest) -> Course:
    context = "\n".join(_retrieve_context(req.prompt, k=5))
    existing = ", ".join(req.existing_skills) if req.existing_skills else "None"

    messages = [
        {"role": "system",
         "content": (
             "You are an expert curriculum designer. "
             "Generate self-contained course content in JSON."
         )},
        {"role": "user",
         "content": (
             f"Context:\n{context}\n\n"
             f"Learning Goal: \"{req.prompt}\"\n"
             f"Already mastered: {existing}\n"
             "→ Skip or briefly acknowledge mastered skills.\n"
             "Return exactly one JSON object following the Course schema."
         )},
    ]

    course: Course = generate_structured(messages, Course)
    course.instructor    = "SkillForge GenAI"
    course.published     = False
    course.isPublic      = False
    course.rating        = 0.0
    course.language      = "EN"
    course.thumbnailUrl  = "https://i.imgur.com/BRsKn1L.png"
    allowed = {"BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"}
    lvl = str(course.level or "").upper().replace(" ", "_") or "BEGINNER"
    if lvl not in allowed:
        logger.warning(f"Unknown level '{course.level}', defaulting to BEGINNER")
        lvl = "BEGINNER"
    course.level = lvl
    course.categories = _infer_categories(course, req.prompt)

    # Normalize content.type
    for mod in parsed.modules:
        for lesson in mod.lessons:
            lesson.content.type = lesson.content.type.upper().replace(" ", "_")
            if lesson.content.type not in {"TEXT", "HTML", "URL", "VIDEO", "AUDIO", "IMAGE"}:
                logger.warning(f"Unknown lesson content type '{lesson.content.type}', defaulting to TEXT")
                lesson.content.type = "TEXT"

    # Post-check – warn if any known skills are still included
    for skill in req.existing_skills:
        if skill.lower() in (s.lower() for s in parsed.skills):
            logger.warning(f"Generated course includes known skill '{skill}'")

    return course

