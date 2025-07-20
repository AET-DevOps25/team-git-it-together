# tests/conftest.py
import os
import pytest
import types

# Before importing the app, stub out everything in the startup lifespan:
import src.main as main

# 1) Prevent real Weaviate client creation & readiness checks
main.get_weaviate_client = lambda: object()
main.test_weaviate_connection = lambda *args, **kwargs: None
main.ensure_schema_exists = lambda client: None

# 2) Prevent scheduler from starting background threads
main.start_scheduler = lambda *args, **kwargs: None
main.stop_scheduler = lambda *args, **kwargs: None

# 3) Stub any other heavy ops (optional)
# e.g. if you get import errors for langchain_huggingface, stub it here:
import sys
sys.modules['langchain_huggingface'] = types.SimpleNamespace(
    HuggingFaceEmbeddings=lambda *a, **k: None
)

# Now import pytest fixtures
from fastapi.testclient import TestClient

@pytest.fixture(scope="session", autouse=True)
def _set_test_env():
    # Safe defaults
    os.environ.setdefault("LLM_PROVIDER", "dummy")
    os.environ.setdefault("OPENAI_API_KEY", "sk-test")
    os.environ.setdefault("WEAVIATE_HOST", "fakehost")
    return

@pytest.fixture
def client():
    """FastAPI TestClient with in‐process app and stubbed lifespan."""
    # Import the app after we've monkeypatched everything above
    from src.main import app
    with TestClient(app) as c:
        yield c
