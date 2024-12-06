#!/bin/bash

current_branch=$(git branch --show-current)
temp_untracked=$(mktemp -d)
git ls-files --others --exclude-standard -z | xargs -0 -I {} mv {} "$temp_untracked"

sbt doc
temp_dir=$(mktemp -d)
cp -R target/scala-*/api/* "$temp_dir"

git checkout pages

git rm -rf .
cp -R "$temp_dir"/* .

git add .
git commit -m "Update Scaladoc"
git push origin pages

git switch "$current_branch"

mv "$temp_untracked"/* .

rm -rf "$temp_dir" "$temp_untracked"