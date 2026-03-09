#!/bin/bash
set -e

echo "🔧 Configurando ambiente de desarrollo Java Spring Boot..."

# ─── Gradle Wrapper ───────────────────────────────────────────────────────────
if [ ! -f "./gradlew" ]; then
    echo "⚙️  gradlew no encontrado, generando con: gradle wrapper..."
    gradle wrapper --gradle-version 8.12
    echo "✅ gradlew generado"
fi
chmod +x ./gradlew
echo "✅ gradlew: permisos asignados"

# ─── Verificar versiones ───────────────────────────────────────────────────────
echo ""
echo "──────────────────────────────────────────────"
echo "  📦 Versiones instaladas"
echo "──────────────────────────────────────────────"
java -version
gradle --version | head -4

# ─── Configurar Git ───────────────────────────────────────────────────────────
git config --global core.autocrlf input
git config --global pull.rebase false
echo "✅ Git configurado"

# ─── Crear directorios de trabajo estándar ────────────────────────────────────
mkdir -p logs reports

echo ""
echo "🎉 ¡Ambiente listo!"
echo "   • Para iniciar el proyecto: ./gradlew bootRun"
echo "   • Para compilar           : ./gradlew build"
echo "   • Para ejecutar tests     : ./gradlew test"
echo "   • Para debug remoto       : ./gradlew bootRun --debug-jvm  (puerto 5005)"
echo ""
