from fastapi import FastAPI, UploadFile, File
import shutil
import os
from gemini_service import extract_receipt_data

app = FastAPI()

@app.post("/scan-receipt")
async def scan_receipt(file: UploadFile = File(...)):

    path = f"temp_{file.filename}"

    # Save temporary file
    with open(path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        data = extract_receipt_data(path)

    finally:
        # Delete image after processing
        if os.path.exists(path):
            os.remove(path)

    return {
        "name": data["name"],
        "amount": data["amount"],
        "category": data["category"],
        "note": data["note"],
        "type": "EXPENSE",
        "createdAt": data["createdAt"]
    }