import asyncio
import pytest
from src.services.crawler import crawler_service as cs

def test_fetch_invalid_scheme():
    with pytest.raises(Exception):
        asyncio.run(cs.fetch_and_clean_page("ftp://example.com"))

def test_fetch_and_cache(monkeypatch, tmp_path):
    html = "<html><body><h1>Hello</h1><script>evil()</script></body></html>"

    async def fake_get(*args, **kwargs):
        class R:
            text = html
            def raise_for_status(self): pass
        return R()

    monkeypatch.setattr("httpx.AsyncClient.get", fake_get)

    # First fetch
    result1 = asyncio.run(cs.fetch_and_clean_page("http://example.com"))
    assert "Hello" in result1["text"] and "evil" not in result1["text"]

    # Second fetch hits cache
    result2 = asyncio.run(cs.fetch_and_clean_page("http://example.com"))
    assert result2 == result1
