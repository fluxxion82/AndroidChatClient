# AndroidChatClient

A very simple android app that will connect to the ChatServer Project. Must start the chat server first. This was a very quick protoype, so not much time was spent on a polished UI. Was testing out queueing offline messages and sending the queue when connection was re-established. If you type
> logout

it will log you out of the server and exit the app. If no messages appear on screen after entering the server info, wait. To change the ip or port of the server, you will have to clear out the app data. Messages sent offline will be queued until the next time you connect to the server.


--------
## Architecture

This approach is also known as _Clean Architecture_.

Application is divided into three layers with corresponding modules:

- Domain Layer  
    - `:domain`  
    Defines entities, interfaces and use cases. 
    These describe core functionality of the app.
     Use cases are the public interfaces of the domain to be used by presentation layer.
     Use cases contain business logic.
- Data layer  
    - `:data:api` -
    Api related classes
    - `:data:database`   
    Contains persistent storage for apps information. 
    Its responsibility is to provide convenient way to switch to other database provider in the future.
	- `:data:platform`   
    Stores small, unrelated and purely Android Framework related classes like SharedPreference based implementations, Android services access etc.
    Original plan for this module was to avoid having multiple single classes modules.
    - `:data:remote`   
    Basic and temporal storage for all remote related implementations. 
    - `:data:logging`
    Custom logging class
    - `:data:mocked`   
    Mirrors `:data:remote` providing ability to have repeatable, predictable environment for all api calls.
    
- Presentation layer
    - `:presentation:v3app` - Actual app code. 
    Provides Android framework classes, views with their logic stored in ViewModels.
    It should not rely on any of data layer dependencies. 

Most of the actual *logic* in the app will be implemented in the *domain* module, which is pure Kotlin module (without Android dependencies).
All classes will follow dependency inversion rules.
 This will allow to test domain easily.  
 Actual implementations of functional group interfaces will be injected using Dependency Injection framework.

Each layer consists of one or more separate Gradle sub-projects, organised into functional groups.
There may exist multiple sub-projects for one functional group containing different implementations
(e.g. REST implementation of gateway interfaces vs GraphQL implementation of gateway interfaces).

There are two additional modules within the main project:
- `buildSrc` - Does not contain any functional code.  
Encapsulates complex build logic.
Contains project-wide module configurations only. 
Exposes reusable plugins and tasks that can be easily added to project modules.
- `app` - Does not contain any functional code.  
Contains dependency injection configuration. 
Due to Android Framework requirements serves as a startup point so main Application class can be found there. 
Dependency injection has been implemented with Dagger2, taking benefit from `dagger-android` library.



add custom tasks:

- `./gradlew projectLint` - runs Android Lint checks for all modules.
- `./gradlew projectTest` - runs tests for all modules.
- `./gradlew projectCodestyle` - performs codestyle check for all modules.
- `./gradlew dependencyUpdates` - generates report of used used dependencies vs most recent versions.


## Codestyle

Codestyle rules are configured and checked using 
[Detekt](https://github.com/arturbosch/detekt), 
[ktlint](https://github.com/jeremymailen/kotlinter-gradle) and 
[Android Lint](https://developer.android.com/studio/write/lint) rules.