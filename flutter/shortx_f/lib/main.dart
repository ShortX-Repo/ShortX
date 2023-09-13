import 'package:flutter/material.dart';
import 'package:mmkv/mmkv.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'ShortX',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.red),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'ShortX Flutter'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  void _testClicked() async {
    // must wait for MMKV to finish initialization
    final rootDir = await MMKV.initialize();
    print('MMKV for flutter with rootDir = $rootDir');

    final mmkv = await MMKV.defaultMMKV(cryptKey: "tornaco.apps.shortx");
    // DeviceId
    print(
        'MMKV res = ${mmkv.decodeString("39A5DC32-B10D-4370-AB98-A9A809256885")}');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              'Welcome to Flutter',
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _testClicked,
        tooltip: 'Test',
        child: const Icon(Icons.animation),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
