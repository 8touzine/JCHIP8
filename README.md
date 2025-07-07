
# JCHIP8 Emulator

A **CHIP-8** emulator written in Java by [myself](https://github.com/8touzine).


![screenshot](JCHIP8.PNG)
---

##  Features

- Clean and styled JavaFX interface
- Load classic CHIP-8 ROMs
- Graphics display, keyboard input, sound, and timers
- Fetch and decode of Chip8 opcodes 
- Standalone executable JAR build
- debug functionalities

---

##  Prerequisites

- **Java 21** (JDK or JRE)  
   [Download from Adoptium](https://adoptium.net)

- **JavaFX SDK 21 (optional: included in release zip**  
   [Download here](https://gluonhq.com/products/javafx/)

---


##  How to run the emulator

This version may not work with every ch8 ROM **yet**. 
Tested ROM: **Space invaders** and **Cave** included in the zip.

Keys:

1 2 3 4
A Z E R
Q S D F
W X C C


###  Windows

Get zip file in latest release

Double-click `run.bat`  

Or run manually:

```bat
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -jar jchip8-fat-[version].jar
```

---


##  Loading a ROM

1. Place your `.ch8` files in the `roms/` folder
2. The emulator can load a default ROM or open a file chooser to select a ROM

---

##  Dependencies

- JavaFX 21.0.7
- Gradle plugins:
  - `org.beryx.jlink` *(optional for native build)*
  - `org.openjfx.javafxplugin`
- Lombok *(compile-time only)*

---

##  Build (for developers)

```bash
./gradlew clean jar
```

The final JAR will be in `build/libs/jchip8-fat-1.0.jar`  
Run with JavaFX using `--module-path`.

---

## Author

👤 **8touzine**  
GitHub: [github.com/8touzine](https://github.com/8touzine)

---

## License

MIT License

Copyright (c) 2025 8touzine
