import os
import pytest
from fastapi.testclient import TestClient
from src.main import app

@pytest.fixture(scope="session", autouse=True)
def _set_test_env():
    """Ensure safe defaults for external services."""
    os.environ.setdefault("LLM_PROVIDER", "dummy")          # no real tokens hit
    os.environ.setdefault("OPENAI_API_KEY", "sk-test")      # harmless
    os.environ.setdefault("WEAVIATE_HOST", "fakehost")      # we’ll mock

@pytest.fixture
def client():
    """FastAPI TestClient with in‑process app."""
    with TestClient(app) as c:
        yield c
