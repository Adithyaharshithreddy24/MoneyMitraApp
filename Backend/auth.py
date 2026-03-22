import os
import json
from google_auth_oauthlib.flow import InstalledAppFlow

SCOPES = ['https://www.googleapis.com/auth/gmail.send']

# ✅ Load credentials from ENV
creds_json = os.getenv("GOOGLE_CREDENTIALS")

if not creds_json:
    raise Exception("GOOGLE_CREDENTIALS not found in environment")

creds_dict = json.loads(creds_json)

# 🔥 Create temp credentials file (needed by Google lib)
with open("temp_credentials.json", "w") as f:
    json.dump(creds_dict, f)

# 🔐 OAuth flow
flow = InstalledAppFlow.from_client_secrets_file(
    "temp_credentials.json", SCOPES
)

creds = flow.run_local_server(port=0)

# ✅ Save token
with open("token.json", "w") as token:
    token.write(creds.to_json())

# 🧹 cleanup
os.remove("temp_credentials.json")

print("✅ Token generated!")