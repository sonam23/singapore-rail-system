# How to RUN the application
1. Install Maven if not already installed.

2. Starting the web server, 
> mvn spring-boot:run

3. After downloading all the dependencies, validate the spring boot application has started by checking this keywork in the console 'Started RailSystemServiceApplication'



# CONTRACT

1. For setting up the network - 
```
	curl -X GET \
	  http://localhost:8080/setup \
	  -H 'content-type: application/json' 
```	  
2. For getting the best route between two stations -
```
	curl -X POST \
	  http://localhost:8080/find-route \
	  -H 'content-type: application/json' \
	  -d '{
		"source": "Holland Village",
		"destination": "Bugis"
		}'
```
3. For getting the shortest path between two stations based on time - 
```
	curl -X POST \
	  http://localhost:8080/find-route-time \
	  -H 'content-type: application/json' \
	  -d '{
		"source": "Holland Village",
		"destination": "Bugis",
		"time": "2019-01-31T23:00"
		}'
```

## SAMPLE RESPONSE

```json{
    "distance": 11,
    "routeStations": [
        "CC21",
        "CC22",
        "EW21",
        "EW20",
        "EW19",
        "EW18",
        "EW17",
        "EW16",
        "EW15",
        "EW14",
        "EW13",
        "EW12"
    ],
    "routeDescription": [
        "Take CC line from Holland Village to Buona Vista",
        "Change from CC line to EW line",
        "Take EW line from Buona Vista to Commonwealth",
        "Take EW line from Commonwealth to Queenstown",
        "Take EW line from Queenstown to Redhill",
        "Take EW line from Redhill to Tiong Bahru",
        "Take EW line from Tiong Bahru to Outram Park",
        "Take EW line from Outram Park to Tanjong Pagar",
        "Take EW line from Tanjong Pagar to Raffles Place",
        "Take EW line from Raffles Place to City Hall",
        "Take EW line from City Hall to Bugis"
    ],
    "stationList": [
        {
            "code": [
                "CC21"
            ],
            "name": "Holland Village",
            "date": "8 October 2011"
        },
        {
            "code": [
                "EW21",
                "CC22"
            ],
            "name": "Buona Vista",
            "date": "12 March 1988"
        },
        {
            "code": [
                "EW20"
            ],
            "name": "Commonwealth",
            "date": "12 March 1988"
        },
        {
            "code": [
                "EW19"
            ],
            "name": "Queenstown",
            "date": "12 March 1988"
        },
        {
            "code": [
                "EW18"
            ],
            "name": "Redhill",
            "date": "12 March 1988"
        },
        {
            "code": [
                "EW17"
            ],
            "name": "Tiong Bahru",
            "date": "12 March 1988"
        },
        {
            "code": [
                "EW16",
                "NE3",
                "TE17"
            ],
            "name": "Outram Park",
            "date": "12 December 1987"
        },
        {
            "code": [
                "EW15"
            ],
            "name": "Tanjong Pagar",
            "date": "12 December 1987"
        },
        {
            "code": [
                "NS26",
                "EW14"
            ],
            "name": "Raffles Place",
            "date": "12 December 1987"
        },
        {
            "code": [
                "NS25",
                "EW13"
            ],
            "name": "City Hall",
            "date": "12 December 1987"
        },
        {
            "code": [
                "EW12",
                "DT14"
            ],
            "name": "Bugis",
            "date": "4 November 1989"
        }
    ],
    "time": 110,
    "numberOfStationChanged": 1,
    "timeCategory": "NIGHT_HOURS"
}
```

## PRINT ON CONSOLE
```
___________________________________________________________________________
Traveling from Holland Village to Bugis at NIGHT_HOURS
Stations travelled: 11
Number of times lines were changed: 1
Route: [CC21, CC22, EW21, EW20, EW19, EW18, EW17, EW16, EW15, EW14, EW13, EW12]
TimeTaken: 110.0
Take CC line from Holland Village to Buona Vista
Change from CC line to EW line
Take EW line from Buona Vista to Commonwealth
Take EW line from Commonwealth to Queenstown
Take EW line from Queenstown to Redhill
Take EW line from Redhill to Tiong Bahru
Take EW line from Tiong Bahru to Outram Park
Take EW line from Outram Park to Tanjong Pagar
Take EW line from Tanjong Pagar to Raffles Place
Take EW line from Raffles Place to City Hall
Take EW line from City Hall to Bugis
___________________________________________________________________________
Holland Village -> [CC21]
Buona Vista -> [EW21, CC22]
Commonwealth -> [EW20]
Queenstown -> [EW19]
Redhill -> [EW18]
Tiong Bahru -> [EW17]
Outram Park -> [EW16, NE3, TE17]
Tanjong Pagar -> [EW15]
Raffles Place -> [NS26, EW14]
City Hall -> [NS25, EW13]
Bugis -> [EW12, DT14]

```
