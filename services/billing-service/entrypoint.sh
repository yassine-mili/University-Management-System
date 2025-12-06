#!/bin/bash
# Wait for SQL Server TCP port to be open
host="billing-sql"
port=1433

echo "Waiting for ${host}:${port} to be available (TCP)..."
TRIES=0
MAX=60
while ! bash -c "</dev/tcp/${host}/${port}" >/dev/null 2>&1 && [ $TRIES -lt $MAX ]; do
  sleep 2
  TRIES=$((TRIES+1))
  echo "Attempt $TRIES"
done

if [ $TRIES -ge $MAX ]; then
  echo "TCP port ${port} on ${host} not reachable after waiting; continuing to start app (may fail)"
fi

# Start the app
dotnet BillingService.dll
