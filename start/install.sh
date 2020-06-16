#!/bin/bash
echo Hello this script provide you install CI bot for telegram
read -p 'Provide token for connecting to telegram: ' token
read -p 'Specify the port for running bot: ' port

cat <<END > docker-compose.yml
version: '3'
services:
  db:
    image: mysql:8.0.20
    container_name: mysql-bot
    ports:
     - 3306:3306
    volumes:
      - ./mysql_data:/var/lib/mysql
    environment:
      MYSQL_DATABASE: ci_bot
      MYSQL_ROOT_PASSWORD: root
  bot:
    image: trelloiii/ci-bot
    container_name: ci-bot
    environment:
      MYSQL_HOST: db
      MYSQL_USER: root
      MYSQL_PASSWORD: root
      BOT_TOKEN: $token
      PORT: $port
    ports:
     - $port:$port
    volumes:
      - ./data:/ci-bot/data
      - ./var/run/docker.sock:/var/run/docker.sock
    restart: always
    links:
      - db

END

echo Installing...
docker-compose up -d