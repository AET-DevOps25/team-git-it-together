# GenAI Service

This document provides documentation for the GenAI service components of SkillForge.

## Web Crawler Service

This service provides an API endpoint to fetch, clean, and cache web content.

### Endpoint: `POST /crawl`

Accepts a URL and returns the cleaned text content. It uses a file-based cache in `genai/tmp/crawled_pages` to avoid re-fetching URLs.

**Request Body:**

```json
{
  "url": "https://example.com/some/article"
}
```

**Success Response (First Time):**

```json
{
  "url": "https://example.com/some/article",
  "text": "The cleaned text of the article...",
  "message": "Content fetched, cleaned, and cached successfully."
}
```

**Success Response (From Cache):**

```json
{
  "url": "https://example.com/some/article",
  "text": "The cleaned text of the article...",
  "message": "Content retrieved from cache."
}
```

**Error Response:**

If a URL cannot be reached, the API will return a 400 Bad Request with details in the response body:

```json
{
  "detail": "Failed to fetch URL: <error details>"
}
```

For other unexpected errors, it will return a 500 Internal Server Error:

```json
{
  "detail": "An unexpected error occurred: <error details>"
}
```

### Implementation Details

- Uses `httpx` for HTTP requests with proper timeout and redirect handling
- Employs `BeautifulSoup` for HTML parsing and content cleaning
- Removes irrelevant HTML elements like scripts, styles, navigation, etc.
- Implements file-based caching using MD5 hashes of URLs as identifiers
- Cache location: `genai/tmp/crawled_pages/`

### Usage in Embedding Pipeline

The crawler service is designed to be used as part of the content processing pipeline:
1. Fetch and clean web content using the crawler
2. Process the cleaned text with the LLM
3. Generate embeddings for the processed content
4. Store embeddings in Weaviate for retrieval
