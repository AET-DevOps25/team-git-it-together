from pydantic import BaseModel
from typing import List

# ──────────────────────────────────────────────────────────────────────────
# Incoming request  ➜  /api/v1/rag/generate-course
# ──────────────────────────────────────────────────────────────────────────
class CourseGenerationRequest(BaseModel):
    """Payload from course-service (goal + skills to skip)."""
    prompt: str
    existing_skills: List[str] = []
