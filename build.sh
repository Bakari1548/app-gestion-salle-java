#!/bin/bash

# Script de build pour UNIV-SCHEDULER
# Crée le JAR exécutable et prépare les livrables

echo "=== BUILD UNIV-SCHEDULER ==="

# Vérification de Java
JAVA_HOME="/home/bakari/.jdks/temurin-21.0.10"
JAVA_CMD="$JAVA_HOME/bin/java"
JAVAC_CMD="$JAVA_HOME/bin/javac"
JAR_CMD="$JAVA_HOME/bin/jar"
JAVADOC_CMD="$JAVA_HOME/bin/javadoc"

if [ ! -f "$JAVA_CMD" ]; then
    echo "ERREUR: Java n'est pas installé dans $JAVA_HOME"
    exit 1
fi

echo "Version Java: $($JAVA_CMD -version 2>&1 | head -n 1)"

# Création des répertoires
echo "Création des répertoires..."
mkdir -p out/production/univ-scheduler
mkdir -p lib
mkdir -p dist
mkdir -p docs

# Vérification du driver SQLite
if [ ! -f "lib/sqlite-jdbc-3.51.2.0.jar" ]; then
    echo "AVERTISSEMENT: sqlite-jdbc-3.51.2.0.jar non trouvé dans lib/"
    echo "Téléchargement du driver..."
    
    # Téléchargement si wget est disponible
    if command -v wget &> /dev/null; then
        wget -O lib/sqlite-jdbc-3.51.2.0.jar "https://github.com/xerial/sqlite-jdbc/releases/download/3.51.2.0/sqlite-jdbc-3.51.2.0.jar"
    else
        echo "ERREUR: Veuillez télécharger manuellement sqlite-jdbc-3.51.2.0.jar et le placer dans lib/"
        exit 1
    fi
fi

# Compilation
echo "Compilation des sources..."
$JAVAC_CMD -cp ".:lib/sqlite-jdbc-3.51.2.0.jar" -d out/production/univ-scheduler \
    src/*.java \
    src/dao/*.java \
    src/views/*.java \
    src/models/*.java \
    src/services/*.java \
    src/database/*.java

if [ $? -ne 0 ]; then
    echo "ERREUR: Échec de la compilation"
    exit 1
fi

echo "Compilation réussie!"

# Création du manifeste
echo "Création du manifeste..."
cat > MANIFEST.MF << EOF
Manifest-Version: 1.0
Main-Class: Main
Class-Path: lib/sqlite-jdbc-3.51.2.0.jar
Implementation-Title: UNIV-SCHEDULER
Implementation-Version: 1.0
Implementation-Vendor: Universite

EOF

# Création du JAR
echo "Création du JAR exécutable..."
$JAR_CMD cvfm dist/univ-scheduler.jar MANIFEST.MF -C out/production/univ-scheduler .

if [ $? -ne 0 ]; then
    echo "ERREUR: Échec de la création du JAR"
    exit 1
fi

# Copie des ressources
echo "Copie des ressources..."
cp -r src/resources dist/
cp lib/sqlite-jdbc-3.51.2.0.jar dist/lib/

# Copie du script SQL
cp src/resources/sql/init_database.sql dist/

# Création du script de lancement
cat > dist/run.sh << 'EOF'
#!/bin/bash
cd "$(dirname "$0")"
java -jar univ-scheduler.jar
EOF

cat > dist/run.bat << 'EOF'
@echo off
cd /d "%~dp0"
java -jar univ-scheduler.jar
pause
EOF

chmod +x dist/run.sh

# Génération de la Javadoc
echo "Génération de la Javadoc..."
$JAVADOC_CMD -cp ".:lib/sqlite-jdbc-3.51.2.0.jar" -d docs \
    -sourcepath src \
    -subpackages dao \
    -subpackages views \
    -subpackages models \
    -subpackages services \
    -subpackages database \
    -author \
    -version \
    -windowtitle "UNIV-SCHEDULER Documentation" \
    -doctitle "UNIV-SCHEDULER API Documentation" 2>/dev/null

# Création du package de distribution
echo "Création du package de distribution..."
cd dist
zip -r ../univ-scheduler-v1.0.zip .
cd ..

# Test du JAR
echo "Test du JAR..."
timeout 5s $JAVA_CMD -jar dist/univ-scheduler.jar > /dev/null 2>&1
if [ $? -eq 124 ]; then
    echo "JAR fonctionnel (test arrêté après 5 secondes)"
else
    echo "AVERTISSEMENT: Le JAR peut avoir des problèmes"
fi

echo ""
echo "=== BUILD TERMINÉ ==="
echo "Fichiers créés:"
echo "- dist/univ-scheduler.jar (JAR exécutable)"
echo "- dist/univ-scheduler-v1.0.zip (Package complet)"
echo "- docs/ (Javadoc)"
echo "- dist/run.sh (Script Linux/Mac)"
echo "- dist/run.bat (Script Windows)"
echo ""
echo "Pour lancer l'application:"
echo "  cd dist"
echo "  ./run.sh   (Linux/Mac)"
echo "  run.bat    (Windows)"
echo "  java -jar univ-scheduler.jar"
