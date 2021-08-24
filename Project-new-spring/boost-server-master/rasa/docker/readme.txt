#to copy rasa files from folder on local to folder on remote server for example dev.myboost.com

scp docker-compose.yml model-*.tar.gz boost@dev.myboost.com:/data/app/rasa

#edit the following property in docker-compose.yml to match Boost API url path
# callback:
#   url: "http://192.168.200.1:8080/api/...................."
# 1. replace 192.168.200.1 above with IP of docker0 network device
# 2. replace :8080 above with port of tomcat instance
# 3. replace /api above with context path of API deployment

#to run new docker image using docker-compose on remote server for example dev.myboost.com

ssh boost@dev.myboost.com "/data/software/docker-compose -f /data/app/rasa/docker-compose.yml up -d --force-recreate --remove-orphans"

#to run new docker image using docker-compose on remote server with interactive login ssh

ssh boost@dev.myboost.com
#optional
#su - root
/data/software/docker-compose -f /data/app/rasa/docker-compose.yml up -d --force-recreate --remove-orphans
