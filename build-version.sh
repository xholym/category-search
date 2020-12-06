#!/usr/bin/env sh

mkdir -p version
./gradlew clean build
cp build/libs/category-search-0.0.1-all.jar version/category-search

