#!/bin/bash

# Source directory where the WAR file is located
SOURCE_DIR="/target"

# Destination directory where the WAR file should be copied
DEST_DIR="/tomcat/apache-tomcat-8.5.29/webapps"

# WAR file name
WAR_FILE="ibanklos.war"

# Echo current operation
echo "Copying $WAR_FILE from $SOURCE_DIR to $DEST_DIR"

# Copy the WAR file
cp "$SOURCE_DIR/$WAR_FILE" "$DEST_DIR"

# Verify and echo the completion
if [ $? -eq 0 ]; then
    echo "Copy successful."
else
    echo "Copy failed."
fi
