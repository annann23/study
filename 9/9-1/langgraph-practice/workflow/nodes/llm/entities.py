from dataclasses import dataclass
from workflow.types import NodeType

@dataclass
class LLM():
    type: NodeType = NodeType.LLM
    model_provider: str = "ollama"
    model_name: str = "gemma4:e4b"
    temperature: float = 0.7
    system_prompts: list[str] = ["너는 친절한 어시스턴트야. 한국말로 답변해."]