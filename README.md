# SafetyNova 🛡️

SafetyNova is a completed Android application designed for personal safety, offering features like live location sharing via SMS, emergency contact management, and weather updates.

This repository has been configured to follow GitHub security best practices by keeping all private API keys and configuration files safe and out of version control.

---

## Getting Started

To compile and run this project, you need to set up the local configuration files which are excluded from this repository. Follow the steps below:

### 1. Add Android SDK and API Keys
Create a file named `local.properties` in the root directory of the project (you can copy the `local.properties.template` file as a starting point) and define the following properties:

```properties
# Location of your local Android SDK
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

# Google Maps API Key
# Get one from: https://developers.google.com/maps/documentation/android-sdk/get-api-key
MAPS_API_KEY=your_google_maps_api_key_here

# OpenWeatherMap API Key
# Get one from: https://openweathermap.org/api
WEATHER_API_KEY=your_openweathermap_api_key_here
```

### 2. Set Up Firebase Configuration
1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Register an Android app with the package name `com.example.safetynova`.
3. Download the `google-services.json` file.
4. Place the `google-services.json` file in the `app/` directory of the project (you can refer to `app/google-services.json.template` for the expected format).

### 3. Set Up Live Location Tracking Webpage (Netlify)
The live location sharing feature works by sending a Netlify link to trusted contacts. The frontend tracker web app code is located under `app/src/main/assets/Location/`.

To run the tracker page locally or deploy it:

#### Local Development:
Copy `app/src/main/assets/Location/config.js.template` to `app/src/main/assets/Location/config.js` and add your Firebase and Google Maps API keys:

```javascript
const firebaseConfig = {
  apiKey: "YOUR_FIREBASE_API_KEY",
  authDomain: "YOUR_FIREBASE_AUTH_DOMAIN",
  projectId: "YOUR_FIREBASE_PROJECT_ID",
  storageBucket: "YOUR_FIREBASE_STORAGE_BUCKET",
  messagingSenderId: "YOUR_FIREBASE_MESSAGING_SENDER_ID",
  appId: "YOUR_FIREBASE_APP_ID"
};

const googleMapsApiKey = "YOUR_GOOGLE_MAPS_API_KEY";
```
#### Production Deployment (Netlify):
This repository includes a `netlify.toml` file that automates configuration generation. When linking the repository to Netlify:
1. Set the publish directory to `app/src/main/assets/Location`.
2. Add the following Environment Variables in the Netlify Dashboard (under Site Settings > Environment Variables):
   - `FIREBASE_API_KEY`
   - `FIREBASE_AUTH_DOMAIN`
   - `FIREBASE_PROJECT_ID`
   - `FIREBASE_STORAGE_BUCKET`
   - `FIREBASE_MESSAGING_SENDER_ID`
   - `FIREBASE_APP_ID`
   - `GOOGLE_MAPS_API_KEY`

---
### Notice: 
Remember to **add the tracker page link** to "C:\Users\Dell\AndroidStudioProjects\Safety_Nova\app\src\main\java\com\example\safetynova\LiveLocationSharing.java" on line 192
## Technologies Used
- **Android**: Java, Android SDK, ViewBinding
- **Firebase**: Authentication, Firestore Database, Google Sign-In
- **APIs**: Google Maps SDK, OpenWeatherMap API
- **Web Tracker**: HTML, Vanilla CSS, Javascript
