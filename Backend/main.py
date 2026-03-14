from fastapi import FastAPI, UploadFile, File
import shutil
import os
from gemini_service import extract_receipt_data, analyze_notification

app = FastAPI()


# -----------------------------
# Receipt Scanner API
# -----------------------------
@app.post("/scan-receipt")
async def scan_receipt(file: UploadFile = File(...)):

    path = f"temp_{file.filename}"

    with open(path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        data = extract_receipt_data(path)

    finally:
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


# -----------------------------
# Notification Analyzer API
# -----------------------------
@app.post("/analyze-notification")
async def analyze_notification_api(data: dict):

    message = data["text"]

    result = analyze_notification(message)

    return result