#!/bin/bash
# Runs once, on the first start of the Oracle container.
# Creates the schema, loads the sample data and installs the PL/SQL used by the app.
set -e

SCRIPTS=(
  USBD03/schema.sql
  USBD04/data.sql
  USBD33/train_trigger.sql
  USLP08/uslp08_functions.sql
  USLP09/uslp09_functions.sql
  USLP10/uslp10_functions.sql
)

cd /sql-scripts
for f in "${SCRIPTS[@]}"; do
  echo ">>> Running $f"
  sqlplus -s -L "APPUSER/admin@//localhost/FREEPDB1" <<EOF
SET DEFINE OFF
SET FEEDBACK OFF
WHENEVER SQLERROR CONTINUE
@$f
COMMIT;
EXIT
EOF
done
echo ">>> freightMngt schema loaded"
