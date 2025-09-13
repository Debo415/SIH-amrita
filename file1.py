# backend.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
import json

app = FastAPI()

# Allow frontend running on localhost:8000 to fetch data
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # You can restrict to your frontend origin
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load dataset
with open("dataset.json", "r", encoding="utf-8") as f:
    dataset = json.load(f)


# Helper: search in a category
def search_category(category: str, query: str):
    query_lower = query.lower()
    if category not in dataset:
        return {"error": f"Category '{category}' not found."}

    results = []
    for item in dataset[category]:
        # For food and gadgets: search by 'name'
        # For clothes: search by 'fabric' (you can expand later)
        if category == "clothes":
            if query_lower in str(item.get("fabric", "")).lower():
                results.append(item)
        else:
            if query_lower in str(item.get("name", "")).lower():
                results.append(item)

    if not results:
        return {"error": f"No results found for '{query}' in '{category}'."}

    return results


# Endpoint: search by category
@app.get("/{category}")
def search(category: str, name: str):
    x= search_category(category, name)
    return x


# Optional: root endpoint
@app.get("/")
def root():
    return {"message": "Sustainable Shopping Backend is running!"}
