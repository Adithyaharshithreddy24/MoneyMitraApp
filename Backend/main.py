from fastapi import FastAPI, UploadFile, File
from fastapi.responses import RedirectResponse
from pydantic import BaseModel
from typing import List
import shutil
import os
import resend

from gemini_service import extract_receipt_data, analyze_notification

app = FastAPI()

# =============================
# CONFIG
# =============================

resend.api_key = os.getenv("RESEND_API_KEY")

# =============================
# MODELS
# =============================

class Member(BaseModel):
    id: str
    name: str
    email: str
    amount: int
    managerName: str
    managerUpi: str

class RequestData(BaseModel):
    members: List[Member]

# =============================
# HEALTH CHECK
# =============================

@app.get("/")
def home():
    return {"status": "running"}

# =============================
# RECEIPT SCANNER
# =============================

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

# =============================
# NOTIFICATION ANALYZER
# =============================

@app.post("/analyze-notification")
async def analyze_notification_api(data: dict):

    message = data["text"]
    result = analyze_notification(message)

    return result

# =============================
# SEND EMAIL 
# =============================

@app.post("/send-reminder")
def send_reminder(data: RequestData):

    for m in data.members:

        upi_link = f"https://moneymitraapp.onrender.com/pay?pa={m.managerUpi}&pn={m.managerName}&am={m.amount}"

        html = f"""
        <h2>Hello {m.name}</h2>

        <p><b>{m.managerName}</b> has requested your chit payment.</p>

        <p><b>Amount Due:</b> ₹{m.amount}</p>

        <a href="{upi_link}" 
           style="padding:12px 20px;background:green;color:white;text-decoration:none;border-radius:8px;">
           Pay Now
        </a>

        <p>If button doesn't work, pay to: <b>{m.managerUpi}</b></p>
        """

        resend.Emails.send({
            "from": "MoneyMitra <onboarding@resend.dev>",
            "to": [m.email],
            "subject": "Chit Payment Reminder",
            "html": html
        })

    return {"success": True}

# =============================
# UPI REDIRECT
# =============================

@app.get("/pay")
def pay(pa: str, pn: str, am: int):

    upi_url = f"upi://pay?pa={pa}&pn={pn}&am={am}&cu=INR"

    return RedirectResponse(url=upi_url)