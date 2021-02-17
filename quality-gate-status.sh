QGSTATUS=$(curl -s -u  "$SONAR_TOKEN": https://sonarcloud.io/api/qualitygates/project_status?projectKey="$SONAR_PROJECT_KEY" | jq '.projectStatus.status' | tr -d '"')
echo "Quality Gate Status: $QGSTATUS"
if [ "$QGSTATUS" = "OK" ]
then
exit 0
elif [ "$QGSTATUS" = "ERROR" ]
then
exit 1
fi
