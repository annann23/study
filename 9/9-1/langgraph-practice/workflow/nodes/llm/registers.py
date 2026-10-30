from functools import partial
import workflow.nodes.llm.invokers as llm_invokers
from workflow.nodes.llm.entities import LLM
from langgraph.graph import StateGraph

def add_llm_node(
    graph_builder: StateGraph,
    llm: LLM
) -> StateGraph:

    graph_builder.add_node(llm.type.value, partial(llm_invokers.llm_invoke, llm=llm))
    return graph_builder
