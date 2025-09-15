# MusicGPT - AI Music Generation App

A sophisticated Android application that simulates the "Prompt to Music Generation" workflow with polished UI/UX animations and seamless state transitions. This app demonstrates modern Android development practices with a focus on smooth user interactions and visual appeal.

## 🎵 Features

- **Prompt-to-Music Generation Flow**: Intuitive UI that simulates AI music generation from text prompts
- **Task Management System**: Track generation progress with real-time status updates
- **Background Audio Playback**: Integrated music player with foreground service support
- **Floating Audio Player**: Persistent mini-player with playback controls
- **Smooth Animations**: Polished transitions and interactive components throughout the app
- **Modern Material Design**: Clean, contemporary UI following Material 3 design principles

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Latest Material Design system
- **Android Gradle Plugin**: 8.11.1
- **Kotlin**: 2.0.21

### Architecture & Patterns
- **MVVM (Model-View-ViewModel)**: Clean separation of concerns
- **Clean Architecture**: Layered architecture with data, domain, and presentation layers
- **Dependency Injection**: Hilt for managing dependencies
- **State Management**: Reactive UI with Compose state management

### Key Libraries
- **Hilt**: `2.52` - Dependency injection framework
- **Media3 (ExoPlayer)**: `1.4.1` - Audio playback and media session management
- **Coil**: `2.4.0` - Efficient image loading and caching
- **Compose BOM**: `2024.09.00` - Jetpack Compose libraries coordination
- **Coroutines**: `1.9.0` - Asynchronous programming and reactive streams

## 🏗️ Architecture

The application follows a clean, modular architecture:

```
app/src/main/java/com/lalas/musicgpt/
├── data/                    # Data Layer
│   ├── constants/          # App-wide constants
│   ├── model/             # Data models and UI state
│   └── repository/        # Data repositories (future implementation)
├── di/                     # Dependency Injection
│   └── PlayerModule.kt    # Audio player dependencies
├── presentation/           # Presentation Layer
│   ├── components/        # Reusable UI components
│   ├── screens/          # App screens
│   └── viewmodels/       # ViewModels for state management
├── service/               # Background Services
│   ├── MusicService.kt   # Foreground service for audio playback
│   └── NotificationDismissReceiver.kt
├── theme/                 # UI Theming
└── utils/                # Utility functions and extensions
```

### Key Components

1. **MusicGPTViewModel**: Manages app state and business logic
2. **MusicService**: Handles background audio playback with Media3
3. **FloatingPlayer**: Persistent mini-player component
4. **TaskCard**: Displays generation tasks with progress indicators
5. **Custom Theme System**: Dark/light mode support with Material 3

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Iguana | 2023.2.1 or newer
- **JDK**: Java 11 or higher
- **Android SDK**: API level 24+ (Android 7.0) minimum
- **Target SDK**: API level 36

### Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/MusicGPT.git
   cd MusicGPT
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository folder
   - Wait for Gradle sync to complete

3. **Build the Project**
   ```bash
   ./gradlew clean build
   ```
   Or use Android Studio's Build menu: `Build > Make Project`

4. **Run the Application**
   - Connect an Android device (API 24+) or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 🎮 Usage & Navigation

### Main Features Demonstration

1. **Home Screen**: 
   - Displays music generation tasks with progress indicators
   - Smooth animations for task state transitions
   - Floating action button for new generation requests

2. **Music Generation Flow**:
   - Tap the "+" button to simulate starting a new generation
   - Watch animated progress indicators and state transitions
   - Generated tracks automatically appear in the task list

3. **Audio Player**:
   - Tap any completed task to start playback
   - Floating mini-player appears with smooth animations
   - Background playback continues with notification controls

4. **Navigation**:
   - Bottom navigation bar with smooth transitions
   - Edge-to-edge design with proper system bar handling

## 🎨 Key Animations & UI Polish

- **Task Card Animations**: Smooth expand/collapse with spring animations
- **Progress Indicators**: Animated circular progress with custom easing
- **Floating Player**: Slide-up animation with blur effects
- **Navigation Transitions**: Crossfade between screens
- **Gesture Interactions**: Ripple effects 

## 🏁 Demo & Testing

### Sample Content
The app includes three sample audio files (`sample1.mp3`, `sample2.mp3`, `sample3.mp3`) located in `res/raw/` for demonstration purposes.

### Test Scenarios
1. Launch the app and observe the home screen animations
2. Trigger a new music generation task
3. Monitor progress animations and state transitions
4. Play a completed track and test the floating player
5. Navigate between different sections using the bottom navigation

## 📱 Device Compatibility

- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36 (Android 14)
- **Supported Architectures**: ARM64, ARM, x86, x86_64
- **Screen Sizes**: Phone and tablet layouts (responsive design)

## 🔧 Development Notes

### Build Configuration
- **Compile SDK**: 36
- **Java Target**: 11
- **Kotlin JVM Target**: 11
- **ProGuard**: Disabled in debug builds

### Permissions
- `INTERNET`: For future API integrations
- `FOREGROUND_SERVICE`: Background audio playback
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK`: Media-specific foreground service
- `WAKE_LOCK`: Prevent device sleep during playback
- `POST_NOTIFICATIONS`: Android 13+ notification support

---

**Note**: This application simulates music generation workflows and does not actually connect to AI music generation APIs. The focus is on demonstrating polished UI/UX implementations and smooth animation techniques in modern Android development.
