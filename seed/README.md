# SkillForge Course Seeder

This module initializes the SkillForge Course Service with predefined data for development and testing.

## 📦 What It Does

- Generates **sample courses** with rich metadata, modules, lessons, and categories.
- Populates your **MongoDB** database via the **API Gateway**.
- Creates a default test user for authentication.

## 🚀 How to Run

From the project root, run:

```bash
python seed/seed_all.py
```

This script:

1. Runs `generate_seed_data.py`
   → Creates course JSON files in `seed/seed_courses/`

2. Runs `seed_database.py`
   → Registers a test user, logs in, and POSTs the courses via HTTP to the Course Service.

## 🧪 Output

* ✅ 11 fully structured courses
* ✅ Each course includes modules, lessons, skills, categories, etc.
* ✅ Categories are predefined, fixed in code (not stored in DB)
* ✅ Test user: `max123 / password`

## 🗂 Directory Structure

```
seed/
├── seed_all.py           # Runs generator + seeder
├── generate_seed_data.py # Generates sample JSON files
├── seed_database.py      # Posts courses to the API
└── seed_courses/         # Contains generated JSON files
```

## 🔐 Notes

* Uses only **native Python libraries**
* Sends requests via the **API Gateway**: `http://localhost:8081/api/v1`
* Requires backend services (gateway, user, course) to be running
