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
* Login History captures if login was via Username/Password or Google SSO
* Imports student information from Excel via Multi Threading (1k students in 8 seconds). Example Excel is found
  in classpath. File name: StudentInfo.xlsx. ThreadPoolTaskExecutor makes use of a TaskDecorator that copies
  User Context from main thread to child thread. ThreadPoolTaskExecutor also makes use of RejectedExecutionHandler
  which blocks the main thread until more child threads can be created.
* Imported users are approved by default, but they are inactive till log-in.
* Self sign-up users will be active, but not approved till ADMIN approves.
* Users can Login via Google SSO
* CORS setup with Allowed Origin of http://localhost:3000 and http://localhost:80. That means, only from
  these URLs are you allowed to invoke the APIs. The API call made with OPTIONS verb should have these 3 headers.
  Origin, Access-Control-Request-Method and Access-Control-Request-Headers.
* Users can update only their own profile. Only admins can update other user profiles.
* Use Kafka to send messages to other microservices.
* Use ActiveMq to send messages to other microservices.
* Has Get Tenant List API which is needed during sign-up to select tenant
* Has Reset Password API
* Has API that sends email to recover password by resetting it. Has refresh token that expires in 3 days.
* has Invite User API
* Get User List API based on User Status: APPROVED, REJECTED, PENDING_APPROVAL
* Admin can approve a list of users. Approved users will be promoted from USER role to STUDENT role
* Admin can reject a list of users
* Actions performed by Admin are audited. Actions such as IMPORT_USERS_VIA_FILE, APPROVE_USERS, REJECT_USERS, ADD_ADMIN
* Admin can add another active and approved user as ADMIN
* Rejected users can request for APPROVAL again. If Admin rejects twice, then users cannot request again.
* Get permissions of user API
* PasswordValidator class will validate if the password is Strong
