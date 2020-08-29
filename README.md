## Running
```bash
docker-compose -f docker-compose-single.yml up -d
sbt "runMain redis.bench.main.MainCE"
```