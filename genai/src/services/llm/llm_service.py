# genai/src/services/llm/llm_service.py

import os
from langchain_openai import ChatOpenAI
from langchain_community.llms import FakeListLLM
from langchain_core.language_models.base import BaseLanguageModel

def llm_factory() -> BaseLanguageModel:
    """
    Factory function to create and return an LLM instance based on the provider
    specified in the environment variables.
    """
    provider = os.getenv("LLM_PROVIDER", "dummy").lower()
    print(f"--- Creating LLM for provider: {provider} ---")

    if provider == "openai":
        if not os.getenv("OPENAI_API_KEY"):
            raise ValueError("OPENAI_API_KEY is not set for the 'openai' provider.")
        # Returns a high-quality chat model from OpenAI
        return ChatOpenAI(model="gpt-4o-mini", temperature=0.7)
    
    elif provider == "dummy":
        # This is a fake LLM for testing. It will cycle through these responses.
        responses = [
            "The first summary from the dummy LLM is about procedural languages.",
            "The second summary is about object-oriented programming.",
            "This is a fallback response.",
        ]
        return FakeListLLM(responses=responses)
    
    # In the future, you could add other providers like 'ollama' here
    # elif provider == "ollama":
    #     return ChatOllama(model="llama3")

    else:
        raise ValueError(f"Unsupported LLM provider: {provider}")

def generate_text(prompt: str) -> str:
    """
    Generates a text completion for a given prompt using the configured LLM.
    """
    # 1. Get the correct LLM instance from our factory
    llm = llm_factory()
    
    # 2. Invoke the LLM with the prompt
    #    (The .invoke() method is standard across all LangChain models)
    response = llm.invoke(prompt)

    # 3. The response object's structure can vary slightly by model.
    #    For Chat models, the text is in the .content attribute.
    #    For standard LLMs (like our FakeListLLM), it's the string itself.
    if hasattr(response, 'content'):
        return response.content
    else:
        return response