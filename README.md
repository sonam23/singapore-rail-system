1. Starting the web server, which is started at port 8080
mvn spring-boot:run

2. Initializing the Singapore Railway Network
http://localhost:8080/setup

3. Finding route, this is a POST HTTP request.
curl -X POST \
  http://localhost:8080/find-route \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 1f7e4bb6-37d3-d05b-a68c-839ef02115d6' \
  -d '{
	"source": "Holland Village",
	"destination": "Bugis"
}

