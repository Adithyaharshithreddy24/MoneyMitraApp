# ==============================
# 🚀 LOAN MODEL FILE
# ==============================

import os
import logging
from enum import Enum
from contextlib import asynccontextmanager
from fastapi import APIRouter
from pydantic import BaseModel, Field
import pandas as pd
import numpy as np
import xgboost as xgb
from sklearn.calibration import CalibratedClassifierCV
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, confusion_matrix
import shap

# ==============================
# LOGGING
# ==============================
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ==============================
# ROUTER (🔥 IMPORTANT CHANGE)
# ==============================
router = APIRouter()

# ==============================
# SCHEMA
# ==============================

class EducationEnum(str, Enum):
    graduate = "Graduate"
    not_graduate = "Not Graduate"

class SelfEmployedEnum(str, Enum):
    yes = "Yes"
    no = "No"

class PersonalLoanApplication(BaseModel):
    no_of_dependents: int = Field(..., ge=0)
    education: EducationEnum
    self_employed: SelfEmployedEnum
    income_annum: float = Field(..., gt=0)
    loan_amount: float = Field(..., gt=0)
    loan_term: int = Field(..., gt=0)
    cibil_score: int = Field(..., ge=0, le=900)
    residential_assets_value: float = Field(..., ge=0)
    commercial_assets_value: float = Field(..., ge=0)
    luxury_assets_value: float = Field(..., ge=0)
    bank_asset_value: float = Field(..., ge=0)

# ==============================
# MODEL SYSTEM (NO CHANGE)
# ==============================

class PersonalLoanApprovalSystem:
    def __init__(self, data_path="personal_loan_approval_dataset.csv"):
        self.data_path = data_path
        self.model = None
        self.calibrated_model = None
        self.feature_cols = None
        self.explainer = None
        self.metrics = {}

    def _clean_data(self, df):
        df.columns = df.columns.str.strip()
        df['loan_status'] = df['loan_status'].astype(str).str.strip().str.lower()
        df['loan_status'] = df['loan_status'].map({'approved': 1, 'rejected': 0})
        df = df.dropna(subset=['loan_status'])
        df = df.fillna(0)
        return df

    def _engineer_features(self, df):
        df = df.copy()
        df['loan_income_ratio'] = df['loan_amount'] / df['income_annum']
        df['income_per_dependent'] = df['income_annum'] / (df['no_of_dependents'] + 1)
        df['total_assets'] = (
            df['residential_assets_value'] +
            df['commercial_assets_value'] +
            df['luxury_assets_value'] +
            df['bank_asset_value']
        )
        df['loan_asset_ratio'] = df['loan_amount'] / (df['total_assets'] + df['income_annum'])
        df['self_employed'] = df['self_employed'].apply(lambda x: 1 if str(x) == "Yes" else 0)
        df['education'] = df['education'].apply(lambda x: 1 if str(x) == "Graduate" else 0)
        return df.astype(float)

    def train(self):
        logger.info("Training...")
        df = pd.read_csv(self.data_path)
        df = self._clean_data(df)
        df = self._engineer_features(df)

        X = df.drop(columns=['loan_id', 'loan_status'], errors='ignore')
        y = df['loan_status']

        self.feature_cols = X.columns.tolist()

        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, stratify=y, random_state=42
        )

        weight = len(y_train[y_train == 0]) / len(y_train[y_train == 1])

        self.model = xgb.XGBClassifier(eval_metric='logloss', scale_pos_weight=weight)
        self.model.fit(X_train, y_train)

        self.calibrated_model = CalibratedClassifierCV(self.model, method='sigmoid', cv='prefit')
        self.calibrated_model.fit(X_test, y_test)

        self.explainer = shap.TreeExplainer(self.model)

    def _apply_rules(self, data):
        reasons = []
        if data.cibil_score < 550:
            reasons.append("Low CIBIL")
        if data.income_annum < 150000:
            reasons.append("Low income")
        if (data.loan_amount / data.income_annum) > 6:
            reasons.append("Loan too high vs income")
        return reasons

    def predict(self, data: PersonalLoanApplication):

        rules = self._apply_rules(data)

        if rules:
            return {
                "loan_status": "Rejected",
                "approval_probability": 0,
                "risk_level": "Extreme",
                "recommendations": rules,
                "explanation": [{"feature": "rules", "impact": -100}]
            }

        df = pd.DataFrame([data.dict()])
        df = self._engineer_features(df)
        df = df[self.feature_cols]

        prob = float(self.calibrated_model.predict_proba(df)[0][1])
        status = "Approved" if prob >= 0.65 else "Rejected"

        return {
            "loan_status": status,
            "approval_probability": round(prob * 100, 2),
            "risk_level": "Medium",
            "recommendations": [],
            "explanation": []
        }

# ==============================
# INIT
# ==============================

system = PersonalLoanApprovalSystem()

@router.on_event("startup")
def startup():
    system.train()

# ==============================
# ROUTES
# ==============================

@router.post("/personalloanprediction")
def predict(data: PersonalLoanApplication):
    return system.predict(data)