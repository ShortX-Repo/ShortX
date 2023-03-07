./gradlew clean
./gradlew -no-daemon :patch-magisk:bridge-dex-app:extractBridgeJar
./gradlew -no-daemon :patch-magisk:module:zipRelease
