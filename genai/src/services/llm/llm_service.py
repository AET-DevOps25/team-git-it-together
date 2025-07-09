import os
from langchain_openai import ChatOpenAI
import json
import logging
from langchain_community.llms import FakeListLLM
from langchain_core.language_models.base import BaseLanguageModel
from typing import List, Type, TypeVar
from pydantic import BaseModel, ValidationError

logger = logging.getLogger(__name__)

def llm_factory() -> BaseLanguageModel:
    """
    Factory function to create and return an LLM instance based on the provider
    specified in the environment variables.
    Supports OpenAI, OpenAI-compatible (local/llmstudio), and dummy models.
    """
    provider = os.getenv("LLM_PROVIDER", "dummy").lower()
    logger.info(f"--- Creating LLM for provider: {provider} ---")

    if provider in ("openai", "llmstudio", "local"):
        # Get API base and key from env
        openai_api_key = os.getenv("OPENAI_API_KEY", "sk-xxx-dummy-key")
        openai_api_base = os.getenv("OPENAI_API_BASE", "https://api.openai.com/v1")

        model = os.getenv("OPENAI_MODEL", "gpt-4o-mini")
        return ChatOpenAI(
            model=model,
            temperature=0.7,
            openai_api_key=openai_api_key,
            openai_api_base=openai_api_base
        )
    
    elif provider == "dummy":
        responses = [
            "The first summary from the dummy LLM is about procedural languages.",
            "The second summary is about object-oriented programming.",
            "This is a fallback response.",
        ]
        return FakeListLLM(responses=responses)

    else:
        raise ValueError(f"Currently Unsupported LLM provider: {provider}")

LLM_SINGLETON = llm_factory()

def generate_text(prompt: str) -> str:
    """
    Generates a text completion for a given prompt using the configured LLM.
    """
    # 1. Get the correct LLM instance from our factory
    llm = LLM_SINGLETON

    # if we using local LLM, we need to append "/no_think" in case the model is a thinking model
    if os.getenv("LLM_PROVIDER", "dummy").lower() == "llmstudio" and hasattr(llm, 'model_name'):
            prompt += "/no_think"
    
    # 2. Invoke the LLM with the prompt
    response = llm.invoke(prompt)

    # 3. The response object's structure can vary slightly by model.
    #    For Chat models, the text is in the .content attribute.
    #    For standard LLMs (like our FakeListLLM), it's the string itself.
    if hasattr(response, 'content'):
        return response.content
    else:
        return response