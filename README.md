# Pi Solar Monitor Android App (PSM-android-app)

An Android application and companion widgets for the [Pi-Solar-monitor](https://github.com/nkhearn/Pi-Solar-monitor) system.

## 🌟 Features

- **Real-time Monitoring**: Connects to the Pi-Solar-monitor WebSocket server for live data updates.
- **Dynamic Dashboard**: Automatically lists all available metrics retrieved from the API.
- **App Widgets**: Customizable widgets for your home screen to show real-time metrics (e.g., PV Power) and their last update time.
- **Configurable Connection**: Easily set the host and port of your Pi-Solar-monitor instance within the app settings.
- **Native Kotlin**: Built using modern Android development practices, including Jetpack Compose for the UI.

## 🚀 Getting Started

### Prerequisites

- A running instance of [Pi-Solar-monitor](https://github.com/nkhearn/Pi-Solar-monitor) on your local network.
- Android device running Android 5.0 (API 21) or higher.

### Installation

1. Clone this repository.
2. Open the project in Android Studio.
3. Build and run the app on your Android device.

### Configuration

1. Launch the app.
2. Tap the **Settings** icon in the top-right corner.
3. Enter the **Host** IP address and **Port** (default is 8000) of your Pi-Solar-monitor server.
4. Tap **Save Settings**. The app will attempt to connect via WebSocket and fetch the latest data.

## 📡 API Integration

The app utilizes both REST and WebSocket endpoints from the Pi-Solar-monitor:

- **WebSocket (`ws://<host>:<port>/ws`)**: Used for real-time `new_data` broadcasts.
- **REST API (`http://<host>:<port>/api/last`)**: Used to fetch the most recent data point on app startup or when refreshing widgets.
- **REST API (`http://<host>:<port>/api/keys`)**: Used to discover available metrics.

## 🛠️ Architecture

- **Jetpack Compose**: For a modern, declarative UI.
- **ViewModel & StateFlow**: For reactive data management.
- **Retrofit**: For REST API communication.
- **OkHttp**: For WebSocket connections.
- **App Widgets**: For home screen integration.

## 📝 License

This project is licensed under the MIT License.
