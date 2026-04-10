from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import RedirectResponse
from pydantic import BaseModel
from typing import List
import shutil
import os
import base64
import json
from email.mime.text import MIMEText
from fastapi.responses import JSONResponse

from googleapiclient.discovery import build
from google.oauth2.credentials import Credentials

from gemini_service import extract_receipt_data, analyze_notification

app = FastAPI()
from loan_model import router as loan_router

# 🔥 INCLUDE LOAN ROUTES
app.include_router(loan_router)

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

# ==============================
# PERSONAL LOAN APPROVAL SYSTEM
# ==============================

def calculate_home_loan(property_value, cibil_score, monthly_income):

    if property_value <= 3000000:
        ltv = 0.9
    elif property_value <= 7500000:
        ltv = 0.8
    else:
        ltv = 0.75

    loan_by_ltv = property_value * ltv

    max_emi = monthly_income * 0.45

    tenure_years = 20
    tenure_months = tenure_years * 12

    if cibil_score >= 750:
        annual_rate = 8.5
        status = "High Approval ✅"
    elif cibil_score >= 650:
        annual_rate = 9.5
        status = "Moderate Approval ⚠️"
    else:
        annual_rate = 11
        status = "Low Approval ❌"

    monthly_rate = annual_rate / 12 / 100

    loan_by_income = max_emi * ((1 + monthly_rate)**tenure_months - 1) / \
                     (monthly_rate * (1 + monthly_rate)**tenure_months)

    loan = min(loan_by_ltv, loan_by_income)

    emi = loan * monthly_rate * (1 + monthly_rate)**tenure_months / \
          ((1 + monthly_rate)**tenure_months - 1)

    total_payment = emi * tenure_months
    total_interest = total_payment - loan

    return {
        "property_value": property_value,
        "loan_by_ltv": round(loan_by_ltv, 2),
        "loan_by_income": round(loan_by_income, 2),
        "eligible_loan": round(loan, 2),
        "interest_rate": f"{annual_rate}%",
        "tenure_years": tenure_years,
        "emi_per_month": round(emi, 2),
        "total_interest": round(total_interest, 2),
        "total_payment": round(total_payment, 2),
        "approval_status": status
    }

@app.post("/calculatehomeloan")
def calculate(
    propertyvalue: float = Form(...),
    monthlyincome: float = Form(...),
    cibil: int = Form(...)
):
    return JSONResponse(
        calculate_home_loan(propertyvalue, cibil, monthlyincome)
    )


import requests

# =============================
# GOLD LOAN CALCULATOR
# =============================

def get_gold_price_inr():
    try:
        gold = requests.get("https://api.gold-api.com/price/XAU", timeout=5).json()
        usd_price = gold['price']

        currency = requests.get("https://api.exchangerate-api.com/v4/latest/USD", timeout=5).json()
        rate = currency['rates']['INR']

        inr_per_gram = (usd_price * rate) / 31.1035
        return inr_per_gram

    except:
        return None


@app.post("/calculategoldloan")
def calculate_gold(weight: float = Form(...)):
    try:
        price_per_gram = get_gold_price_inr()

        if price_per_gram is None:
            return JSONResponse({"error": "API failed"}, status_code=500)

        # ✅ Constants
        tenure_months = 12
        interest_rate = 10.0   # fixed %

        # ✅ Gold valuation
        gold_value = weight * price_per_gram

        # ✅ RBI LTV (75%)
        loan_amount = gold_value * 0.75

        # ✅ EMI Calculation
        monthly_rate = interest_rate / 12 / 100

        emi = loan_amount * monthly_rate * (1 + monthly_rate)**tenure_months / \
              ((1 + monthly_rate)**tenure_months - 1)

        total_payment = emi * tenure_months
        total_interest = total_payment - loan_amount

        return {
            "weight": weight,
            "price_per_gram": round(price_per_gram, 2),
            "gold_value": round(gold_value, 2),
            "loan_amount": round(loan_amount, 2),

            # 🔥 Fixed values
            "interest_rate": f"{interest_rate}%",
            "tenure_months": tenure_months,

            "emi": round(emi, 2),
            "total_interest": round(total_interest, 2),
            "total_payment": round(total_payment, 2)
        }

    except Exception as e:
        return JSONResponse({"error": str(e)}, status_code=500)
    
# =============================
# VEHICLE LOAN CALCULATOR
# =============================

def calculate_vehicle_loan(vehicle_price, cibil_score, monthly_income, vehicle_type):

    # ✅ LTV & Tenure
    if vehicle_type == "new":
        ltv = 0.9
        tenure_years = 5
    else:
        ltv = 0.75
        tenure_years = 3

    loan_by_ltv = vehicle_price * ltv

    # ✅ Income eligibility
    max_emi = monthly_income * 0.4
    tenure_months = tenure_years * 12

    # ✅ Interest based on CIBIL
    if cibil_score >= 750:
        annual_rate = 9
        status = "High Approval ✅"
    elif cibil_score >= 650:
        annual_rate = 11
        status = "Moderate Approval ⚠️"
    else:
        annual_rate = 13
        status = "Low Approval ❌"

    monthly_rate = annual_rate / 12 / 100

    # ✅ Loan eligibility based on income
    loan_by_income = max_emi * ((1 + monthly_rate)**tenure_months - 1) / \
                     (monthly_rate * (1 + monthly_rate)**tenure_months)

    # ✅ Final eligible loan
    loan = min(loan_by_ltv, loan_by_income)

    # ✅ EMI Calculation
    emi = loan * monthly_rate * (1 + monthly_rate)**tenure_months / \
          ((1 + monthly_rate)**tenure_months - 1)

    # ✅ Totals
    total_payment = emi * tenure_months
    total_interest = total_payment - loan

    return {
        "vehicle_price": vehicle_price,
        "vehicle_type": vehicle_type,
        "loan_by_ltv": round(loan_by_ltv, 2),
        "loan_by_income": round(loan_by_income, 2),
        "eligible_loan": round(loan, 2),
        "interest_rate": f"{annual_rate}%",
        "tenure_years": tenure_years,
        "emi_per_month": round(emi, 2),
        "total_interest": round(total_interest, 2),
        "total_payment": round(total_payment, 2),
        "approval_status": status
    }

@app.post("/calculatevehicleloan")
def calculate_vehicle(
    price: float = Form(...),
    income: float = Form(...),
    cibil: int = Form(...),
    vehicle_type: str = Form(...)
):
    return JSONResponse(
        calculate_vehicle_loan(price, cibil, income, vehicle_type)
    )

# =============================
# UPI REDIRECT
# =============================

@app.api_route("/pay", methods=["GET", "HEAD"])
def pay(pa: str, pn: str, am: int):

    upi_url = f"upi://pay?pa={pa}&pn={pn}&am={am}&cu=INR"

    return RedirectResponse(url=upi_url)