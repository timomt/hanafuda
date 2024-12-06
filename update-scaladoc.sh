#!/bin/bash

sbt doc
temp_dir=$(mktemp -d)
cp -R target/scala-*/api/* "$temp_dir"

current_branch=$(git branch --show-current)
git checkout pages

git rm -rf .
cp -R "$temp_dir"/* .

git add .
git commit -m "Update Scaladoc"
git push origin pages

git switch "$current_branch"

rm -rf "$temp_dir"