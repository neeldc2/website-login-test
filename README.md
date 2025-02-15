This project will Act as a microservice that

* will allow user creation with password encryption
* will allow users to login using their credentials
* when using the Access Token, will be able to hit a test API to verify that Access Token is working
* acts as a gateway service that routes API calls to other microservice
* when using the Access Token, will be able to authenticate the API calls meant for other microservice
* will not pass the Access Token to other microservices
* will pass User Context to other microservices so that they know which user has made the API call
* has Flyway which will run during start-up
* produces Refresh Token during Login
* APIs cannot be authenticated using Refresh Token
* Refresh Token can only be used to create Access Token
* Inserts default Roles and Permissions
* Login History is captured that has success and failed logins along with ip address and user agent
* Login History is partitioned with DATETIME for every month till 2026
* Imports student information from Excel via Multi Threading (1k students in less than a second). Example Excel is found
  in classpath. File name: StudentInfo.xlsx

TODO:

* Add roles and permissions API
