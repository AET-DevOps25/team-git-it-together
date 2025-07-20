# genai/tests/test_course_gen.py
import pytest
from src.services.rag import course_generator as cg
from src.services.rag.schemas import CourseGenerationRequest, Course

def test_course_generation(monkeypatch):
    # 1) Never call Weaviate / vector DB for context
    monkeypatch.setattr(cg, "_retrieve_context", lambda query, k=5: ["dummy context"])

    # 2) Never call embed_text / cosine for categories
    monkeypatch.setattr(cg, "_infer_categories", lambda course, prompt="": ["TestCat"])

    # 3) Provide only the minimal JSON fields; instructor will be replaced anyway
    dummy = {
        "title": "Test Course",
        "description": "A test",
        "instructor": "Someone Else",  # this gets overwritten by generate_course
        "skills": ["Skill1"],
        "modules": [
            {
                "title": "Module 1",
                "description": "Desc",
                "order": 1,
                "lessons": [
                    {
                        "title": "Lesson 1",
                        "description": "Desc",
                        "order": 1,
                        "content": {"type": "TEXT", "content": "Hello"}
                    }
                ]
            }
        ],
        "level": "Beginner",
        "categories": []  # initial categories, will get replaced by our stub
    }

    # 4) Stub out the LLM structured call to return our dummy
    monkeypatch.setattr(
        cg, "generate_structured",
        lambda messages, schema: schema.model_validate(dummy)
    )

    # 5) Run the generator
    req = CourseGenerationRequest(prompt="Learn X", existing_skills=["Skill1"])
    course: Course = cg.generate_course(req)

    # 6) Assert the post‑process behavior
    assert course.title == "Test Course"
    # Instructor is always set to "SkillForge GenAI"
    assert course.instructor == "SkillForge GenAI"
    # Our stubbed category logic
    assert course.categories == ["TestCat"]
    # And content-type detection remained TEXT
    assert course.modules[0].lessons[0].content.type == "TEXT"
