# ==============================
# 🚀 REALISTIC LOAN AI (FINAL WORKING)
# XGBoost + Cleaning + Metrics + FastAPI
# ==============================

import os
import logging
from enum import Enum
from contextlib import asynccontextmanager
from fastapi import FastAPI
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
# SCHEMA
# ==============================

class EducationEnum(str, Enum):
    graduate = "Graduate"
    not_graduate = "Not Graduate"

class SelfEmployedEnum(str, Enum):
    yes = "Yes"
    no = "No"

class LoanApplication(BaseModel):
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
# CORE SYSTEM
# ==============================

class LoanApprovalSystem:

    def __init__(self, data_path="loan_approval_dataset.csv"):
        self.data_path = data_path
        self.model = None
        self.calibrated_model = None
        self.feature_cols = None
        self.explainer = None
        self.metrics = {}

    # --------------------------
    # DATA CLEANING (🔥 IMPORTANT)
    # --------------------------
    def _clean_data(self, df):

        df.columns = df.columns.str.strip()

        # FIX loan_status (MAIN ERROR FIX)
        df['loan_status'] = df['loan_status'].astype(str).str.strip().str.lower()
        df['loan_status'] = df['loan_status'].map({
            'approved': 1,
            'rejected': 0
        })

        # Drop invalid rows
        df = df.dropna(subset=['loan_status'])

        # Fill other missing values
        df = df.fillna(0)

        return df

    # --------------------------
    # FEATURE ENGINEERING
    # --------------------------
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

        # 🔥 FIXED ASSET LOGIC
        df['loan_asset_ratio'] = df['loan_amount'] / (df['total_assets'] + df['income_annum'])

        df['self_employed'] = df['self_employed'].apply(lambda x: 1 if str(x) == "Yes" else 0)
        df['education'] = df['education'].apply(lambda x: 1 if str(x) == "Graduate" else 0)

        return df.astype(float)

    # --------------------------
    # TRAIN
    # --------------------------
    def train(self):

        logger.info("🔄 Training started...")

        df = pd.read_csv(self.data_path)
        df = self._clean_data(df)
        df = self._engineer_features(df)

        X = df.drop(columns=['loan_id', 'loan_status'], errors='ignore')
        y = df['loan_status']

        self.feature_cols = X.columns.tolist()

        # Split
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, stratify=y, random_state=42
        )

        # Class balance
        weight = len(y_train[y_train == 0]) / len(y_train[y_train == 1])

        self.model = xgb.XGBClassifier(
            eval_metric='logloss',
            scale_pos_weight=weight,
            max_depth=4,
            learning_rate=0.05,
            subsample=0.8,
            colsample_bytree=0.8,
            random_state=42
        )

        self.model.fit(X_train, y_train)

        # Calibration
        self.calibrated_model = CalibratedClassifierCV(self.model, method='sigmoid', cv='prefit')
        self.calibrated_model.fit(X_test, y_test)

        # Metrics
        y_pred = self.calibrated_model.predict(X_test)

        self.metrics = {
            "accuracy": round(float(accuracy_score(y_test, y_pred)), 4),
            "precision": round(float(precision_score(y_test, y_pred)), 4),
            "recall": round(float(recall_score(y_test, y_pred)), 4),
            "f1_score": round(float(f1_score(y_test, y_pred)), 4),
            "confusion_matrix": confusion_matrix(y_test, y_pred).tolist()
        }

        logger.info("\n📊 MODEL PERFORMANCE")
        logger.info("=" * 40)
        logger.info(f"Accuracy   : {self.metrics['accuracy']}")
        logger.info(f"Precision  : {self.metrics['precision']}")
        logger.info(f"Recall     : {self.metrics['recall']}")
        logger.info(f"F1 Score   : {self.metrics['f1_score']}")
        logger.info("=" * 40)

        cm = self.metrics["confusion_matrix"]

        logger.info("\n📉 CONFUSION MATRIX")
        logger.info("          Predicted")
        logger.info("          0     1")
        logger.info(f"Actual 0  {cm[0][0]}   {cm[0][1]}")
        logger.info(f"Actual 1  {cm[1][0]}   {cm[1][1]}")
        logger.info("=" * 40)

        self.explainer = shap.TreeExplainer(self.model)

    # --------------------------
    # RULES
    # --------------------------
    def _apply_rules(self, data):
        reasons = []

        if data.cibil_score < 550:
            reasons.append("Low CIBIL")

        if data.income_annum < 150000:
            reasons.append("Low income")

        if (data.loan_amount / data.income_annum) > 6:
            reasons.append("Loan too high vs income")

        return reasons

    # --------------------------
    # PREDICT
    # --------------------------
    def predict(self, data: LoanApplication):

        rules = self._apply_rules(data)

        if rules:
            return {
                "loan_status": "Rejected",
                "approval_probability": 0,
                "risk_level": "Extreme",
                "recommendations": rules,
                "explanation": [{"feature": "rules", "impact": -100}]
            }

        # Premium override
        if (
            data.cibil_score > 750 and
            data.income_annum > 800000 and
            (data.loan_amount / data.income_annum) < 3
        ):
            return {
                "loan_status": "Approved",
                "approval_probability": 90,
                "risk_level": "Low",
                "recommendations": ["Premium customer"],
                "explanation": [{"feature": "override", "impact": 100}]
            }

        df = pd.DataFrame([data.dict()])
        df = self._engineer_features(df)
        df = df[self.feature_cols]

        prob = float(self.calibrated_model.predict_proba(df)[0][1])

        status = "Approved" if prob >= 0.65 else "Rejected"

        # Smooth probability
        prob = max(min(prob, 0.98), 0.02)
        prob = 1 / (1 + np.exp(-3 * (prob - 0.5)))

        prob_percent = round(prob * 100, 2)

        # Risk
        if prob > 0.8:
            risk = "Low"
        elif prob > 0.65:
            risk = "Medium"
        elif prob > 0.4:
            risk = "High"
        else:
            risk = "Extreme"

        # SHAP
        shap_vals = self.explainer.shap_values(df)
        if isinstance(shap_vals, list):
            shap_vals = shap_vals[1]

        top = sorted(zip(self.feature_cols, shap_vals[0]), key=lambda x: abs(x[1]), reverse=True)

        explanation = [{"feature": f, "impact": round(float(v), 4)} for f, v in top[:3]]

        recs = ["Maintain good credit"] if status == "Approved" else ["Improve income", "Reduce loan"]

        return {
            "loan_status": status,
            "approval_probability": prob_percent,
            "risk_level": risk,
            "recommendations": recs,
            "explanation": explanation
        }

# ==============================
# FASTAPI
# ==============================

system = LoanApprovalSystem()

@asynccontextmanager
async def lifespan(app: FastAPI):
    if not os.path.exists(system.data_path):
        raise RuntimeError("Dataset not found")
    system.train()
    yield

app = FastAPI(lifespan=lifespan)

@app.post("/predict")
def predict(data: LoanApplication):
    return system.predict(data)

@app.get("/metrics")
def metrics():
    return system.metrics

@app.get("/health")
def health():
    return {"status": "ok"}