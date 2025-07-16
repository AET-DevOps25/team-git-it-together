import logging
from typing import List

from .schemas import CourseGenerationRequest, Course
from ..embedding import embedder_service
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
           "content": """You are an expert curriculum designer and technical writer specializing in creating comprehensive, structured learning content. Your expertise lies in:


1. **Structured Learning Design**: Creating logical learning progressions with clear objectives
2. **Markdown Content Creation**: Writing rich, well-formatted instructional content using proper markdown syntax
3. **Technical Communication**: Explaining complex concepts in clear, accessible language
4. **Practical Application**: Including hands-on exercises, examples, and real-world scenarios


**Content Requirements:**
- Use proper markdown formatting (headers, lists, code blocks, emphasis, links)
- Include practical examples and code snippets where relevant
- Structure content with clear sections and subsections
- Provide actionable steps and exercises
- Use tables for comparisons and structured information
- Include relevant images, diagrams, or video references when helpful


**Course Structure Guidelines:**
- Each lesson should be self-contained but build upon previous lessons
- Include clear learning objectives for each module
- Provide comprehensive explanations, not just summaries
- Use progressive complexity (simple to advanced concepts)
- Include practical exercises and real-world applications"""
       },
       {
       "role": "user",
       "content": (
           f"**Context Information:**\n{context}\n\n"
               f"**Learning Goal:** \"{req.prompt}\"\n\n"
               f"**Existing Skills (Skip These):** {existing_skills_text}\n"
               "→ **DO NOT** include lessons that teach these skills\n"
               "→ If these skills are relevant to the course flow, briefly acknowledge them and state they are already mastered\n\n"
               "**Content Generation Instructions:**\n"
               "1. Generate exactly one JSON object matching the Course schema\n"
               "2. For `content.content`, use one of these formats:\n"
               "   - **Markdown text** for regular lessons (will be set to TEXT type)\n"
               "   - **HTML content** for rich interactive lessons (will be set to HTML type)\n"
               "   - **YouTube URLs** for video lessons (will be set to VIDEO type)\n"
               "3. For markdown content, use **rich markdown formatting** including:\n"
               "   - **Headers** (##, ###, ####) for clear section organization\n"
               "   - **Bold** and *italic* text for emphasis\n"
               "   - **Code blocks** with syntax highlighting for examples\n"
               "   - **Numbered and bulleted lists** for step-by-step instructions\n"
               "   - **Tables** for comparisons and structured data\n"
               "   - **Blockquotes** for important notes and tips\n"
               "   - **Links** to relevant resources when appropriate\n\n"
               "4. **Skill Generation Guidelines:**\n"
               "   - Generate 1-3 diverse skills that cover the full topic breadth\n"
               "   - Skills should be 1-2 words long\n"
               "   - Include foundational, intermediate, and advanced concepts\n"
               "   - Mix theoretical knowledge with practical application\n"
               "   - Avoid focusing too heavily on one specific aspect\n"
               "   - Skills should be specific but not overly narrow\n"
               "   - Examples: 'JavaScript Fundamentals', 'DOM Manipulation', 'Event Handling', 'API Integration', 'Error Handling', 'Performance Optimization', 'Testing & Debugging'\n\n"
               "5. **Content Quality Standards:**\n"
               "   - Provide comprehensive, detailed explanations\n"
               "   - Include practical examples and real-world scenarios\n"
               "   - Add hands-on exercises and coding challenges\n"
               "   - Use clear, professional language\n"
               "   - Structure content for optimal learning flow\n\n"
               "6. **Ensure the course is complete, structured, and educational as if being taught in a real classroom setting**"
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


   # Set content.type based on content analysis
   for mod in parsed.modules:
       for lesson in mod.lessons:
           content = lesson.content.content.strip()
          
           # Detect content type based on content
           if content.startswith('http') and ('youtube.com' in content or 'youtu.be' in content):
               lesson.content.type = "VIDEO"
               logger.info(f"Set lesson '{lesson.title}' content type to VIDEO (YouTube URL detected)")
           elif any(tag in content for tag in ['<div', '<html', '<body', '<section', '<article', '<header', '<footer', '<nav', '<main']):
               lesson.content.type = "HTML"
               logger.info(f"Set lesson '{lesson.title}' content type to HTML (HTML tags detected)")
           else:
               # Default to TEXT for markdown content
               lesson.content.type = "TEXT"
               logger.info(f"Set lesson '{lesson.title}' content type to TEXT (markdown content)")


   # Optional: Post-check – warn if any known skills are still included
   for skill in req.existing_skills:
       if skill.lower() in (s.lower() for s in parsed.skills):
           logger.warning(f"Generated course includes known skill '{skill}'")


   return parsed
