from pydantic import BaseModel
from typing import Optional

class Transaction(BaseModel):
    id: str = ""
    accountId: str = ""
    name: str = ""
    amount: float = 0.0
    type: str = "EXPENSE"
    accountLabel: str = ""
    category: str = ""
    customCategory: Optional[str] = None
    note: str = ""
    createdAt: int