2import 'dart:convert';

import 'package:dynamic_color/dynamic_color.dart';
import 'package:flutter/material.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:http/http.dart';
import 'package:mmkv/mmkv.dart';
import 'package:shortx_f/markdown_ext.dart';
import 'package:shortx_f/subscribe_config.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  static final _defaultLightColorScheme =
      ColorScheme.fromSwatch(primarySwatch: Colors.red);

  static final _defaultDarkColorScheme = ColorScheme.fromSwatch(
      primarySwatch: Colors.red, brightness: Brightness.dark);

  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return DynamicColorBuilder(
        builder: (ColorScheme? lightDynamic, ColorScheme? darkDynamic) {
      return MaterialApp(
        title: 'ShortX',
        theme: ThemeData(
          colorScheme: lightDynamic ?? _defaultLightColorScheme,
          useMaterial3: true,
        ),
        darkTheme: ThemeData(
          colorScheme: darkDynamic ?? _defaultDarkColorScheme,
          useMaterial3: true,
        ),
        home: const MyHomePage(title: 'ShortX Flutter'),
      );
    });
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

  Future<SubscriptionConfig> fetchSubscribeConfig() async {
    final response = await get(
        Uri.parse('http://shortx.emui.tech/api/getSubscriptionConfig'));

    print(response.body);

    if (response.statusCode == 200) {
      return SubscriptionConfig.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to load SubscriptionConfig');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Padding(
          padding: EdgeInsets.all(2.0),
          child: FutureBuilder<SubscriptionConfig>(
            future: fetchSubscribeConfig(),
            builder: (context, snapshot) {
              if (snapshot.connectionState != ConnectionState.done) {
                return const CircularProgressIndicator();
              } else if (snapshot.hasError) {
                return Text('Error: ${snapshot.error}');
              } else {
                return Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    Visibility(
                      visible: true,
                      child: Expanded(
                        child: Markdown(
                          data: snapshot.data?.formatSubscribeInfo() ?? "ERROR.",
                          extensionSet: MarkdownExtensionSet.commonMark.value,
                          styleSheetTheme:
                              MarkdownStyleSheetBaseTheme.cupertino,
                          styleSheet: MarkdownStyleSheet.fromTheme(
                                  Theme.of(context))
                              .copyWith(
                                  h2: Theme.of(context)
                                      .textTheme
                                      .titleLarge
                                      ?.copyWith(fontWeight: FontWeight.w600),
                                  blockquoteDecoration: BoxDecoration(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .surfaceVariant,
                                    borderRadius: BorderRadius.circular(20.0),
                                  ),
                                  blockquotePadding: const EdgeInsets.all(16),
                                  horizontalRuleDecoration: BoxDecoration(),
                                  textScaleFactor: 0.88),
                        ),
                      ),
                    )
                  ],
                );
              }
            },
          ),
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
