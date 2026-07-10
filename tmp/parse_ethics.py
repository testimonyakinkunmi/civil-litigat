import sys
import json
import csv
import re

# Load the existing JSON data
json_path = "app/src/main/assets/jsondata.json"
with open(json_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

# Structure to build: week -> quizzes
weeks_map = {}

# Read from stdin
reader = csv.reader(sys.stdin)
header = next(reader) # skip header

for row in reader:
    if not row or len(row) < 9:
        continue
    
    week_raw = row[0].strip().upper()  # e.g., "WEEK 1" or "WEEK 7"
    # normalize week_raw
    match = re.search(r'\d+', week_raw)
    if not match:
        continue
    week_num = match.group()
    week_name = f"WEEK {week_num}"
    
    subtopic_raw = row[1].strip() if row[1].strip() else "Professional Ethics"
    scenario = row[2].strip()
    
    options = [
        row[3].strip(),
        row[4].strip(),
        row[5].strip(),
        row[6].strip()
    ]
    
    ans_str = row[7].strip().upper()
    correct_idx = -1
    
    # Check for direct Option letters or text matches
    if ans_str in ['A', 'OPTION A']:
        correct_idx = 0
    elif ans_str in ['B', 'OPTION B']:
        correct_idx = 1
    elif ans_str in ['C', 'OPTION C']:
        correct_idx = 2
    elif ans_str in ['D', 'OPTION D']:
        correct_idx = 3
    else:
        for idx, opt in enumerate(options):
            if opt.lower() == ans_str.lower():
                correct_idx = idx
                break
        if correct_idx == -1:
            for idx, opt in enumerate(options):
                if ans_str.lower() in opt.lower() or opt.lower() in ans_str.lower():
                    correct_idx = idx
                    break
    if correct_idx == -1:
        correct_idx = 0
        
    explanation = row[8].strip()
    
    quiz_obj = {
        "scenario": scenario,
        "options": options,
        "correctIndex": correct_idx,
        "verbatimCorrection": explanation,
        "category": "ethics"
    }
    
    if week_name not in weeks_map:
        weeks_map[week_name] = {}
    if subtopic_raw not in weeks_map[week_name]:
        weeks_map[week_name][subtopic_raw] = []
    weeks_map[week_name][subtopic_raw].append(quiz_obj)

# Create the topic objects
new_topics = []
sorted_weeks = sorted(weeks_map.keys(), key=lambda w: int(re.search(r'\d+', w).group()))

for week in sorted_weeks:
    subtopics_list = []
    for subtopic_name, quizzes in weeks_map[week].items():
        subtopics_list.append({
            "subtopicName": subtopic_name,
            "quizzes": quizzes
        })
    new_topics.append({
        "topicName": week,
        "category": "ethics",
        "subtopics": subtopics_list
    })

# Append new topics to existing topics in jsondata.json
data['topics'].extend(new_topics)

with open(json_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)

print(f"Successfully processed Professional Ethics questions. Added {len(new_topics)} week topics.")
