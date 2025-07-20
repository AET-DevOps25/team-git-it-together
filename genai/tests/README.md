# Test Suite for *SkillForge GenAI*

> **Disclaimer:**  
> Most of these tests use mocks or stubs to isolate logic and avoid hitting real external services.  
> Only the FastAPI “ping” and validation tests exercise the real app wiring—everything else simulates dependencies.

## What’s covered?

| File                | Scope                  | What it asserts                                                   | Real vs. Mocked             |
|---------------------|------------------------|-------------------------------------------------------------------|-----------------------------|
| `test_api.py`       | Integration (FastAPI)  | Ping, validation errors, basic routing                            | **Real** (in‑process app)   |
| `test_crawler.py`   | Unit + I/O caching     | HTML cleanup, cache reuse, invalid URLs                           | Mocked HTTP client         |
| `test_embedder.py`  | Unit                   | Chunk splitting logic; bypasses Weaviate via dummy client/class   | Fully mocked (Weaviate)    |
| `test_course_gen.py`| Unit                   | Course post‑processing: instructor override, content‑type, categories | Fully mocked (LLM & DB)    |

## Running locally

```bash
# From the repo root (where pytest.ini lives)
pip install -r requirements.txt
pytest -q
