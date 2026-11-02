#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
if ! command -v java >/dev/null 2>&1; then echo "Instale o Java JDK 17 ou superior."; exit 1; fi
if [ ! -f target/classes/br/edu/cinearquivo/Main.class ]; then echo "Execute primeiro: mvn clean package"; exit 1; fi
exec java -cp 'target/classes:target/dependency/*' br.edu.cinearquivo.Main
