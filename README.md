To run the program execute: java -jar bookbub.jar </path/to/booksJson> </path/to/keyWordsJson>
Output is printed to console. For the given samples, the following output is produced:

Hunger Games
action, 15.0
sci-fi, 8.0
literary fiction, 0.0

Infinite Jest
literary fiction, 12.0
sci-fi, 0.0
mystery, 0.0


Future functionality:
0) The core program uses Boyer Moore string search algorithm - need to benchmark and research other algorithms
1) Reading named args from command line
2) Better exception handling - for e.g: use custom exceptions
3) Better logging infrastructure - custom handlers and formatters
4) Support for internationalization

