from typing import Any, Dict
from workflow.nodes.llm.entities import LLM
import ollama

class AgentState:
    messages: list

def llm_invoke(state: AgentState, llm: LLM) -> Dict[str, Any]:
    messages = [{"role": "system", "content": p} for p in llm.system_prompts]
    messages.append({"role": "user", "content": state["messages"]})

    response = ollama.chat(
        model='gemma4:e4b',
        messages= messages,
        options={"temperature": llm.temperature},
    )

    return {'message':  response['message']}