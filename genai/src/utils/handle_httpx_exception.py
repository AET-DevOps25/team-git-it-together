from fastapi import HTTPException
import httpx
import logging

def handle_httpx_exception(url: str, exc: Exception) -> HTTPException:
    """
    Maps httpx exceptions to FastAPI HTTPException with appropriate status code and detail.
    Usage: except Exception as e: raise handle_httpx_exception(url, e)
    """
    logger = logging.getLogger("skillforge.genai.httpx_helper")
    if isinstance(exc, httpx.HTTPStatusError):
        status_code = exc.response.status_code
        msg = exc.response.text[:250] if exc.response is not None else str(exc)
        logger.error(f"HTTP error while fetching '{url}': {status_code} - {msg}")
        return HTTPException(
            status_code=status_code,
            detail=f"Failed to fetch '{url}': {msg}"
        )
    elif isinstance(exc, httpx.TimeoutException):
        logger.error(f"Timeout occurred while fetching '{url}'.")
        return HTTPException(
            status_code=504,  # Gateway Timeout
            detail=f"Timeout occurred while fetching '{url}'"
        )
    elif isinstance(exc, httpx.RequestError):
        logger.error(f"Network error while fetching '{url}': {exc}")
        return HTTPException(
            status_code=502,  # Bad Gateway
            detail=f"Network error while fetching '{url}': {str(exc)}"
        )
    else:
        logger.error(f"Unexpected error while fetching '{url}': {exc}", exc_info=True)
        return HTTPException(
            status_code=500,
            detail=f"Unexpected error while fetching '{url}': {str(exc)}"
        )
