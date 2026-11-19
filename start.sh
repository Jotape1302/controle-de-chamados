#!/usr/bin/env bash
# Sobe back-end (Spring Boot, perfil dev) e front-end (Vite) juntos para desenvolvimento local.
set -euo pipefail

cd "$(dirname "$0")"

cleanup() {
    echo ""
    echo ">> Encerrando servidores..."
    kill $(jobs -p) 2>/dev/null || true
}
trap cleanup EXIT INT TERM

if ! command -v javac >/dev/null 2>&1; then
    echo "ERRO: JDK 17+ (com o compilador javac) não encontrado no PATH." >&2
    echo "      Ter só o 'java' (JRE) não basta. No Fedora: sudo dnf install java-25-openjdk-devel" >&2
    exit 1
fi

if [ ! -d frontend/node_modules ]; then
    echo ">> Instalando dependências do front-end..."
    (cd frontend && npm ci)
fi

echo ">> Iniciando back-end em http://localhost:8080 ..."
./mvnw -q spring-boot:run &

echo ">> Aguardando a API responder..."
for _ in $(seq 1 120); do
    if curl -sf http://localhost:8080/api/health >/dev/null 2>&1; then
        break
    fi
    sleep 1
done
curl -sf http://localhost:8080/api/health >/dev/null || { echo "ERRO: a API não subiu em 120s." >&2; exit 1; }

echo ">> Back-end OK (Swagger: http://localhost:8080/swagger-ui.html)"
echo ">> Iniciando front-end em http://localhost:5173 ..."
(cd frontend && npm run dev) &

wait
