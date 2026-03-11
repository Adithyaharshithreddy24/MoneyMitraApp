import google.generativeai as genai
import pytesseract
import cv2
from PIL import Image
import json
import re
import time

# Gemini API key
genai.configure(api_key="AIzaSyCGr85Sq54Dn3vLrEk7oVpgv1lRDqot0GI")

model = genai.GenerativeModel("gemini-2.5-flash")


def extract_receipt_data(image_path):

    # ---------------- OCR TEXT EXTRACTION ----------------
    image = cv2.imread(image_path)

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # increase contrast
    gray = cv2.threshold(gray, 0, 255,
                         cv2.THRESH_BINARY + cv2.THRESH_OTSU)[1]

    text = pytesseract.image_to_string(gray)

    # ---------------- GEMINI ANALYSIS ----------------
    prompt = f"""
You are an AI that extracts structured transaction data.

Below is OCR text from a receipt:

{text}

Extract these fields:

1. Store name
2. Final TOTAL amount paid
3. Category
4. Short note

Rules:
- Amount must be the FINAL TOTAL
- Ignore item prices
- Category must be one of:
Food, Shopping, Transport, Medicine, Entertainment, Bills, Sport, Others

Return ONLY JSON in this format:

{{
"name": "",
"amount": 0,
"type": "EXPENSE",
"category": "",
"note": ""
}}
"""

    response = model.generate_content(prompt)

    result_text = response.text

    try:

        json_text = re.search(r"\{.*\}", result_text, re.DOTALL).group()

        data = json.loads(json_text)

    except:
        data = {
            "name": "Receipt",
            "amount": 0,
            "type": "EXPENSE",
            "category": "Others",
            "note": ""
        }

    # ---------------- CLEAN AMOUNT ----------------
    amount = str(data.get("amount", "0"))

    numbers = re.findall(r"\d+\.?\d*", amount)

    data["amount"] = float(numbers[0]) if numbers else 0

    data["createdAt"] = int(time.time() * 1000)

    return data