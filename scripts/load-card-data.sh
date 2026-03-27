#!/bin/bash

# Script para cargar datos de cartas desde la API del backend
# Este script permite cargar, recargar o limpiar los datos de cartas

API_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para mostrar el uso del script
show_usage() {
    echo -e "${BLUE}=== One Piece Card Manager - Data Loader ===${NC}"
    echo ""
    echo "Uso: $0 [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  load      - Cargar datos de cartas (solo si la BD está vacía)"
    echo "  reload    - Limpiar y recargar todos los datos"
    echo "  clear     - Eliminar todos los datos de cartas"
    echo "  help      - Mostrar esta ayuda"
    echo ""
    echo "Ejemplos:"
    echo "  $0 load"
    echo "  $0 reload"
    echo "  $0 clear"
    echo ""
}

# Función para verificar si el servidor está disponible
check_server() {
    if ! curl -s "$API_URL/cards" > /dev/null 2>&1; then
        echo -e "${RED}✗ Error: No se puede conectar con el servidor en $API_URL${NC}"
        echo "Asegúrate de que el backend está ejecutándose"
        exit 1
    fi
}

# Función para cargar datos
load_data() {
    echo -e "${YELLOW}⏳ Cargando datos de cartas...${NC}"
    response=$(curl -s -X POST "$API_URL/cards/load-data" -H "Content-Type: application/json")
    
    success=$(echo "$response" | grep -q '"success":true' && echo "true" || echo "false")
    message=$(echo "$response" | grep -o '"message":"[^"]*' | cut -d'"' -f4)
    cardsLoaded=$(echo "$response" | grep -o '"cardsLoaded":[0-9]*' | cut -d':' -f2)
    
    if [ "$success" = "true" ]; then
        echo -e "${GREEN}✓ $message${NC}"
        echo -e "${GREEN}✓ Se cargaron $cardsLoaded cartas${NC}"
    else
        echo -e "${RED}✗ $message${NC}"
        exit 1
    fi
}

# Función para recargar datos
reload_data() {
    echo -e "${YELLOW}⏳ Recargando datos de cartas...${NC}"
    response=$(curl -s -X POST "$API_URL/cards/reload-data" -H "Content-Type: application/json")
    
    success=$(echo "$response" | grep -q '"success":true' && echo "true" || echo "false")
    message=$(echo "$response" | grep -o '"message":"[^"]*' | cut -d'"' -f4)
    cardsLoaded=$(echo "$response" | grep -o '"cardsLoaded":[0-9]*' | cut -d':' -f2)
    
    if [ "$success" = "true" ]; then
        echo -e "${GREEN}✓ $message${NC}"
        echo -e "${GREEN}✓ Se cargaron $cardsLoaded cartas${NC}"
    else
        echo -e "${RED}✗ $message${NC}"
        exit 1
    fi
}

# Función para limpiar datos
clear_data() {
    echo -e "${YELLOW}⚠ ¿Estás seguro de que quieres eliminar todas las cartas? (s/n)${NC}"
    read -r response
    if [ "$response" != "s" ] && [ "$response" != "S" ]; then
        echo -e "${BLUE}Operación cancelada${NC}"
        exit 0
    fi
    
    echo -e "${YELLOW}⏳ Eliminando todas las cartas...${NC}"
    response=$(curl -s -X DELETE "$API_URL/cards/clear-all" -H "Content-Type: application/json")
    
    success=$(echo "$response" | grep -q '"success":true' && echo "true" || echo "false")
    message=$(echo "$response" | grep -o '"message":"[^"]*' | cut -d'"' -f4)
    cardsDeleted=$(echo "$response" | grep -o '"cardsDeleted":[0-9]*' | cut -d':' -f2)
    
    if [ "$success" = "true" ]; then
        echo -e "${GREEN}✓ $message${NC}"
        echo -e "${GREEN}✓ Se eliminaron $cardsDeleted cartas${NC}"
    else
        echo -e "${RED}✗ $message${NC}"
        exit 1
    fi
}

# Main
if [ $# -eq 0 ]; then
    show_usage
    exit 0
fi

case "$1" in
    load)
        check_server
        load_data
        ;;
    reload)
        check_server
        reload_data
        ;;
    clear)
        check_server
        clear_data
        ;;
    help)
        show_usage
        ;;
    *)
        echo -e "${RED}✗ Comando no reconocido: $1${NC}"
        show_usage
        exit 1
        ;;
esac
