#!/bin/bash
# Quick deployment script for Docker

set -e

# define colors for text
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}=== Docker Deployment Script ===${NC}"
echo ""

echo -e "${YELLOW}Building WAR file...${NC}"
if mvn clean package; then
    echo -e "${GREEN}✓ WAR file built successfully (local validation)${NC}"
else
    echo -e "${YELLOW}⚠ Local build failed, but Docker will attempt to build it${NC}"
fi
echo ""

echo -e "${YELLOW}Building Docker images...${NC}"
docker-compose build

echo -e "${GREEN}✓ Docker images built${NC}"
echo ""

echo -e "${YELLOW}Starting services...${NC}"
docker-compose up -d

echo -e "${GREEN}✓ Services started${NC}"
echo ""

echo -e "${YELLOW}Waiting for WildFly to start...${NC}"
echo "This may take 1-2 minutes..."
sleep 30

echo ""
echo -e "${GREEN}=== Deployment Complete! ===${NC}"
echo ""
echo "Application URLs:"
echo "  - Application: http://localhost:8080/studentManagement/"
echo "  - Management Console: http://localhost:9990"



