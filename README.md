1. Install Maven if not already installed.

2. Starting the web server, 
mvn spring-boot:run

3. After downloading all the dependencies, validate the spring boot application has started by checking this keywork in the console 'Started RailSystemServiceApplication'

4. Use any tool to make the API call, POSTMAN, Rest Assured, Swagger or simple CURL command, for POST/GET API calls

5. Initializing the Singapore Railway Network via GET API 
http://localhost:8080/setup

6. Expected response from the GET API - 'Successfully Setup Network'

7. POST HTTP request.
curl -X POST \
  http://localhost:8080/find-route \
  -H 'content-type: application/json' \
  -d '{
	"source": "Holland Village",
	"destination": "Bugis"
}'

