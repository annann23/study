import operator
from typing import Annotated, Any, Dict, TypedDict
from workflow.nodes.llm.entities import LLM
import ollama

class AgentState(TypedDict):
    messages: Annotated[list, operator.add]
    output: str

def llm_invoke(state: AgentState, llm: LLM) -> Dict[str, Any]:
    sys_msg = [{"role": "system", "content": p} for p in llm.system_prompts]

    response = ollama.chat(
        model='gemma4:e2b',
        messages= sys_msg + state["messages"],
        options={"temperature": llm.temperature},
    )

    answer = response['message'].content
    return {
        "messages": [{"role": "assistant", "content": answer}],
        "output": answer,
    }