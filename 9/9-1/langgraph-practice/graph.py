from langgraph.graph import StateGraph, START, END
import workflow.nodes.llm.registers as llm_registers
import workflow.nodes.llm.invokers as llm_invokers
import workflow.nodes.llm.entities as llm_entities
from langgraph.checkpoint.memory import MemorySaver

graph_builder = StateGraph(llm_invokers.AgentState)

llm = llm_entities.LLM(type=llm_entities.NodeType.LLM)

llm_registers.add_llm_node(graph_builder, llm)

graph_builder.add_edge(START, llm.type.value)
graph_builder.add_edge(llm.type.value, END)

graph = graph_builder.compile(checkpointer=MemorySaver())
config = {"configurable": {"thread_id": "1"}}

while True:
    user_input = input("입력: ")
    if user_input in ("exit", "quit", "종료"):
        break
    result = graph.invoke({"messages": [{"role": "user", "content": user_input}]}, config=config)
    print("답변:", result["output"])
