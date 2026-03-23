
import google.generativeai as genai
import pytesseract
import cv2
import json
import re
import time
import os
from PIL import Image
from dotenv import load_dotenv
load_dotenv()

# ---------------- API KEY ----------------
print("API KEY:", os.getenv("GEMINI_API_KEY"))

genai.configure(
    api_key=os.getenv("GEMINI_API_KEY") 
)

model = genai.GenerativeModel("gemini-2.5-flash")


# ---------------- CLEAN JSON HELPER ----------------

def extract_json(text):

    try:
        json_text = re.search(r"\{.*\}", text, re.DOTALL).group()
        return json.loads(json_text)

    except:
        return None


# ---------------- RECEIPT SCANNER ----------------

def extract_receipt_data(image_path):

    # ---------------- image --> ocr --> text(ocr response) --> agentic ai --> return json{}  ----------------

    # image = cv2.imread(image_path)

    # gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # gray = cv2.threshold(
    #     gray,
    #     0,
    #     255,
    #     cv2.THRESH_BINARY + cv2.THRESH_OTSU
    # )[1]

    # text = pytesseract.image_to_string(gray)


    # prompt = f"""
    #         You are an AI that extracts structured transaction data.

    #         OCR text from receipt:

    #         {text}

    #         Extract:

    #         - Store name
    #         - Final TOTAL amount
    #         - Category
    #         - Short note

    #         Categories allowed:
    #         Food, Shopping, Transport, Medicine, Entertainment, Bills, Sport, Others

    #         Return ONLY JSON:

    #         {{
    #         "name": "",
    #         "amount": 0,
    #         "type": "EXPENSE",
    #         "category": "",
    #         "note": ""
    #         }}
    # """

    #response = model.generate_content(prompt)
    #--------------------------------------------------------------------------------

    image = Image.open(image_path)   

    prompt = """
        You are an AI that extracts structured transaction data from receipts.

        Rules:
        - Extract FINAL TOTAL amount only
        - Ignore GST/tax lines
        - If multiple totals exist, pick payable amount

        Extract:
        - Store name
        - Final TOTAL amount
        - Category
        - Short note

        Categories allowed:
        Food, Shopping, Transport, Medicine, Entertainment, Bills, Sport, Others

        Return ONLY JSON:

        {
        "name": "",
        "amount": 0,
        "type": "EXPENSE",
        "category": "",
        "note": ""
        }
    """
    response = model.generate_content([prompt, image])

    data = extract_json(response.text)

    if not data:

        data = {
            "name": "Receipt",
            "amount": 0,
            "type": "EXPENSE",
            "category": "Others",
            "note": ""
        }

    # Clean amount
    amount = str(data.get("amount", "0"))

    numbers = re.findall(r"\d+\.?\d*", amount)

    data["amount"] = float(numbers[0]) if numbers else 0

    data["createdAt"] = int(time.time() * 1000)

    return data


# ---------------- NOTIFICATION PARSER ----------------

def analyze_notification(message):

    prompt = f"""
        Detect if this message contains a financial transaction.

        Message:
        {message}

        Extract:

        - transaction type
        - amount
        - merchant
        - category

        Categories allowed:
        Food, Shopping, Transport, Medicine, Entertainment, Bills, Sport, Others

        Return JSON ONLY:

        {{
        "name": "",
        "amount": 0,
        "type": "INCOME / EXPENSE",
        "category": "",
        "note": ""
        }}
    """

    response = model.generate_content(prompt)

    data = extract_json(response.text)

    if not data:

        return {
            "name": "",
            "amount": 0,
            "type": "NONE",
            "category": "",
            "note": ""
        }

    data["createdAt"] = int(time.time() * 1000)

    return data