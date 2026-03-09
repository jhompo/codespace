# ─────────────────────────────────────────────────────────────────────────────
#  Dockerfile del Devcontainer — Spring Boot + Java 21 + Gradle 8.12
#
#  Base: Eclipse Temurin 21 (JDK oficial, Debian Bookworm)
#  Incluye: Java 21, Gradle 8.12, Git, curl, Docker CLI, zip/unzip
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy

# ── Variables ─────────────────────────────────────────────────────────────────
ARG GRADLE_VERSION=8.12
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=${USER_UID}

# ── Herramientas del sistema ───────────────────────────────────────────────────
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    wget \
    git \
    git-lfs \
    zip \
    unzip \
    gnupg \
    lsb-release \
    ca-certificates \
    bash-completion \
    procps \
    htop \
    && rm -rf /var/lib/apt/lists/*

# ── Docker CLI (para docker compose dentro del Codespace) ─────────────────────
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
    | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
    https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" \
    > /etc/apt/sources.list.d/docker.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends docker-ce-cli docker-compose-plugin \
    && rm -rf /var/lib/apt/lists/*

# ── Gradle ────────────────────────────────────────────────────────────────────
RUN wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/local/bin/gradle \
    && rm /tmp/gradle.zip

# ── GitHub CLI ────────────────────────────────────────────────────────────────
RUN curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg \
    | gpg --dearmor -o /usr/share/keyrings/githubcli-archive-keyring.gpg \
    && echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] \
    https://cli.github.com/packages stable main" \
    > /etc/apt/sources.list.d/github-cli.list \
    && apt-get update \
    && apt-get install -y --no-install-recommends gh \
    && rm -rf /var/lib/apt/lists/*

# ── Usuario no-root (vscode) ──────────────────────────────────────────────────
RUN groupadd --gid ${USER_GID} ${USERNAME} \
    && useradd --uid ${USER_UID} --gid ${USER_GID} -m ${USERNAME} \
    && apt-get update \
    && apt-get install -y --no-install-recommends sudo \
    && echo "${USERNAME} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/${USERNAME} \
    && chmod 0440 /etc/sudoers.d/${USERNAME} \
    && rm -rf /var/lib/apt/lists/*

# ── Variables de entorno ───────────────────────────────────────────────────────
ENV JAVA_HOME=/opt/java/openjdk
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}
ENV PATH=${JAVA_HOME}/bin:${GRADLE_HOME}/bin:${PATH}

# ── Directorio de trabajo ─────────────────────────────────────────────────────
WORKDIR /workspaces
USER ${USERNAME}

# ── Verificación final en build ───────────────────────────────────────────────
RUN java -version && gradle --version | head -3
