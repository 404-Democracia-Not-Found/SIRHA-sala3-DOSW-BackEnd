#!/bin/bash

# SIRHA Backend Startup Script

echo "=== SIRHA Backend Startup Script ==="
echo "Starting SIRHA Backend with MongoDB Atlas connection..."

# Set Java options for optimal performance
export JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8 -Duser.timezone=America/Bogota"

# Set Spring profile
export SPRING_PROFILES_ACTIVE=dev

# Ensure logs directory exists
mkdir -p logs

echo "Java Options: $JAVA_OPTS"
echo "Spring Profile: $SPRING_PROFILES_ACTIVE"
echo "MongoDB Atlas Connection: Enabled"
echo "==============================================="

# Run the application
java $JAVA_OPTS -jar target/sirha-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE