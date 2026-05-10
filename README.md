# TreasureMap - Android Location-Based Game

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/ZsNerNAP)

## Project Overview
TreasureMap is a location-based 'game' where users can "bury" virtual treasures (In theory indicating the location of real treasure or geocaches) at their current GPS coordinates. Other users can then see search areas on a custom-styled map and attempt to track them down using real-time distance proximity.

---

## Key Features Implemented

### 1. Authentication & Security
- **Google Sign-In Integration:** Secure user authentication using Google Play Services and Firebase Auth.
- **Persistent Sessions:** Users remain logged in across app restarts, with account data managed via Firebase.

### 2. Map & Geospatial Features
- **Custom Map Styling:** A bespoke "Neon/Dark" JSON map style implemented to match the app's aesthetic.
- **Real-time GPS Tracking:** Continuous monitoring of user location to center the camera and calculate distances.
- **Search Area Obfuscation:** To add a gameplay element, treasures are not shown at their exact location. Instead, a randomized offset is used to create a "Search Radius" (approx. 100m) around the actual hidden spot.

### 3. Data Persistence
- **Cloud Synchronization:** Moved from local JSON storage to **Firebase Realtime Database**, allowing for instant data syncing across all users.
- **Dynamic List Filtering:** A comprehensive Treasure List view with filters for "All", "Mine", and "Discovered" treasures.
- **Distance Sorting:** The treasure list automatically sorts items by their physical distance from the user's current coordinates.

---

## Technology Stack
- **Language:** Kotlin
- **Database:** Firebase Realtime Database
- **Auth:** Firebase Authentication & Google Sign-In API
- **Maps:** Google Maps SDK for Android
- **Location:** Google Play Services FusedLocationProviderClient
- **Architecture:** Interface-based Repository pattern (TreasureStore) for flexible data management.

---

## Missing Features & Known Issues
In the interest of transparency, the following features were planned but not fully implemented in this version:

- **Update Functionality:** While the UI allows navigation to an edit screen, the full logic to update existing treasures in Firebase is currently incomplete.
- **Automatic Discovery Detection:** The logic to automatically mark a treasure as "Found" when the user physically enters the exact coordinates is not yet implemented.
- **Enhanced User Profiles:** User avatars and detailed profile statistics (treasure counts) currently use placeholder logic.
- **Smooth GPS Interpolation:** The GPS positioning relies on raw updates and lacks smoothing/interpolation for vehicle-speed movement.
- **Specific Deletion:** Deletion is currently implemented for "all user treasures" or selection-based; specific "Delete by ID" API calls are missing.
- **User Data Deletion:** Deletion of user data / account was not implemented.
- **Treasure Found State:** There is no implementation for triggering the "found" state in treasure in the app.
- **List Filtering:** The list filtering is buggy.
- **No Local Account:** Removed local account creation / login for security and quicker development.
- **UI Updates:** Buggy, Was working, No longer works very well.
- **Many More:** Too many to list.
---

## Credits & Acknowledgments
- **Module Materials:** Core architecture based on labs and lectures from the Mobile App Development module.
- **AI Assistance:** AI tools (Gemini) were used to assist with complex areas including:
  - Geospatial distance calculations and sorting logic.
  - Refactoring the persistence layer for Firebase Realtime Database compatibility.
  - Implementing the Activity Result API for Google Sign-In.
  - Comprehensive code documentation and commenting.
  - This README.md
