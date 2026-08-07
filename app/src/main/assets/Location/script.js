// Initialize Firebase using firebaseConfig loaded from config.js
const app = firebase.initializeApp(firebaseConfig);
const db = firebase.firestore(app);

// Get userId from URL
const urlParams = new URLSearchParams(window.location.search);
const userId = urlParams.get('userId');

if (!userId) {
  alert("User ID is missing in the URL.");
  throw new Error("User ID is required.");
}

// Global variables
let map, marker, polyline, path = [];
let unsubscribeSnapshot = null; // Will hold the unsubscribe function for onSnapshot
const trackingDurationMs = 7200000; // 2 hours in milliseconds

// Initialize Google Map
function initMap() {
  const defaultLocation = { lat: 37.7749, lng: -122.4194 }; // Default to San Francisco
  map = new google.maps.Map(document.getElementById('map'), {
    center: defaultLocation,
    zoom: 15,
  });

  marker = new google.maps.Marker({
    position: defaultLocation,
    map: map,
    title: "User's Location",
  });

  polyline = new google.maps.Polyline({
    path: path,
    geodesic: true,
    strokeColor: "#FF0000",
    strokeOpacity: 1.0,
    strokeWeight: 2,
  });
  polyline.setMap(map);

  // Firestore document reference
  const docRef = db.collection('User').doc(userId);
  let timerStarted = false; // ensures the timer is started only once
  unsubscribeSnapshot = docRef.onSnapshot((doc) => {
    if (doc.exists) {
      const data = doc.data();

      // Update location if available
      const location = data.LiveLocation;
      if (location && typeof location.latitude === 'number' && typeof location.longitude === 'number') {
        const userLocation = { lat: location.latitude, lng: location.longitude };
        map.setCenter(userLocation);
        marker.setPosition(userLocation);

        path.push(userLocation);
        polyline.setPath(path);
      } else {
        console.error("Invalid location data.");
      }

      // Start the 2-hour timer only when trackingStartTime is set (i.e. when SMS is sent)
      if (!timerStarted && data.trackingStartTime) {
        timerStarted = true;
        let trackingStartTime;
        // Check if trackingStartTime is a Firestore Timestamp object
        if (data.trackingStartTime.toMillis) {
          trackingStartTime = data.trackingStartTime.toMillis();
        } else {
          trackingStartTime = new Date(data.trackingStartTime).getTime();
        }
        const elapsed = Date.now() - trackingStartTime;
        const remaining = trackingDurationMs - elapsed;
        startTrackingTimer(remaining > 0 ? remaining : 0);
      }
    } else {
      console.error("No such document!");
    }
  });
}

// Function to start the countdown timer with a given duration (in ms)
function startTrackingTimer(duration) {
  const timerDisplay = document.getElementById('timer');
  let remainingTime = duration;

  // Update timer every second
  const timerInterval = setInterval(() => {
    remainingTime -= 1000;

    // Convert milliseconds to hours:minutes:seconds format
    const hours = Math.floor((remainingTime / (1000 * 60 * 60)) % 24);
    const minutes = Math.floor((remainingTime / (1000 * 60)) % 60);
    const seconds = Math.floor((remainingTime / 1000) % 60);
    timerDisplay.textContent = `Tracking active for: ${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;

    if (remainingTime <= 0) {
      clearInterval(timerInterval);
      timerDisplay.textContent = "Tracking expired.";
      alert("Tracking period has ended.");
    }
  }, 1000);
}

// Helper function to pad time values with leading zeros
function pad(number) {
  return number < 10 ? '0' + number : number;
}

// Load Google Maps API dynamically using key from config.js
(function loadGoogleMapsScript() {
  if (typeof googleMapsApiKey === 'undefined' || !googleMapsApiKey) {
    console.error("Google Maps API Key is not defined in config.js");
    return;
  }
  const script = document.createElement('script');
  script.src = `https://maps.googleapis.com/maps/api/js?key=${googleMapsApiKey}&callback=initMap`;
  script.async = true;
  script.defer = true;
  document.head.appendChild(script);
})();
