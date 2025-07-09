from pydantic import BaseModel
from typing import Optional, List

# ──────────────────────────────────────────────────────────────────────────
# Incoming request  ➜  /api/v1/rag/generate-course
# ──────────────────────────────────────────────────────────────────────────
class CourseGenerationRequest(BaseModel):
    """Payload from course-service (goal + skills to skip)."""
    prompt: str
    existing_skills: List[str] = []

class LessonContent(BaseModel):
    type: str        # e.g. TEXT, VIDEO, URL
    content: str     # raw text or link

class Lesson(BaseModel):
    title: str
    description: str
    content: LessonContent
    order: int

class Module(BaseModel):
    title: str
    description: str
    lessons: List[Lesson]
    order: int


class Course(BaseModel):
    title: str
    description: str
    instructor: str
    skills: List[str]
    modules: List[Module]
    level: str
    thumbnailUrl: Optional[str] = None
    published: bool = False
    isPublic: bool = False
    language: str = "EN"
    rating: float = 0.0
    categories: List[str] = []

    model_config = {
        "extra": "forbid",
        "json_schema_extra": {"additionalProperties": False},
    }