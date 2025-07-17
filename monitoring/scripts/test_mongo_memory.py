from pymongo import MongoClient

client = MongoClient("mongodb://dev_user:dev_password@localhost:27017/skillforge_dev?authSource=admin")
db = client.skillforge_dev

N = 1000
for i in range(N):
    db.testcol.insert_one({"x": "a" * 1000})
    if (i + 1) % 100 == 0 or (i + 1) == N:
        print(f"Inserted {i + 1}/{N} documents...", flush=True)

print(f"Inserted {N} documents.")

# Delete all documents from the collection
result = db.testcol.delete_many({})
print(f"Deleted {result.deleted_count} documents.")