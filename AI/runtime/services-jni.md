# services-jni

## Role

`services-jni` provides native support for runtime features that are easier or only possible to implement in C++. It includes binder retrieval, key-value storage, and anti-tamper style native helpers.

## Module Type

- Android library with JNI/C++ code

## Key Build Facts

- Uses CMake and native sources under `src/main/cpp`.
- Exposes Java/Kotlin wrappers under `src/main/java/tornaco/apps/shortx/j/`.
- Builds a native `services` library loaded at runtime.

## Key Source Areas

- `services-jni/src/main/java/tornaco/apps/shortx/j/Retriever.kt`
- `services-jni/src/main/java/tornaco/apps/shortx/j/KVStore.kt`
- `services-jni/src/main/cpp/Retriever.cpp`
- `services-jni/src/main/cpp/KVStore.cpp`
- `services-jni/src/main/cpp/AntiCrack.cpp`

## Important Entry Points

- `Retriever.kt`: native binder retrieval bridge.
- `KVStore.kt`: native key-value storage wrapper.
- C++ implementation files: low-level runtime support.

## How It Connects

The main `services` layer uses this module when it needs native-backed behavior rather than pure JVM logic.

## Reading Outcome

Read this module only when the code path reaches native storage, binder retrieval, or anti-tamper behavior.
