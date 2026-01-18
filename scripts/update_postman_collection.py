#!/usr/bin/env python3
"""
Update Postman Collection with new endpoints
© 2025-2026 Hector Torres - Greenlake Martial Arts
"""
import json
import sys
from pathlib import Path

def add_endpoint(folder_items, endpoint_config, position=None):
    """Add an endpoint to a folder at the specified position"""
    if position is None:
        folder_items.append(endpoint_config)
    else:
        folder_items.insert(position, endpoint_config)

def update_collection(collection_path):
    """Update the Postman collection with missing endpoints"""
    with open(collection_path, 'r') as f:
        collection = json.load(f)

    # Find folders
    levels_folder = next((item for item in collection['item'] if item['name'] == 'Levels'), None)
    students_folder = next((item for item in collection['item'] if item['name'] == 'Students'), None)
    progress_folder = next((item for item in collection['item'] if item['name'] == 'Student Progress'), None)

    # Check if endpoints already exist
    if levels_folder:
        existing = [item['name'] for item in levels_folder['item']]
        if 'Get Students in Level' not in existing:
            endpoint = {
                "name": "Get Students in Level",
                "event": [{
                    "listen": "test",
                    "script": {
                        "exec": [
                            "pm.test(\"Status code is 200\", function () {",
                            "    pm.response.to.have.status(200);",
                            "});",
                            "",
                            "pm.test(\"Response is an array\", function () {",
                            "    var jsonData = pm.response.json();",
                            "    pm.expect(jsonData).to.be.an('array');",
                            "});"
                        ],
                        "type": "text/javascript"
                    }
                }],
                "request": {
                    "method": "GET",
                    "header": [{"key": "Authorization", "value": "Bearer {{token}}"}],
                    "url": {
                        "raw": "{{baseUrl}}/levels/:id/students",
                        "host": ["{{baseUrl}}"],
                        "path": ["levels", ":id", "students"],
                        "variable": [{"key": "id", "value": "1"}]
                    }
                }
            }
            add_endpoint(levels_folder['item'], endpoint, 2)
            print("✅ Added: GET /levels/:id/students")

    if students_folder:
        existing = [item['name'] for item in students_folder['item']]
        if 'Get Students by Level' not in existing:
            endpoint = {
                "name": "Get Students by Level",
                "event": [{
                    "listen": "test",
                    "script": {
                        "exec": [
                            "pm.test(\"Status code is 200\", function () {",
                            "    pm.response.to.have.status(200);",
                            "});",
                            "",
                            "pm.test(\"Response is an array\", function () {",
                            "    var jsonData = pm.response.json();",
                            "    pm.expect(jsonData).to.be.an('array');",
                            "});"
                        ],
                        "type": "text/javascript"
                    }
                }],
                "request": {
                    "method": "GET",
                    "header": [{"key": "Authorization", "value": "Bearer {{token}}"}],
                    "url": {
                        "raw": "{{baseUrl}}/students/level/:levelId",
                        "host": ["{{baseUrl}}"],
                        "path": ["students", "level", ":levelId"],
                        "variable": [{"key": "levelId", "value": "1"}]
                    }
                }
            }
            add_endpoint(students_folder['item'], endpoint, 3)
            print("✅ Added: GET /students/level/:levelId")

    if progress_folder:
        existing = [item['name'] for item in progress_folder['item']]
        if 'Get Progress by Student and Level' not in existing:
            endpoint = {
                "name": "Get Progress by Student and Level",
                "event": [{
                    "listen": "test",
                    "script": {
                        "exec": [
                            "pm.test(\"Status code is 200\", function () {",
                            "    pm.response.to.have.status(200);",
                            "});",
                            "",
                            "pm.test(\"Response has level and requirements\", function () {",
                            "    var jsonData = pm.response.json();",
                            "    pm.expect(jsonData).to.have.property('level');",
                            "    pm.expect(jsonData).to.have.property('requirements');",
                            "    pm.expect(jsonData.requirements).to.be.an('array');",
                            "});"
                        ],
                        "type": "text/javascript"
                    }
                }],
                "request": {
                    "method": "GET",
                    "header": [{"key": "Authorization", "value": "Bearer {{token}}"}],
                    "url": {
                        "raw": "{{baseUrl}}/student-progress/student/:studentId/level/:levelId",
                        "host": ["{{baseUrl}}"],
                        "path": ["student-progress", "student", ":studentId", "level", ":levelId"],
                        "variable": [
                            {"key": "studentId", "value": "1"},
                            {"key": "levelId", "value": "1"}
                        ]
                    }
                }
            }
            add_endpoint(progress_folder['item'], endpoint, 3)
            print("✅ Added: GET /student-progress/student/:studentId/level/:levelId")

    # Write updated collection
    with open(collection_path, 'w') as f:
        json.dump(collection, f, indent=2)
    
    print(f"\n✅ Postman collection updated: {collection_path}")

if __name__ == "__main__":
    project_root = Path(__file__).parent.parent
    collection_path = project_root / "GMA-School-API.postman_collection.json"
    
    if not collection_path.exists():
        print(f"❌ Collection not found: {collection_path}")
        sys.exit(1)
    
    update_collection(collection_path)
