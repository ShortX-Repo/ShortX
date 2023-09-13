# shortx_f

A new Flutter module project.

## Getting Started

For help getting started with Flutter development, view the online
[documentation](https://flutter.dev/).

For instructions integrating Flutter modules to your existing applications,
see the [add-to-app documentation](https://flutter.dev/docs/development/add-to-app).


## Build aar

```agsl
flutter build aar --obfuscate --split-debug-info=build/debug-info
```

## Build error

```agsl

configurations.configureEach {
        resolutionStrategy {
            force 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10'
        }
 }

```