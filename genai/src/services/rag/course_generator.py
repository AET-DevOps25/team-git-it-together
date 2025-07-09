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

# ──────────────────────────────────────────────────────────────────────────
# Helper functions
# ──────────────────────────────────────────────────────────────────────────

def _retrieve_context(query: str, k: int = 5) -> List[str]:
    docs = embedder_service.query_similar_chunks(query_text=query, limit=k)
    return [d.content for d in docs.results]



def _infer_categories(course: Course, prompt: str = "") -> List[str]:
    if not CATEGORY_EMBEDDINGS:
        return ["Programming & Development"]

    try:
        # Weighted vector components
        title_vec = np.array(embed_text(course.title))
        desc_vec = np.array(embed_text(" ".join([
            course.description,
            " ".join(course.skills),
            " ".join(mod.title for mod in course.modules),
        ])))
        prompt_vec = np.array(embed_text(prompt)) if prompt else np.zeros_like(title_vec)

        # Weights
        course_vector = (
            0.35 * title_vec +
            0.25 * desc_vec +
            0.40 * prompt_vec
        )

        # Normalize (optional but improves consistency)
        course_vector = course_vector / np.linalg.norm(course_vector)

    except Exception as e:
        logger.warning(f"Embedding error: {e}")
        return ["Programming & Development"]

    # Calculate similarities
    similarity_scores = []
    for cat, cat_vec in CATEGORY_EMBEDDINGS.items():
        sim = cosine_similarity(course_vector, cat_vec)
        similarity_scores.append((cat, sim))
        logger.info(f"Category similarity - {cat}: {sim:.4f}")

    similarity_scores.sort(key=lambda x: x[1], reverse=True)
    top_sim = similarity_scores[0][1]

    # Logic
    threshold = 0.40
    delta = 0.05
    max_categories = 4

    matched = [
        cat for cat, sim in similarity_scores
        if sim >= threshold or sim >= (top_sim - delta)
    ]

    if not matched:
        matched = [similarity_scores[0][0]]

    return matched[:max_categories]

# ──────────────────────────────────────────────────────────────────────────
# Main course generation
# ──────────────────────────────────────────────────────────────────────────


def generate_course(req: CourseGenerationRequest) -> Course:
    context_chunks = _retrieve_context(req.prompt, k=5)
    context = "\n".join(context_chunks)

    existing_skills_text = (
        ", ".join(req.existing_skills)
        if req.existing_skills else "None"
    )

    messages = [
        {
            "role": "system",
            "content": "You are an expert curriculum designer. Your job is to generate self-contained, structured course content tailored to the learner's needs and prior experience."
        },
        {
        "role": "user",
        "content": (
            f"Context:\n{context}\n\n"
                f"Learning Goal: \"{req.prompt}\"\n\n"
                f"The user already knows the following skills and does NOT want to cover them again: {existing_skills_text}\n"
                "→ Do NOT include lessons that teach these skills.\n"
                "→ If these skills are relevant to the course flow, briefly acknowledge them and state they are already mastered.\n\n"
                "Now, generate exactly one JSON object matching the Course schema.\n"
                "Each lesson's `content.type` must be either 'TEXT' or 'HTML'.\n"
                "Lesson `content.content` must include detailed instructional material, not summaries, bullet points, or URLs.\n"
                "Ensure the course is complete, structured, and educational as if being taught in a real class."
            )
        },
    ]

    parsed: Course = generate_structured(messages, Course)

    # Set base metadata
    parsed.instructor = "SkillForge GenAI"
    parsed.published = False
    parsed.isPublic = False
    parsed.rating = 0.0
    parsed.language = "EN"
    parsed.thumbnailUrl = "https://i.imgur.com/BRsKn1L.png"

    # Normalize level
    allowed_levels = {"BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"}
    lvl = str(parsed.level).upper().replace(" ", "_") if parsed.level else "BEGINNER"
    if lvl not in allowed_levels:
        logger.warning(f"Unknown level '{parsed.level}', falling back to BEGINNER")
        lvl = "BEGINNER"
    parsed.level = lvl

    # Category detection
    parsed.categories = _infer_categories(parsed, req.prompt)

    # Normalize content.type
    for mod in parsed.modules:
        for lesson in mod.lessons:
            lesson.content.type = lesson.content.type.upper().replace(" ", "_")
            if lesson.content.type not in {"TEXT", "HTML", "URL", "VIDEO", "AUDIO", "IMAGE"}:
                logger.warning(f"Unknown lesson content type '{lesson.content.type}', defaulting to TEXT")
                lesson.content.type = "TEXT"

    # Optional: Post-check – warn if any known skills are still included
    for skill in req.existing_skills:
        if skill.lower() in (s.lower() for s in parsed.skills):
            logger.warning(f"Generated course includes known skill '{skill}'")

    return parsed
