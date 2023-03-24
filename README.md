# spring-boot-advanced-auth
**An spring boot application with advance authentication mechanism which could be used as standalone webapp as well as integrated into the microservice architecture, acting as an authentication server with minor adjustments. It is aesthetically pleasing as well with the inbuilt template so say goodbye to the hassle of finding designs online.**

\
**1. Login :**
>![image](https://user-images.githubusercontent.com/37661832/227501026-186c59d6-1cd9-4a45-afc6-7d18d028d63b.png)

\
**2. Register :**
>![image](https://user-images.githubusercontent.com/37661832/227500924-a57ff70d-8a7b-4efa-8f4b-4d044b49d973.png)

\
**3. Admin Panel (Preview) :**
>https://user-images.githubusercontent.com/37661832/227435468-a5e72e94-f2d5-4eea-94fc-0d1362bb98d0.mp4

\
**Installation Process**
1. Spring 2.7.7 is used here along with Java 17 so make sure your device is having correct JDK installed. 
2. MySQL is used here as database. **application.properties** needs to be updated with correct datasource URL, Username & Password. Please note that the currect credentials are dummy values, that has to be replaced with your own database. Be cautious with hibernate.ddl-auto property. It is kept as "update" which means your database tables will get updated as soon as the model entities are changed in your project files. You want to use migration tool like "Flyway" if running this app in production.
3. Few values are needed to be present in database when app runs for the first time. Launcher file is configured with "CommandLineRunner" Bean which has been commented out at the moment. You should uncomment it while running the app for the first time. That will auto populate database with two user roles, "USER" and "ADMIN". One user account will be created along with that and that will get assigned to the role. Make sure to comment out that part once database is populated with values to avoid duplication.
4. By default, this app is configured to run as standalone but if you are willing to run this app as microservice, there are pre-configured properties in application.properties file. Proper comments are maintained which will guide you to modify properties file for both the scenarios, **Standalone** as well as **Microservice**.
5. Email me at **jirilzala98@gmail.com** if having any sort of confusion or need assistance with this. I will try my best to respond at the earliest.

\
**Documentation**
This application is configured with two fronts, GUI with stunning admin panel as well as API interface with endpoints for each functionality. Adming urls starts with "/admin" and for authentication you should use "/auth". API Urls starts with "/api/v1/auth".

**1. AUTH (GUI) :**
There are two pages, one for login and another for registration. Subsquent urls will be "/auth/login" and "/auth/register". Role "USER" will be assigned to each user. It hasn't got any authorities to interect with the web-app at the moment. Minumum authority required is Role "ADMIN". I will try to introduce feature of changing the authorities in future. CommandLineRunner will automatically create a user "jiril@admin.com" with password "12345678". You may inject your own user in database manually if preferred. At the moment I'm storing raw passwords along with encrypted ones (using Bcrypt). In production level application, this practice should be avoided.

**2. ADMIN (GUI) :**
You will be redirected automatically to the Admin panel once logged in. Please use url "/admin" to access it manually. Admin panel at the moment has functionality to manage all the users. You can add/view/edit/delete the user. I will introduce functionality to manage Roles in future. Please note that the you will not be able to edit Role of the user once assigned. This functionality is only available via API interface or you could inject it directly into DB.

**3. API :**
All the actions like registering the user/role, accessing user/role info, editing and adding role to existing user. API has its own security configuration. JWT token authentication is needed to access all the endpoints. There are two testing endpoints just to check the availability of the web-app. "/api/v1/auth/public" is a public url, accessible by anyone. "/api/v1/auth/private" is only accessible by authenticated users with "ADMIN" authority. Feel free to use those for testing. Infact all the API endpoints (except the public) could only be reached if user is authenticated and possesses authority of "ADMIN".
\
"/api/v1/auth/login" should be used to authenticate and fetch the JWT tokens. Access token and refresh token will be provided as a response. Access token will last for 2 days before it gets expired. Refresh token will last for 30 days. Use refresh token with endpoint "/api/v1/auth/token/refresh" and you will get new access token. User "/api/v1/auth/validate" if you want to check whether token is valid or not.
\
Please use email as username while registering a new user. Password should be at least 8 char long. You could add more filters and checks for password type to ensure user is using strong password to enhance overall security.
\
For other endpoitns, please refer to controller file "ApiAuthController" which contains all the API endpoints along with its configuration.
\
**I will keep improving this application in future. Please email me your suggestions if you want to be modified in some specific manner. Mail id is jirilzala98@gmail.com. Happy Coding!**
