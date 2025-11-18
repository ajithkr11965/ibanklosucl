#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status
set -o pipefail  # Return value of a pipeline is the value of the last (rightmost) command to exit with a non-zero status

# Configuration..gs
WAR_FILE=$1
TARGET_SERVER="tomcat@10.1.214.235"
TARGET_DIR="/tomcat/ajithkr/ibanklos"
BACKUP_DIR="/tomcat/ajithkr/ibanklos/backup"
LOG_FILE="/tomcat/ajithkr/ibanklos/logs/deployment.log"

# Function to log messages...
log_message() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Check if WAR file is provided
if [[ -z "$WAR_FILE" ]]; then
    log_message "ERROR: WAR file not provided. Exiting..."
    exit 1
fi

# Create a backup of the current deployment.........
echo "$(date) - Creating backup of current deployment"
ssh $TARGET_SERVER "if [ -f $TARGET_DIR/*.war ]; then cp $TARGET_DIR/*.war $TARGET_DIR/backup/; else echo 'No WAR file to backup'; fi"
#ssh $TARGET_SERVER "mkdir -p $BACKUP_DIR && cp $TARGET_DIR/*.war $BACKUP_DIR/backup_$(date +%Y%m%d_%H%M%S).war"

# Copy the new WAR file
log_message "Copying WAR file to the server: $TARGET_SERVER"
scp -o StrictHostKeyChecking=no "$WAR_FILE" "$TARGET_SERVER:$TARGET_DIR"

if [[ $? -eq 0 ]]; then
    log_message "Deployment successful at $(date). WAR file has been copied to the server."

    # Restart the application server (adjust the command as needed)
    log_message "Restarting application server"
    #ssh $TARGET_SERVER "sudo systemctl restart your-app-service"

    # Verify the application is running
    #log_message "Verifying application status"
    #if curl -sSf http://your-uat-server-url > /dev/null; then
     #   log_message "Application is up and running"
    #else
     #   log_message "ERROR: Application is not responding. Rolling back..."
      #  ssh $TARGET_SERVER "cp $BACKUP_DIR/$(ls -t $BACKUP_DIR | head -n1) $TARGET_DIR/app.war && sudo systemctl restart your-app-service"
    #fi
else
    log_message "ERROR: Deployment failed. Check SCP details."
    exit 1
fi
