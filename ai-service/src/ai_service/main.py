import os
import requests
from typing import Optional
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from fastapi.openapi.utils import get_openapi
from pydantic import BaseModel, Field
from sentence_transformers import SentenceTransformer

from langchain_core.tools import tool
from langchain_google_genai import ChatGoogleGenerativeAI
from langgraph.prebuilt import create_react_agent

# ---------- Load env ----------
load_dotenv()

if not os.getenv("GOOGLE_API_KEY"):
    raise RuntimeError("GOOGLE_API_KEY is missing. Add it to your .env file.")

BACKEND_URL = os.getenv("BACKEND_URL", "http://localhost:8080")

# ---------- App with Swagger metadata ----------
app = FastAPI(
    title="MediConnect AI Service",
    description=(
        "AI microservice for **MediConnect**.\n\n"
        "### Features\n"
        "- 🔢 **Embeddings** — sentence-transformers (all-MiniLM-L6-v2, 384-dim)\n"
        "- 🤖 **Agent** — Gemini-powered ReAct agent that finds doctors by symptom\n"
        "- 🩺 **Doctor search** — semantic search via pgvector on the Spring Boot backend\n"
    ),
    version="1.0.0",
    contact={"name": "MediConnect Team", "email": "team@mediconnect.example"},
    license_info={"name": "MIT"},
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    openapi_tags=[
        {"name": "Embeddings", "description": "Convert text into a 384-dim vector."},
        {"name": "Agent",      "description": "Gemini ReAct agent with tool-calling."},
        {"name": "System",     "description": "Health & diagnostics."},
    ],
)

# ---------- Embedding model ----------
embedder = SentenceTransformer("all-MiniLM-L6-v2")


# ---------- Request / Response schemas ----------
class EmbedRequest(BaseModel):
    text: str = Field(
        ...,
        min_length=1,
        examples=["chest pain and shortness of breath"],
        description="Any non-empty text to embed.",
    )


class EmbedResponse(BaseModel):
    text: str
    dimension: int
    embedding: list[float]


class AgentRequest(BaseModel):
    query: str = Field(
        ...,
        min_length=1,
        examples=["I have severe chest pain and feel breathless, who should I see?"],
        description="Natural-language question. The agent will call tools as needed.",
    )


class DoctorMatch(BaseModel):
    """A single doctor matched by the agent (mirrors Spring Boot DoctorResponse)."""
    doctorId: int = Field(description="Unique doctor ID from the backend.")
    name: str = Field(description="Doctor's full name.")
    specialization: str = Field(description="Medical specialization.")
    qualification: Optional[str] = Field(default=None, description="Degrees / qualifications.")
    language: Optional[str] = Field(default=None, description="Languages spoken.")
    location: Optional[str] = Field(default=None, description="City / clinic location.")
    consultationFee: float = Field(description="Consultation fee as a number.")


class AgentStructuredResponse(BaseModel):
    """Final structured agent output."""
    summary: str = Field(
        description=(
            "Short human-friendly summary of the recommendation "
            "(2–4 sentences). If no doctors were found, say so clearly."
        )
    )
    doctorIds: list[int] = Field(
        default_factory=list,
        description="List of doctor IDs (integer) matched for this query. Empty if none.",
    )
    doctors: list[DoctorMatch] = Field(
        default_factory=list,
        description="Full details of each matched doctor. Empty if none.",
    )


class HealthResponse(BaseModel):
    status: str
    llm: str
    backend: str


# ---------- Endpoint 1: embeddings ----------
@app.post(
    "/embed",
    response_model=EmbedResponse,
    tags=["Embeddings"],
    summary="Generate a text embedding",
    description=(
        "Returns a **384-dimensional** L2-normalized vector from "
        "`sentence-transformers/all-MiniLM-L6-v2`."
    ),
)
def embed(req: EmbedRequest):
    if not req.text.strip():
        raise HTTPException(status_code=400, detail="text cannot be empty")

    vector = embedder.encode(req.text, normalize_embeddings=True).tolist()
    return {"text": req.text, "dimension": len(vector), "embedding": vector}


