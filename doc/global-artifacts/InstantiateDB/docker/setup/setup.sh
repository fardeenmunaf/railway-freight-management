#!/bin/bash
echo "A aguardar Oracle iniciar completamente..."

# Espera até que a frase 'DATABASE IS READY TO USE!' apareça nos logs
until grep -q "DATABASE IS READY TO USE!" /opt/oracle/diag/rdbms/*/*/alert*.log 2>/dev/null; do
  echo "Ainda a iniciar... a aguardar logs de prontidão"
  sleep 10
done

echo "Oracle está pronto! A aplicar script init.sql..."
sqlplus -s / as sysdba @/opt/oracle/scripts/init.sql

echo "✅ Script concluído com sucesso. Mantendo container ativo."
tail -f /dev/null
