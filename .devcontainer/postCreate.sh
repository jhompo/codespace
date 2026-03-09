#!/bin/bash
set -e

echo "🔧 Configurando ambiente de desarrollo Java Spring Boot..."

# ─── JAVA_HOME ────────────────────────────────────────────────────────────────
# Inicializa SDKMAN si está disponible (imagen mcr.microsoft.com/devcontainers/java)
if [ -f "/usr/local/sdkman/bin/sdkman-init.sh" ]; then
    source /usr/local/sdkman/bin/sdkman-init.sh
    echo "✅ SDKMAN iniciado"
fi

# Exporta JAVA_HOME si no está definido
if [ -z "$JAVA_HOME" ]; then
    DETECTED_JAVA=$(find /usr/local/sdkman/candidates/java /usr/lib/jvm -maxdepth 1 -mindepth 1 -type d 2>/dev/null | head -1)
    if [ -n "$DETECTED_JAVA" ]; then
        export JAVA_HOME="$DETECTED_JAVA"
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "✅ JAVA_HOME configurado: $JAVA_HOME"
    fi
fi

# Persiste JAVA_HOME en .bashrc para sesiones futuras del terminal
if ! grep -q "sdkman-init" ~/.bashrc 2>/dev/null; then
    echo '' >> ~/.bashrc
    echo '# Java / SDKMAN' >> ~/.bashrc
    echo '[ -f "/usr/local/sdkman/bin/sdkman-init.sh" ] && source /usr/local/sdkman/bin/sdkman-init.sh' >> ~/.bashrc
    echo "✅ JAVA_HOME añadido a ~/.bashrc"
fi

# ─── Gradle Wrapper ───────────────────────────────────────────────────────────
# Genera el wrapper completo (gradlew + JAR) si alguno falta
if [ ! -f "./gradlew" ] || [ ! -f "./gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "⚙️  Generando Gradle Wrapper (gradle wrapper --gradle-version 8.12)..."
    gradle wrapper --gradle-version 8.12
    echo "✅ Gradle Wrapper generado (gradlew + JAR)"
fi
chmod +x ./gradlew
echo "✅ gradlew: permisos asignados"

# ─── Verificar versiones ───────────────────────────────────────────────────────
echo ""
echo "──────────────────────────────────────────────"
echo "  📦 Versiones instaladas"
echo "──────────────────────────────────────────────"
java -version
./gradlew --version | head -4

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
