[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/ZsNerNAP)

## Treasure Hunt App
---

### Features:

- Account creation and login (without passwords or security)
- Accounts are persistant on device (to JSON)
- Custom Themed Map: used JSON map style to fit the neon / dark theme 
- Real-time Location Tracking: Uses Google Play Services to center the camera on the user and to drop "Treasure" pins on the map 
- Treasure locations can be entered (using user location on creation)
- Treasure locations are persistent on device (to JSON)
- Randomised search radius encircling the treaure location (center offset from treasure)
- Functional treasure proximity bar indicating users real distance from the treasures on map coordinates

---

### TODO

treasure:
- [ ] Update treasure
- [ ] delete treasure all
- [ ] delete treasure by id
- [x] link treasure and user
- [ ] find treasure "created by userID"

user:
- [ ] delete user
- [ ] avatar / profile picture

logic:
- [ ] detection for treasure found
- [ ] smooth gps positioning
- [x] Real accounts

UI:
- [ ] Login Screen
- [ ] Splash Screen
- [ ] user profile card
- [ ] treasure info card
