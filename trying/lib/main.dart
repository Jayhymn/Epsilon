import 'package:flutter/material.dart';
import 'package:english_words/english_words.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Welcome to Flutter',
      home: Scaffold(
        appBar: AppBar(title: Text('Flutter App')),
        body: Center(
          child: RandomWords(),
        ),
      ),
    );
  }
}
class RandomWordsState extends State<RandomWords>{
  @override
  Widget build(BuildContext context) {
    final _suggestions = <WordPair>[];
    final _biggerFont = const TextStyle(fontSize: 18.0);

    Widget _buildSuggestions(){
//      return ListView.builder(
//        padding: const EdgeInsets.all(16.0),
//      )
    }
  }
}

class RandomWords extends StatefulWidget {
  @override
  RandomWordsState createState() => RandomWordsState();
  }
