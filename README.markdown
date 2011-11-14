Ybuilder - a simple build tool for Yeti the funtional language for JVM
==========================================================

Ybuilder aims to be an easy-to-use alternative to maven and ant to build 
[yeti](http://mth.github.com/yeti/) projects. It provides built-in commands for 
common tasks like downloading dependencies, compiling the project, lauching the
REPL etc.

At the base of Ybuilder is a module to call ant-tasks from yeti and to define
build-scripts in yeti. On top of this there is a predefined build module which
handles dependency-management, manages different classapthes,
provides the commands for compiling, launching the repl etc.   
 
## Installation

Installation is easy. Just download the `ybuilder.jar` file, copy it into 
a fresh project-directory, from there call `java -jar ybuilder newProject` and
edit the created `project.yeti`file to set your projects name and specify 
dependencies etc.

## Usage

The central definition file of ybuilder is the project.yeti file in the
root-dir of your project. There you can add dependencies, define additional tasks
change directories etc. Please consult the created file where examples are given
in the comments.

From the command-line you can execute different targets with

    java -jar ybuilder.jar target-name

To get a list of the most important tasks (ie compile, test, jar etc) call 
ybuilder without a target

    java -jar ybuilder.jar

You can also chain targets togehter in a single command by using commas:

    java -jar ybuilder.jar clean, compile
    
Note the commands have to be run from within the project directory.

    
