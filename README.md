# Conflict_Checker
[![Build Status](https://travis-ci.com/JordanPaoletti/Conflict_Checker.svg?branch=master)](https://travis-ci.com/JordanPaoletti/Conflict_Checker)

JVM application that checks for conflicts in class schedules

# Authors 
Caleb Cook

Kolton Hahn

Jordan Paoletti

# Velocity
110 mph per hour all day every day

# Build
To build the project, Execute `mvn install` from the top level directory.
 
To run the project, execute `mvn jfx:run`.

To run only the unit tests, execute `mvn test`.

# Deployment

Use `mvn jfx:jar` to create a self contained jar that can easily be ran.
For easy deployment for windows, use [launch4j](http://launch4j.sourceforge.net/) to wrap the jar in a windows executable.
Ideally, the program would be distributed with the help documentation.


# Architectural Overview

#### `src/main/kotlin/bsu.cc`

* `/contraints` -- Read/Write for constraints file and constraints models
* `/parser`
    * `Actions.kt` -- High level parser actions, combines multiple steps from `XlsxParser.kt`
    * `XlsxParser.kt` -- Excel file parser methods, this is where the majority of ApachePOI use occurs
* `/schedule`
    * `ConflictChecking.kt` -- Logic for determining conflicts
    * `Model.kt` -- Data classes for `ConflictChecking`
    * `Produecers.kt` -- Functions mapping between Excel rows and model data classes
* `/views`
    * `FileDropDownFragment.kt` -- View for the constrains drop down selector
    * `MainView` -- Layout and GUI logic for application  
* `Configuration.kt` -- Companion Object with keys for config file
* `MyApp.kt` -- Application entry point
* `Styles` -- Styles used in `MainView`

#### `src/test/kotlin/bsu.cc`
Unit tests for the project. Each component's unit test are in the corresponding folder from 
`main/kotlin/bsu.cc`
