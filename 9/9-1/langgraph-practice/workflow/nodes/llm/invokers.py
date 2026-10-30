from typing import Any, Dict, TypedDict
from workflow.nodes.llm.entities import LLM
import ollama

class AgentState(TypedDict):
    messages: list
    output: str

def llm_invoke(state: AgentState, llm: LLM) -> Dict[str, Any]:
    sys_msg = [{"role": "system", "content": p} for p in llm.system_prompts]
    state["messages"].append({"role": "user", "content": state["messages"]})

    response = ollama.chat(
        model='gemma4:e2b',
        messages= sys_msg + state["messages"],
        options={"temperature": llm.temperature},
    )

    return {"output": response['message'].content}