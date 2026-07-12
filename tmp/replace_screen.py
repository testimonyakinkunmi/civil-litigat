import os

main_activity_path = "app/src/main/java/com/example/MainActivity.kt"
temp_code_path = "/tmp/new_active_quiz.kt"

with open(main_activity_path, "r", encoding="utf-8") as f:
    main_content = f.read()

with open(temp_code_path, "r", encoding="utf-8") as f:
    new_code = f.read()

start_marker = "@Composable\nfun ActiveQuizScreen"
end_marker = "@Composable\nfun BookmarksScreen"

start_idx = main_content.find(start_marker)
end_idx = main_content.find(end_marker)

if start_idx != -1 and end_idx != -1:
    print(f"Found block! Replacing from index {start_idx} to {end_idx}")
    updated_content = main_content[:start_idx] + new_code + "\n\n" + main_content[end_idx:]
    with open(main_activity_path, "w", encoding="utf-8") as f:
        f.write(updated_content)
    print("Replacement successful!")
else:
    print(f"Error: Start marker found: {start_idx != -1}, End marker found: {end_idx != -1}")
