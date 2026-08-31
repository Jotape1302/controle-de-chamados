#!/usr/bin/env bash
set -e

# Setup Java & Maven PATH if locally installed
if [ -d "/home/jotape/.tools/jdk-17/bin" ]; then
    export PATH="/home/jotape/.tools/jdk-17/bin:/home/jotape/.tools/maven/bin:$PATH"
    export JAVA_HOME="/home/jotape/.tools/jdk-17"
fi

echo "======================================================="
echo "   HOMENEY // CYBER TICKET COMMAND CENTER HUD v2.0    "
echo "======================================================="

# Trap Ctrl+C to stop both processes
cleanup() {
    echo ""
    echo ">> Encerrando servidores..."
    kill $(jobs -p) 2>/dev/null || true
    exit 0
}
trap cleanup SIGINT SIGTERM

echo ">> 1. Iniciando Back-end (Spring Boot 3.2.5 REST API na porta 8080)..."
./mvnw spring-boot:run &
BACKEND_PID=$!

echo ">> 2. Aguardando inicialização da API..."
until curl -s http://localhost:8080/api/health > /dev/null 2>&1; do
    sleep 1
done
echo ">> [OK] Back-end online em http://localhost:8080 (Swagger em http://localhost:8080/swagger-ui.html)"

echo ">> 3. Iniciando Front-end (Vite + React + TypeScript na porta 5173)..."
cd frontend && npm run dev &
FRONTEND_PID=$!

echo "======================================================="
echo ">> [OK] Front-end acessível em: http://localhost:5173"
echo ">> Pressione Ctrl+C a qualquer momento para encerrar."
echo "======================================================="

wait
