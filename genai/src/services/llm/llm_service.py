# genai/src/services/llm/llm_service.py
import os
import json
import logging
from typing import List, Type, TypeVar, Dict, Any

from pydantic import BaseModel, ValidationError
from langchain_openai import ChatOpenAI
from langchain_community.llms import FakeListLLM
from langchain_core.language_models.base import BaseLanguageModel

logger = logging.getLogger(__name__)

T = TypeVar("T", bound=BaseModel)

# ──────────────────────────────────────────────────────────────────────────
# LLM factory
# ──────────────────────────────────────────────────────────────────────────

def llm_factory() -> BaseLanguageModel:
    """Return a singleton LangChain LLM according to $LLM_PROVIDER."""
    provider = os.getenv("LLM_PROVIDER", "dummy").lower()
    logger.info(f"--- Creating LLM for provider: {provider} ---")

    if provider in ("lmstudio", "llmstudio"):
        return ChatOpenAI(
            model=os.getenv("OPENAI_MODEL", "qwen/Qwen1.5-7B-Chat"),
            temperature=0.7,
            openai_api_key=os.getenv("OPENAI_API_KEY", "sk-local"),
            openai_api_base=os.getenv("OPENAI_API_BASE", "http://127.0.0.1:1234/v1"),
        )

    if provider in ("openai", "llmstudio", "local"):
        openai_api_key = os.getenv("OPENAI_API_KEY", "sk-xxx-dummy-key")
        openai_api_base = os.getenv("OPENAI_API_BASE", "https://api.openai.com/v1")
        model = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
        return ChatOpenAI(
            model=model,
            temperature=0.7,
            openai_api_key=openai_api_key,
            openai_api_base=openai_api_base,
        )

    if provider == "dummy":
        responses = [
            "The first summary from the dummy LLM is about procedural languages.",
            "The second summary is about object-oriented programming.",
            "This is a fallback response.",
        ]
        return FakeListLLM(responses=responses)

    raise ValueError(f"Unsupported LLM provider: {provider}")


LLM_SINGLETON = llm_factory()

# ──────────────────────────────────────────────────────────────────────────
# Convenience helpers
# ──────────────────────────────────────────────────────────────────────────

def generate_text(prompt: str) -> str:
    """Simple text completion (legacy helper)."""
    llm = LLM_SINGLETON

    if os.getenv("LLM_PROVIDER", "dummy").lower() == "llmstudio" and hasattr(llm, "model_name"):
        prompt += "/no_think"

    response = llm.invoke(prompt)
    return response.content if hasattr(response, "content") else response


def generate_structured(messages: List[dict], schema: Type[T], *, max_retries: int = 3) -> T:
    """Return a Pydantic object *schema* regardless of the underlying provider.

    1. For $LLM_PROVIDER==openai we use the native `beta.chat.completions.parse` API.
    2. Otherwise we fall back to strict JSON prompting and `model_validate_json()`.
    """
    provider = os.getenv("LLM_PROVIDER", "dummy").lower()

    # ── 1. Native OpenAI JSON mode ───────────────────────────────────────
    if provider == "openai":
        try:
            from openai import OpenAI  # local import to avoid hard dep for other providers

            client = OpenAI(
                api_key=os.getenv("OPENAI_API_KEY"),
                base_url=os.getenv("OPENAI_API_BASE", "https://api.openai.com/v1"),
            )
            response = client.beta.chat.completions.parse(
                model=os.getenv("OPENAI_MODEL", "gpt-4o-mini"),
                messages=messages,
                response_format=schema,
            )
            return response.choices[0].message.parsed  # type: ignore[arg-type]
        except Exception as e:
            logger.warning(f"OpenAI structured parse failed – falling back: {e}")

    # --- 2. Attempt OpenAI-compatible function calling ---
    # This path uses the OpenAI function-calling API, which is also supported by LM Studio and other compatible providers.
    # It allows us to explicitly define the expected schema for the model output, improving reliability for structured responses.
    try:
        from openai import OpenAI  # lightweight; already installed for parse()

        client = OpenAI(
            api_key=os.getenv("OPENAI_API_KEY", "dummy"),
            base_url=os.getenv("OPENAI_API_BASE", "http://127.0.0.1:1234/v1"),
        )

        func_def: Dict[str, Any] = {
            "name": "generate_json",
            "description": f"Return ONE JSON object that matches the `{schema.__name__}` schema.",
            "parameters": schema.model_json_schema(),
        }

        resp = client.chat.completions.create(
            model=os.getenv("OPENAI_MODEL"),
            messages=messages,
            functions=[func_def],
            function_call={"name": "generate_json"},
        )

        # the JSON is returned in message.function_call.arguments
        fc_args = resp.choices[0].message.function_call.arguments
        raw_json = fc_args if isinstance(fc_args, str) else json.dumps(fc_args)

        return schema.model_validate_json(raw_json)

    except Exception as e:
        logger.warning(f"Function‑calling failed – falling back to manual JSON prompt: {e}")

    # --- 3. Fallback: Prompt for strict JSON output ---
    # If both OpenAI-native parsing and function-calling fail, we fall back to a strict prompt instructing the model to return only valid JSON.
    # This is a last-resort method and less robust, but ensures we attempt to recover structured output if all else fails.
    system_json_guard = {
        "role": "system",
        "content": (
            "You are a JSON‑only assistant. Output **only** valid JSON conforming to "
            f"this schema (no markdown, no code fences):\n{json.dumps(schema.model_json_schema())}"
        ),
    }
    convo: List[dict] = [system_json_guard] + messages

    llm = LLM_SINGLETON
    for attempt in range(1, max_retries + 1):
        raw = llm.invoke(convo)
        text = raw.content if hasattr(raw, "content") else raw
        try:
            return schema.model_validate_json(text)
        except ValidationError:
            convo.append({"role": "assistant", "content": text})
            convo.append({"role": "user", "content": "🔁 JSON invalid. Resend *only* corrected JSON."})

    raise ValueError("Failed to get valid structured output after retries")
