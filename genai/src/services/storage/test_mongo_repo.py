# test_mongo_repo.py
from mongo_repo import save_path, col, ensure_index


# local test:
# Inject env vars into this PowerShell session:
# $env:MONGODB_HOST="localhost"
# $env:MONGODB_PORT="27017"
# $env:MONGODB_DATABASE="skillforge_dev"
# $env:MONGODB_USERNAME="dev_user"
# $env:MONGODB_PASSWORD="dev_password"


ensure_index()
print("Running MongoDB test...")

test_id = save_path("ps-user","PowerShell Test", [{"step":"OK"}])
print("Created ID: %s", test_id)
doc = col.find_one({"_id": test_id})
print("Found doc: %s", doc)
col.delete_one({"_id": test_id})
print("Deleted and done")
