# Quick Start Guide - KonnxtNstaxK v1.0

## 🚀 Get Started in 3 Steps

### 1. Configure Android SDK

Create `local.properties` in the project root:

**Windows:**
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

**Mac/Linux:**
```properties
sdk.dir=/Users/YourUsername/Library/Android/sdk
```

Or set environment variable:
```bash
export ANDROID_HOME=/path/to/android/sdk
```

### 2. Build the Project

```bash
# Windows
gradlew.bat clean assembleDebug

# Mac/Linux
./gradlew clean assembleDebug
```

### 3. Run Tests

```bash
# Windows
gradlew.bat test

# Mac/Linux
./gradlew test
```

## 📱 Install on Device

```bash
# Connect device via USB with debugging enabled
gradlew installDebug
```

## 🎮 Game Features

### Ball Sort Puzzle
- Tap tubes to select and move colored balls
- Match all balls of the same color in each tube
- Use undo button to reverse moves
- Progress through increasing difficulty levels

### Multiplier Drop
- Select multiplier (1x, 2x, 3x, 5x, 10x)
- Drop chips into columns
- Higher multipliers = higher risk + reward
- Cash out before losing all lives

### Connect Four
- Drop chips to connect 4 in a row
- Play against AI opponent
- First to connect 4 wins

## 🎨 Customization

### Settings Screen
- Sound effects on/off
- Music on/off
- Volume control
- Animations on/off
- Glow effects on/off
- Vibration on/off
- Tutorials on/off

### Shop Screen
- Purchase character chips with coins
- Unlock special characters
- Customize your gaming experience

## 🧪 Testing

### Run All Tests
```bash
gradlew test
```

### Run Specific Test
```bash
gradlew test --tests BallSortGameTest
gradlew test --tests MultiplierGameTest
gradlew test --tests ConnectFourGameTest
```

### View Test Reports
```
app/build/reports/tests/testDebugUnitTest/index.html
```

## 🔍 Lint & Code Quality

```bash
# Run lint checks
gradlew lint

# View lint report
app/build/reports/lint-results-debug.html
```

## 📦 Build Variants

### Debug Build
```bash
gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
```bash
gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

## 🐛 Troubleshooting

### SDK Not Found
- Ensure `local.properties` exists with correct SDK path
- Or set `ANDROID_HOME` environment variable

### Build Fails
```bash
# Clean and rebuild
gradlew clean
gradlew assembleDebug
```

### Tests Fail
```bash
# Run with stacktrace
gradlew test --stacktrace
```

### Gradle Issues
```bash
# Clear Gradle cache
gradlew clean --refresh-dependencies
```

## 📚 Project Structure

```
app/src/main/java/com/neon/
├── connectsort/
│   ├── core/
│   │   ├── data/              # Data layer (Repository, DataStore)
│   │   └── AppContextHolder.kt
│   ├── navigation/            # Navigation routes and helpers
│   ├── ui/
│   │   ├── screens/           # Composable screens
│   │   │   └── viewmodels/    # ViewModels for each screen
│   │   └── theme/             # Theme, colors, components
│   └── MainActivity.kt
└── game/
    ├── common/                # Shared game logic
    ├── ballsort/              # Ball Sort game logic
    ├── connectfour/           # Connect Four game logic
    └── multiplier/            # Multiplier game logic
```

## 🎯 Key Files

- **Game Logic**: `app/src/main/java/com/neon/game/`
- **UI Screens**: `app/src/main/java/com/neon/connectsort/ui/screens/`
- **ViewModels**: `app/src/main/java/com/neon/connectsort/ui/screens/viewmodels/`
- **Theme**: `app/src/main/java/com/neon/connectsort/ui/theme/`
- **Tests**: `app/src/test/java/com/neon/`

## 💡 Development Tips

### Hot Reload
- Use Android Studio's "Apply Changes" (Ctrl+F10 / Cmd+F10)
- Compose Preview for instant UI feedback

### Debugging
- Use Logcat in Android Studio
- Set breakpoints in ViewModels and game logic
- Use Compose Layout Inspector

### Performance
- Enable hardware acceleration (already configured)
- Use Compose performance profiling
- Monitor memory usage in Android Profiler

## 🔗 Useful Commands

```bash
# List all tasks
gradlew tasks

# Build and install
gradlew installDebug

# Uninstall
gradlew uninstallDebug

# Generate dependency report
gradlew dependencies

# Check for dependency updates
gradlew dependencyUpdates
```

## 📞 Support

For issues or questions:
1. Check `REFACTORING_SUMMARY.md` for detailed documentation
2. Review test files for usage examples
3. Check Android Studio's Build Output for errors

---

**Happy Gaming! 🎮**
