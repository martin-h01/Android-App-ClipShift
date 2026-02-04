# ClipShift

ClipShift ist eine Android-App zum Herunterladen und Konvertieren von Videos und Audios aus verschiedenen Online-Plattformen (z. B. YouTube).  
Die App richtet sich primär an Lern- und Studienzwecke.

---

##  Funktionsübersicht

###  Einfacher Modus
- URL einfügen
- Format auswählen (MP4 oder MP3)
- Download starten
- Die App wählt automatisch die beste verfügbare Qualität

###  Expertenmodus
- Manuelle Auswahl von:
    - Video-Auflösung (z. B. 1080p, 720p, 144p)
    - Audio-Qualität (z. B. 320 kBit/s, 192 kBit/s)
- Volle Kontrolle über Ausgabeformat und Qualität

###  Technische Umsetzung
- Download-Engine: **yt-dlp**
- Medienverarbeitung: **FFmpeg**
- UI: **Jetpack Compose**
- Architektur: **MVVM (ViewModel + StateFlow)**

---

##  Mehrsprachigkeit

Die App ist so aufgebaut, dass sie **mehrsprachig erweiterbar** ist (über `strings.xml`).



---

##  UI-Tests

- Die App enthält **Compose UI Tests**
- Getestet werden u. a.:
    - Dark-/Light-Mode Umschaltung
    - Expertenmodus (Auswahl von Auflösung / Qualität)
    - Download-Flow (simuliert)
---

##  Emulator- & Geräte-Kompatibilität

###  Unterstützt
- **ARM64 (arm64-v8a)** Geräte & Emulatoren
- Physische Android-Geräte mit ARM-Chips
- ARM-basierte Emulatoren (z. B. Apple Silicon / ARM Images)

###  Nicht unterstützt
- x86 / x86_64 Emulatoren

**Grund:**
- FFmpeg wird als **native ARM64-Bibliothek (`libffmpeg.so`)** eingebunden
- x86-Emulatoren können diese Bibliothek nicht ausführen

 Deshalb ist die App absichtlich auf **ARM-Architektur beschränkt**

---

##  Lizenz & Haftung

Dieses Projekt dient ausschließlich zu **Lern- und Demonstrationszwecken**.  
Die Nutzung erfolgt auf eigene Verantwortung.

