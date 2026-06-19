#!/bin/bash
# ──────────────────────────────────────────────────────────────
# Script de análisis SonarQube para el Colegio Bernardo O'Higgins
# ──────────────────────────────────────────────────────────────
# Requisitos:
#   1. Tener Docker funcionando
#   2. Haber ejecutado: docker-compose up -d sonarqube
#   3. Tener Java 21+ y Maven instalados
#   4. Haber generado un token en http://localhost:9000/account/security
#
# Modo de uso:
#   export SONAR_TOKEN=tu_token_aqui
#   bash scripts/sonar-analysis.sh
#
# O también:
#   bash scripts/sonar-analysis.sh TU_TOKEN
# ──────────────────────────────────────────────────────────────

TOKEN="${1:-$SONAR_TOKEN}"

if [ -z "$TOKEN" ]; then
  echo "❌ Debes proporcionar un token de SonarQube."
  echo ""
  echo "  1) Abre http://localhost:9000 en tu navegador"
  echo "  2) Inicia sesión (admin/admin por defecto)"
  echo "  3) Ve a Tu Perfil → Seguridad → Generar token"
  echo "  4) Exporta el token: export SONAR_TOKEN=tu_token"
  echo "  5) Vuelve a ejecutar este script"
  exit 1
fi

echo "=========================================="
echo "  Análisis SonarQube - Colegio O'Higgins"
echo "=========================================="
echo ""

BASE_DIR=$(dirname "$0")/..
cd "$BASE_DIR"

SERVICES=("servicio-academico" "servicio-asistencia" "servicio-comunicaciones" "api-gateway")
HAS_ERRORS=false

for SERVICE in "${SERVICES[@]}"; do
  echo ""
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  echo "  Analizando: $SERVICE"
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

  cd "$SERVICE"

  echo "  → Ejecutando tests con JaCoCo..."
  if ! mvn clean verify -q; then
    echo "  ⚠️  Los tests tienen fallos. Se omitirá la cobertura para este servicio."
  fi

  echo "  → Enviando análisis a SonarQube..."
  if mvn sonar:sonar -Dsonar.token="$TOKEN" -q; then
    echo "  ✅ $SERVICE analizado correctamente"
  else
    echo "  ❌ Error al analizar $SERVICE"
    echo "     Verifica que SonarQube esté corriendo en http://localhost:9000"
    echo "     o prueba en forma manual:"
    echo "       cd $SERVICE && mvn sonar:sonar -Dsonar.token=TU_TOKEN"
    HAS_ERRORS=true
  fi

  cd ..
done

echo ""
echo "=========================================="
if [ "$HAS_ERRORS" = true ]; then
  echo "  ⚠️  Algunos servicios tuvieron errores."
  echo "     Revisa los mensajes anteriores."
else
  echo "  ✅ Todos los servicios analizados correctamente."
fi
echo "  Abre http://localhost:9000 para ver resultados"
echo "=========================================="
