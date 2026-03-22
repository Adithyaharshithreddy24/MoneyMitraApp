from fastapi import FastAPI, UploadFile, File
from fastapi.responses import RedirectResponse
from pydantic import BaseModel
from typing import List
import shutil
import os
import base64
import json
from email.mime.text import MIMEText

from googleapiclient.discovery import build
from google.oauth2.credentials import Credentials

from gemini_service import extract_receipt_data, analyze_notification

app = FastAPI()

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
# EMAIL FUNCTION (ENV BASED)
# =============================

def send_email(to_email: str, subject: str, html: str):
    try:
        # 🔥 Load token from ENV
        token_json = os.getenv("GOOGLE_TOKEN")

        if not token_json:
            raise Exception("GOOGLE_TOKEN missing in ENV")

        creds_dict = json.loads(token_json)

        creds = Credentials.from_authorized_user_info(creds_dict)

        service = build('gmail', 'v1', credentials=creds)

        message = MIMEText(html, "html")
        message['to'] = to_email
        message['subject'] = subject

        raw = base64.urlsafe_b64encode(message.as_bytes()).decode()

        service.users().messages().send(
            userId="me",
            body={"raw": raw}
        ).execute()

        print(f"✅ Sent to {to_email}")
        return True

    except Exception as e:
        print(f"❌ Failed to send to {to_email}: {e}")
        return False

# =============================
# SEND REMINDER
# =============================

@app.post("/send-reminder")
def send_reminder(data: RequestData):

    sent = []
    failed = []

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

        if send_email(m.email, "Chit Payment Reminder", html):
            sent.append(m.email)
        else:
            failed.append(m.email)

    return {
        "success": True,
        "sent": sent,
        "failed": failed
    }

# =============================
# UPI REDIRECT
# =============================

@app.api_route("/pay", methods=["GET", "HEAD"])
def pay(pa: str, pn: str, am: int):

    upi_url = f"upi://pay?pa={pa}&pn={pn}&am={am}&cu=INR"

    return RedirectResponse(url=upi_url)