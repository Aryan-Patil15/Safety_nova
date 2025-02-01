// Firebase Configuration
const firebaseConfig = {
  apiKey: "AIzaSyATLMdTgM_aNtrLAPtDM0LVsgGdh1LkJTI",
  authDomain: "safetynova-27b76.firebaseapp.com",
  projectId: "safetynova-27b76",
  storageBucket: "safetynova-27b76.firebasestorage.app",
  messagingSenderId: "193867853435",
  appId: "1:193867853435:web:839f6d53ad11789c1ffcde"
};

// Initialize Firebase
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
  unsubscribeSnapshot = docRef.onSnapshot((doc) => {
    if (doc.exists) {
      const location = doc.data().LiveLocation;
      if (location && typeof location.latitude === 'number' && typeof location.longitude === 'number') {
        const userLocation = { lat: location.latitude, lng: location.longitude };
        map.setCenter(userLocation);
        marker.setPosition(userLocation);

        path.push(userLocation);
        polyline.setPath(path);
      } else {
        console.error("Invalid location data.");
      }
    } else {
      console.error("No such document!");
    }
  });

  // Start timer countdown
  startTrackingTimer(trackingDurationMs);
}

// Function to start the 2-hour countdown timer
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
    }
  }, 1000);

  // Stop tracking after the specified duration (2 hours)
  setTimeout(() => {
    // Unsubscribe the onSnapshot listener to stop location updates
    if (unsubscribeSnapshot) {
      unsubscribeSnapshot();
      unsubscribeSnapshot = null;
    }
    timerDisplay.textContent = "Tracking expired.";
    alert("Tracking period has ended.");
  }, duration);
}

// Helper function to pad time values with leading zeros
function pad(number) {
  return number < 10 ? '0' + number : number;
}