# ---------- Tools ----------
@tool
def find_doctor(symptoms: str) -> str:
    """
    Find the best matching doctors for a given symptom, disease, or medical need.
    Input should be a short natural-language query, e.g. 'chest pain and shortness of breath'
    or 'child with high fever'. Returns a JSON array of doctors with id, name,
    specialization, qualification, language, location, and consultationFee.
    """
    if not symptoms.strip():
        return "Error: symptom query cannot be empty."

    try:
        url = f"{BACKEND_URL}/api/v1/doctors/search"
        resp = requests.get(
            url,
            params={"q": symptoms, "limit": 3},
            timeout=15,
        )
        if resp.status_code == 404:
            resp = requests.get(
                f"{BACKEND_URL}/api/doctors/search",
                params={"q": symptoms, "limit": 3},
                timeout=15,
            )
        resp.raise_for_status()
        doctors = resp.json()
    except requests.RequestException as e:
        return f"Error contacting doctor service: {e}"

    if not doctors:
        return "[]"

    # Return raw JSON so the structured-output LLM can map it cleanly.
    import json
    return json.dumps(
        [
            {
                "doctorId": d.get("doctorId"),
                "name": d.get("name"),
                "specialization": d.get("specialization"),
                "qualification": d.get("qualification"),
                "language": d.get("language"),
                "location": d.get("location"),
                "consultationFee": d.get("consultationFee"),
            }
            for d in doctors
        ]
    )




# ---------- LLM + Agent (with structured output) ----------
llm = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    temperature=0,
)

tools = [find_doctor]

SYSTEM_PROMPT = (
    "You are a helpful medical assistant. Given a user's symptoms or health concern, "
    "call the find_doctor tool with a concise symptom summary. "
    "Then respond with a short summary and the list of matched doctors "
    "using the required structured format. "
    "If no doctors are found, return an empty doctorIds/doctors list and a clear summary."
)

agent = create_react_agent(
    llm,
    tools,
    prompt=SYSTEM_PROMPT,
    response_format=AgentStructuredResponse,
)


# ---------- Endpoint 2: agent ----------
@app.post(
    "/agent",
    response_model=AgentStructuredResponse,
    tags=["Agent"],
    summary="Run the medical assistant agent (structured output)",
    description=(
        "Runs the Gemini ReAct agent, then returns a **structured** response "
        "containing:\n"
        "- `summary` — short recommendation\n"
        "- `doctorIds` — list of matched doctor IDs\n"
        "- `doctors` — full detail objects for each match"
    ),
)
def run_agent(req: AgentRequest):
    if not req.query.strip():
        raise HTTPException(status_code=400, detail="query cannot be empty")

    try:
        result = agent.invoke({"messages": [("user", req.query)]})
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    structured = result.get("structured_response")
    if structured is None:
        # Fallback: model finished without a structured response
        return AgentStructuredResponse(
            summary=result["messages"][-1].content if result.get("messages") else "No response.",
            doctorIds=[],
            doctors=[],
        )
    return structured


# ---------- Health ----------
@app.get(
    "/health",
    response_model=HealthResponse,
    tags=["System"],
    summary="Health check",
)
def health():
    return {"status": "ok", "llm": "gemini-1.5-flash", "backend": BACKEND_URL}


# ---------- Custom OpenAPI ----------
def custom_openapi():
    if app.openapi_schema:
        return app.openapi_schema
    schema = get_openapi(
        title=app.title,
        version=app.version,
        description=app.description,
        routes=app.routes,
        tags=app.openapi_tags,
    )
    schema["servers"] = [{"url": "http://localhost:8000", "description": "Local dev"}]
    app.openapi_schema = schema
    return schema


app.openapi = custom_openapi