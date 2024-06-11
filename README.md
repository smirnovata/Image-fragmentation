Как запустить:

```
./handmade-orchestrator> mvn clean package -Pproduction
./handmade-orchestrator> docker build -f src/main/Docker/Dockerfile.jvm -t 
ru-tasm-image-fragmentation/handmade-orchestrator:v1 


./python_server> docker build -f Dockerfile -t 
ru-tasm-image-fragmentation/python-server:v1 . 

.> docker compose up -f compose-dev.yaml
```
