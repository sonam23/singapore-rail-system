1. Starting the web server, which is started at port 8080
mvn spring-boot:run

2. Initializing the Singapore Railway Network
http://localhost:8080/setup
curl -X GET \
  http://localhost:8080/setup \;

3. Finding route, this is a POST HTTP request.
curl -X POST \
  http://localhost:8080/find-route \
  -H 'content-type: application/json' \
  -d '{
	"source": "Holland Village",
	"destination": "Bugis"
}

