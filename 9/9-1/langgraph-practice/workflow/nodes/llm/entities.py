from dataclasses import dataclass
from workflow.types import NodeType

@dataclass
class LLM():
    type: NodeType = NodeType.LLM
    model_provider: str
    model_name: str
    temperature: float
    system_prompts: list[str]