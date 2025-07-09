from pydantic import BaseModel, HttpUrl, Field
from typing import List, Optional


# ──────────────────────────────────────────────────────────────────────────
# Incoming request  ➜  /api/v1/rag/generate-course
# ──────────────────────────────────────────────────────────────────────────
class CourseGenerationRequest(BaseModel):
    """
    What the course-service sends us.

    • prompt          – free-text goal (“Become React dev in 30 days”)
    • existing_skills – skills we must *skip* (already mastered)
    """
    prompt: str
    existing_skills: List[str] = []


# ──────────────────────────────────────────────────────────────────────────
# Course domain model returned to course-service
# ──────────────────────────────────────────────────────────────────────────
class LessonContent(BaseModel):
    type: str  # e.g. TEXT, HTML, VIDEO, URL
    content: str  # actual content or URL

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
        "json_schema_extra": {"additionalProperties": False}
    }
