const firebaseConfig = {
    apiKey: "AIzaSyATLMdTgM_aNtrLAPtDM0LVsgGdh1LkJTI",
    authDomain: "safetynova-27b76.firebaseapp.com",
    projectId: "safetynova-27b76",
    storageBucket: "safetynova-27b76.firebasestorage.app",
    messagingSenderId: "193867853435",
    appId: "1:193867853435:web:839f6d53ad11789c1ffcde",
};

const app = firebase.initializeApp(firebaseConfig);
const db = firebase.firestore(app);

const urlParams = new URLSearchParams(window.location.search);
const userId = urlParams.get("userId");

// Display an alert and throw an error if `userId` is missing
if (!userId) {
    alert("User ID is missing in the URL. Please check the link.");
    throw new Error("User ID is required.");
} else {
    alert(`User ID: ${userId}`); // Alert the user ID to confirm
}

let map, marker, path = [];
const MAX_PATH_LENGTH = 100; // Limit to the last 100 points

function initMap() {
    const defaultLocation = { lat: 37.7749, lng: -122.4194 }; // Example: San Francisco
    map = new google.maps.Map(document.getElementById("map"), {
        center: defaultLocation,
        zoom: 15,
        styles: [
            { elementType: "geometry", stylers: [{ color: "#ebe3cd" }] },
            { elementType: "labels.text.fill", stylers: [{ color: "#523735" }] },
            { elementType: "labels.text.stroke", stylers: [{ color: "#f5f1e6" }] },
        ],
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

    // Firestore reference to the user's document
    const docRef = db.collection("User").doc(userId);

    docRef.onSnapshot(
        (doc) => {
            if (doc.exists) {
                console.log("Document data:", doc.data());
                const location = doc.data().LiveLocation;

                if (location && typeof location.latitude === "number" && typeof location.longitude === "number") {
                    const userLocation = { lat: location.latitude, lng: location.longitude };
                    const timestamp = location.timestamp ? new Date(location.timestamp) : "No timestamp";

                    console.log(`Location at ${timestamp}:`, userLocation);

                    // Update map center and marker position
                    map.setCenter(userLocation);
                    marker.setPosition(userLocation);

                    // Update polyline path
                    path.push(userLocation);
                    if (path.length > MAX_PATH_LENGTH) {
                        path.shift(); // Remove the oldest point if path exceeds limit
                    }
                    polyline.setPath([...path]);
                } else {
                    console.error("Invalid or missing LiveLocation data in Firestore.");
                    alert("LiveLocation data is missing or invalid.");
                }
            } else {
                console.error("No document found for the given userId.");
                alert("No data found for this user.");
            }
        },
        (error) => {
            console.error("Error fetching document:", error);
            alert("An error occurred while fetching user data.");
        }
    );
}

window.onload = () => {
    try {
        if (!firebase.apps.length) {
            firebase.initializeApp(firebaseConfig);
        }
        initMap();
    } catch (error) {
        console.error("Error initializing the map or Firebase:", error);
        alert("Failed to initialize the application. Please try again later.");
    }
};
