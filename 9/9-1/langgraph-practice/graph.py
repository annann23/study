from typing import TypedDict
from langgraph.graph import StateGraph, START, END
from ollama import chat

class AgentState:
    messages: list

graph_builder = StateGraph(AgentState)

graph_builder.add_node("llm", lambda state: {"messages": [chat(state.messages)]})
graph_builder.add_edge(START, "llm")
graph_builder.add_edge("llm", END)