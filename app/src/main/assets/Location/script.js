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
let map, marker, path = [];

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

    const polyline = new google.maps.Polyline({
        path: path,
        geodesic: true,
        strokeColor: "#FF0000",
        strokeOpacity: 1.0,
        strokeWeight: 2,
    });
    polyline.setMap(map);

    // Firestore document reference
    const docRef = db.collection('User').doc(userId);
    docRef.onSnapshot((doc) => {
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
}
