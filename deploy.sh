#!/bin/bash

SESSION_NAME="backend"
PROJECT_DIR="$HOME/ongibox"

echo "➡️ Killing existing tmux session (if any)..."
tmux kill-session -t $SESSION_NAME 2>/dev/null

echo "➡️ Pulling latest code from Git..."
cd "$PROJECT_DIR" || exit 1
git pull origin main

echo "➡️ Building project..."
cd demo/demo
./gradlew clean build || { echo "❌  Build failed"; exit 1; }

echo "Running as $(whoami)"
echo "➡️ Finding built JAR file..."
JAR_FILE=$(ls build/libs/*.jar | grep -v plain | head -n 1)

echo "➡️ Starting new tmux session and launching server..."
tmux new-session -d -s $SESSION_NAME "cd $PROJECT_DIR/demo/demo && sudo java -jar $JAR_FILE" || { echo "tmux rc=$?"; exit 1; }
echo "$JAR_FILE"
echo "✅  Server is running inside tmux session: $SESSION_NAME"
echo "To view: tmux attach -t $SESSION_NAME"
