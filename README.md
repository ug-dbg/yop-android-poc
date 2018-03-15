# YOP on Android!
Because... why not ?

## General observations
- Yop relies (a lot) on Java 8 and method references
- Android now supports Java 8
- JDBC does not play well with Android
- An adpater pattern has been (poorly) implemented in Yop to decouple Yop from JDBC
- Come on, let's try this!

## Principles :
- IConnection adapter for Android SQLiteDatabase
- SQLHandler to create, init and get a grip onto the database
- Do some very very simple CRUD operations

## To do :
- Do some more complicated CRUD operations ?
- Weird behavior using *float* field comparison (seems to work with *double*)
- Yop uses the org.reflections library to find implementations of :
    - Yopable classes (when generating database script) but you can give them explicitely 
    - Any field whose type is not concrete and whose implementation is not known (unlikely)
- But org.reflections method *getSubTypesOf* does not seem to work on Android