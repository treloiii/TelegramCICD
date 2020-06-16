if [ $1 == "start" ]
then
  docker-compose start
else
  docker-compose stop
fi
