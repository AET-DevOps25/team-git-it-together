import os
import subprocess

# Paths
script_dir = os.path.dirname(os.path.abspath(__file__))
generator = os.path.join(script_dir, "generate_seed_data.py")
seeder = os.path.join(script_dir, "seed_database.py")


# Run generator
print("🚀 Generating seed data...")
subprocess.run(["python", generator], check=True)
