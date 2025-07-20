# Jesture (Proof of Concept)

Jesture is a gesture recognition app that can learn, store, and recognize gestuers based on the user's input -- an exercise in exploration of alternative input methods.

## Setup
Jesture is simply a Proof-of-concept and portfolio piece, and Jesture's gesture recognition logic is implemented in a separate backend project, which is **not included in this repo**.   This project focuses on the mobile front-end, structured with **Clean Architecture** and designed to be modular, testable, and easy to extend.  However, in the event of an intrepid reader, interfaces and repositories can be implemented to fulfill these purposes, if desired.

## Usage
To store and recognize gestures using Jesture, the user needs to simply tap the Floating Action Button to open the Gesture Creation dialog.  After entering a Name and Description, the user can then hold the Record button to begin tracking accelerometer readings, which will contiue until the user releases the Record button.  After the gesture is recorded, the user can confirm the dialog, and the recorded gesture will be persisted, adding it to the user's list of gestures.

The user can then record more gestures, or use the Record button displayed below the list of Gestures to recognize a gesture.  Similar to the gesture recording process, accelerometer readings will be tracked while the user is holding the button, and upon releasing, the gesture will automatically be sent to the server for comparison against the user's persisted gestures.  Based on the transmitted accelerometer readings, the server will then return the gesture which is closest.

## Thoughts
As mentioned, Jesture is an exploration of alternative input methods.  Gestures could be used to trigger actions, control devices, recognize patterns of motion, or any number of things.  The gesture recognition of Jesture represents a core component of what would be a larger, more integrated system.

## Features/Technologies
- Gesture recognition via backend API
- GraphQL APIs implemented using Retrofit
- Clean Architecture (separated by domain, data, and presentation layers)
- Dependency Injection using Hilt
- MVVM architecture
- Sensor stream handling (Specifically, accelerometer readings)
- UI Implemented using Jetpack Compose

---
Feel free to reach out if you’re a recruiter or developer interested in discussing the project. I'm happy to walk through the architecture, design decisions, and implementation.
