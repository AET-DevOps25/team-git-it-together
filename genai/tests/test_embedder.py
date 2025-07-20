# genai/tests/test_embedder.py
import os
from src.services.embedding import embedder_service as es

def test_chunking(monkeypatch):
    # 1) Force the "local" branch (so it uses HuggingFaceEmbeddings, not OpenAI)
    monkeypatch.setenv("LLM_PROVIDER", "local")

    # 2) Stub out readiness check entirely
    monkeypatch.setattr(es, "wait_for_weaviate_ready", lambda *args, **kwargs: True)

    # 3) Stub get_weaviate_client so we never hit real network
    monkeypatch.setattr(es, "get_weaviate_client", lambda: object())

    # 4) Dummy HF embeddings
    class DummyHuggingFaceEmbeddings:
        def __init__(self, *args, **kwargs): pass
        def embed_query(self, text): return [0.1] * 8

    monkeypatch.setattr(es, "HuggingFaceEmbeddings", DummyHuggingFaceEmbeddings)

    # 5) Dummy Weaviate class with classmethod from_texts()
    class DummyWeaviate:
        @classmethod
        def from_texts(cls, client, index_name, texts, embedding, metadatas, text_key):
            # Verify that client is our dummy
            assert client is not None
            return object()

    monkeypatch.setenv("LLM_PROVIDER", "local")
    monkeypatch.setattr(es, "Weaviate", DummyWeaviate)

    # Now run
    long_text = "A" * 1500
    num_chunks = es.embed_and_store_text(long_text, "http://example.com")

    # We must have split into more than one chunk
    assert num_chunks > 1
