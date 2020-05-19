#!/bin/sh

MODULE="sign"
SERVICE_PORT="10080"
ENDPOINT="http://localhost:$SERVICE_PORT/$MODULE"
APP_NAME=$(echo $MODULE | tr "[a-z]" "[A-Z]")

JAR_PATH=/workspace/release
PROP_PATH=/workspace/properties
PID_FILE=/workspace/script/$MODULE.pid
LOG_FILE=/workspace/logs/applogs/app.log

##local
#SERVICE_PORT="8080"
#ENDPOINT="http://localhost:$SERVICE_PORT/$MODULE"
#JAR_PATH=D:/Workspace/Git_Repository/$MODULE/target
#PID_FILE=../$MODULE.pid
#LOG_FILE=D:/workspace/logs/$MODULE/app.log

check_health() {
  res_code=$(curl -s -o /dev/null -w "%{http_code}" $ENDPOINT"/index.html")
  echo $res_code
}

change_health() {
  health_mode=$(echo $* | tr "[A-Z]" "[a-z]")
  if [ $health_mode = "up" ] || [ $health_mode = "down" ] || [ $health_mode = "shutdown" ]
  then 
    res_code=$(curl -s -o /dev/null -w "%{http_code}" -X PUT $ENDPOINT"/health/$health_mode")
    if [ $res_code -eq 200 ]
    then
      return 0
    else
      return 1
    fi
  else
    echo "Only available {UP|DOWN|SHUTDOWN} - cannot '$health_mode'"
    exit 1
  fi
}

check_if_process_is_running() {
  pid=$(ps -ef | grep $MODULE | grep java | awk -F ' ' '{print $2}')
  if [ -z $pid ]
  then
    return 1
  else
    return 0
  fi
}

scenario_test() {
  python test.py 2>&1 /dev/null
  res_code=$?
  if [ $res_code -eq 0 ]
  then
    return 0
  else
    return 1
  fi
}

service_stop() {
  echo "Start application down.."
  if change_health "down"
  then
    echo "Application has been down"
    echo

    echo -n "Waiting for process to terminate.."
    change_health "shutdown"
    count=0
    while [ $count -le 30 ]; do
      if [ -f $PID_FILE ]
      then
        echo -n "."
        sleep 1
      else
        echo
        echo "Process has been terminated"
        return 0
      fi
      count=$(($count+1))
    done

    echo "Cannot terminate process.. There may be threads in progress"
    return 1
  else
    echo "Cannot down application"
  fi
}

case "$1" in
status)
service_status=$(check_health)
if [ $service_status -eq 200 ]
then
  echo "$APP_NAME Service OK [Status=$service_status]"
else
  echo "$APP_NAME Service Unavailable [Status=$service_status]"
fi
;;

stop)
service_status=$(check_health)
if [ $service_status -eq 200 ]
then
  echo "$APP_NAME Service OK [Status=$service_status]"
  echo
  if service_stop
  then
    exit 0
  else
    exit 1
  fi
else
  echo -n "$APP_NAME service already unavailable [Status=$service_status]"
  if [ -f $PID_FILE ]
  then
    echo " but process may not be terminated"
  else
    echo " and process does not exist"
  fi
fi
;;

start)
if [ -z $2 ]
then
  echo "Argument is required. Please input profile"
  exit 1
fi

if [ -f $PID_FILE ] || check_if_process_is_running
then
  echo "Application already running"
  exit 1
fi

echo "Start $APP_NAME application.."
if [ -z $3 ]
then
  echo "> java -Dspring.profiles.active=$2 -cp $JAR_PATH/$MODULE.jar:$PROP_PATH/$2 org.springframework.boot.loader.PropertiesLauncher"
  java -Dspring.profiles.active=$2 -cp $JAR_PATH/$MODULE.jar:$PROP_PATH/$2 org.springframework.boot.loader.PropertiesLauncher > $LOG_FILE &
else
  echo "> java -Dspring.profiles.active=$2 -cp $JAR_PATH/$MODULE$3.jar:$PROP_PATH/$2 org.springframework.boot.loader.PropertiesLauncher"
  java -Dspring.profiles.active=$2 -cp $JAR_PATH/$MODULE$3.jar:$PROP_PATH/$2 org.springframework.boot.loader.PropertiesLauncher > $LOG_FILE &
fi
echo

echo -n "Waiting for application to start.."
count=0
while [ $count -le 30 ]; do
  if check_if_process_is_running && [ $(check_health) -eq 200 ] && [ -f $PID_FILE ]
  then
    echo
    echo "$APP_NAME application started"

    if scenario_test
    then
      echo "Test success"
      exit 0
    else
      echo "Test failed"
      service_stop
      exit 1
    fi
  else
    echo -n "."
    sleep 1
  fi
  count=$(($count+1))
done

echo "$APP_NAME application cannot start"
exit 1
;;

restart)
if [ -z $2 ]
then
  echo "Argument is required. Please input profile"
  exit 1
fi

$0 stop
if [ $? = 1 ]
then
  exit 1
fi
echo

if [ -z $3 ]
then
  $0 start $2
else
  $0 start $2 $3
fi
;;

check)
python test.py 2>&1 /dev/null
res_code=$?
if [ $res_code -eq 0 ]
then
  echo "Test success"
  exit 0
else
  echo "Test fail"
  exit 1
fi
;;

*)
echo "Usage: $0 {start|stop|restart|status}"
exit 1
esac

exit 0