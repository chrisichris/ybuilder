Ybuilder - a simple build tool for Yeti the funtional language for JVM
==========================================================

Ybuilder is a buildsystem for [yeti](http://mth.github.com/yeti/) in yeti.

It aims to be an easy-to-use alternative to maven and ant to build 
[yeti](http://mth.github.com/yeti/) projects using yeti itself. It provides 
built-in commands for common tasks like downloading dependencies, 
compiling the project, lauching the REPL with the right classpath etc.

At the base of `ybuilder` (`module ybuilder.core.build`)
is a yeti-wrapper around ant-tasks and a target-execution-mechanism. This can
be used to define ant-scripts coded yeti. 
 
On top of the ant-wrapper there is standard and cutomizable build-script 
(`module ybuilder.core.base`) with predefined 
targets (clean, compile, test etc), directories, 
and maven based dependency- and classpath-management. 

While the base of yubilder is similar to ant the modules on top are similar to
maven in that they provide predefined builds which are meant to be used out of 
the box with a bit customization in 90 % of the usecases 

## Installation

There is no real installation. Just download the `ybuilder.jar` from 
[here](http:) and copy it to the root-directory of your project. 

You can than run ybuilder with
 
    java -jar ybuilder

## Support

Join the discussion mailing list at:

<http://groups.google.com/group/yeti-lang>
 
## Creating a yeti project using ybuilder

>1. make a new directory for your project 
>
>2. copy `ybuilder.jar` to the new directory
>
>3. cd to the new directory
>
>4. To create the directory layout invoke:
>   
>   >java -jar ybuilder.jar newProject
>
>5. edit `project.yeti` file to set the projects name, artifact id etc and add 
>   dependencies 
>
>    project.name := "first-test";
>    project.groupId :="org.foo";
>    project.artifactId := "first-test";
>    project.version := "0.1-alpha";
>    project.description := "First ybuilder based project";    
>    
>    dependency "org.yeti" "yeti" "0.9.3" [];
>    dependency "org.apache.commons" "commons-lang3" "3.0.1" [];
>    dependency "junit" "junit" "3.8.2" [TestScope()];
>    dependency "javax.servlet" "servlet-api" "2.4" [ProvidedScope()];
>
>6. run `java -jar ybuilder.jar` to get help and see that project.yeti compiles
>
>7. add a source file to the src/ directory 
>    
>    file: src/foo.yeti:
>    module foo.yeti;
>    
>    println "Hello world!";
>    0;
>
>8. compile it
>
>   >java -jar ybuilder.jar compile
>   
>9. jar it
>
>    >java -jar ybuilder.jar clean, jar
>    
>10. find the result in target/first-test.jar
    

## Usage

Ybuilder is like ant target based.

From the root directory of your project you can execute different targets with

    java -jar ybuilder.jar target-name [, target-name]*
    
i. e. to clean and compile your project type on the command line 

    java -jar ybuilder.jar clean, compile
 
To get a list of the most important targets (ie compile, test, jar etc) call 
ybuilder without a target

    java -jar ybuilder.jar
    
## Directory Layout

When creating a new project `ybuilder` initializes the following directory 
structure 

    ybuilder.jar    
        <the ybuilder.jar file for the project>
    projetct.yeti
        <the project build configuration - (actually a yeti application)>
    .gitignore
        <default .gitignore - can be removed if git is not used>
    src/
        <all source files (ie .yeti .java) and resources go here>
    test/
        <all test source files and resource go here>
    lib/
        unmanaged/
            <put custom .jar files which are not retrieved from maven repos in 
             here>
        managed/
            <.jar files loaded via dependencies are copied in here
             this directory is managed by `ybuilder` do not modifie it>
        ybuilder/
            <dependenceise for the build-process ybuilder itself>
            extlib/
                <additional .jar files for the buildprocess provided directly>
            extlib_managed/
                <.jar files retrieved as defined in buildDependencies.yeti>
    target/
        <the build directory, everything in there gets cleanded on the clean 
         target>

If you do not want to put your resources in the `src/` dir then you can add

    resources/
        <resources which get copied to the compile result>
    test-resources/
        <test resources>

        
        
## Configuration         
        
The central definition file of ybuilder is the `project.yeti` file. 
`project.yeti` defines the project's name, dependencies, custom build tasks etc. 

### Defining the name, groudId, artifactId, version, description

`project.yeti` is a normal yeti file. You set the name, description etc as
variable assignments to the `project` struct ie:

    project.name := "yebspec";
    project.groupId :="org.yeb";
    project.artifactId := "yebspec";
    project.version := "0.1-alpha";
    project.description := "Specification tests for yeti";    

### Dependencies management

`ybuilder` uses [maven ant-tasks](http://maven.apache.org/ant-tasks/index.html)
for dependency resolution. It also uses the same build classpathes as maven
(compile, test, runtime).

Dependencies are defined in the `project.yeti` file and copied to `lib/managed`.

To retrieve the dependencies you *must call*  
`java -jar ybuilder.jar retrieveDependencies'.

#### Defining Dependencies 

To define dependencies in your `project.yeti` file use the function:

    dependency artifactId groupId version [list of optional arguments]
    
ie:

    dependency "org.yeti" "yeti" "0.9.3" [];
    dependency "org.apache.commons" "commons-lang3" "3.0.1" [];
    dependency "junit" "junit" "3.8.2" [TestScope()];
    dependency "javax.servlet" "servlet-api" "2.4" [ProvidedScope()];

The scopes of the dependency is defined in the list of optional arguments. The 
scopes are the same as in maven (CompileScope, TestScope, RuntimeScope, 
SystemScope, ProvidedScope). The default scope is `CompileScope`.

*NOTE:* After modifying your dependencies or adding new dependencies you have 
to call `java -jar ybuilder.jar retrieveDependencies' so that the new
dependencies are retrieved.

Ybuilder does not retrieve dependencies on each compile because this would take
too long.

#### Remote Repositories 

To add additional remote repositories use 

    remoteRepository id repositoryURL
    
ie

    remoteRepository "springsource-release" 
                     "http://repository.springsource.com/maven/bundles/release"
    
(The chrisichris github repository is already included by default)

#### Using unmanaged dependencies

If you do not want to use the maven dependency mechanism or you need artifacts
which are not in a maven-repo than copy the jars to `lib/unmanaged`. `ybuilder`
will put all jars in this directory to all build-pathes. 
   
## Customizing the build

At the base of ybuilder is a yeti wrapper around ant-tasks and a target system. 
This wrapper is in the `ybuilder.core.build` module. 

On top of that the `ybuilder.core.base` module defines standard targets 
(compile, retrieve dependencies etc). 

That's why in the default `project.yeti` file the `ybuilder.core.base` 
module is loaded at the very top.

### Defining your own targets

To define your own targets use the `target` function from the `build` module:

    target name [options] code-function

- name is the name of the target and has to be unique in the project.
- [options] is a list of following options:
    - Depends target: target which should be executed before
    - InLivecyle target: adds the defined target to Livecycle and adds the 
       livecycle before the named livecycle to the dependencies of this target
    - Description text: description of this target printed in help
    - DependecyOf target: adds this target to the dependencies of the given target
    - Livecycle boolean: marks the target as livecycle
    - DoPreDependencies fn: a fuction which is executed before the dependencies
            get executed
- code-function is project -> (): the code which gets executed (once) when
    the target is invoked
    
ie add this to project.yeti, after defining the dependencies:

    load ybuilder.core.build;
    
    helloCompile = target "helloCompile" [Depends compile] do p:
        println "hello"
    done
    
This target will first invoke compile and than print "hello":

    >java -jar ybuilder.jar helloCompile
    
    [javac] compiling ... files
    hello

### Livecycle targets

Similar to maven the `ybuilder.core.base` module defines different predefined 
livecylce-targets which form a linear dependency build-order.

Livecycles are just normal targets with the livecylce flag. Which indecates
that these targets should be executed before other sibling-dependencies. 
(All targets on which a livecycle target itself depends are of course still
executed before the livecycle target)

These are thought to make extension of the core build-process easier. Ie
if yout want to add some target which depends on the result of the compile
process you just make it depend on the `compile` livecycle. If later you add
another compile-target (ie a scala compiler) you just put it in the `compile` 
livecycle and you do not have to update all targets which depend on `compile`.

To put a target in a livecycle use the InLivecycle option when defining the 
target:

    target "mytest" [InLivecycle test] do p: ... done;

This does two things:

1.) adds "mytest" to the dependencies of "test" so that when the "test" target 
(livecycle) is executed also "mytest" is executed

2.) adds the livecylc before "test" (which is processTestClasses) to the
dependecies of "mytest" so that when "mytest" is run every livecycle up to
"test" (but not test itself) is run first

To list all livecycles in the poject use:

    java -jar ybuilder livecycles

### The predefined livecycles    
##### clean:

* preClean 
* clean
* cleanAll //which also cleans the libs

##### build:

* validate
* initializeBuild
* initialize
* generateResources
* processResources
* compile
* processClasses
* generateTestResources
* processTestResources
* testCompile
* processTestClasses
* test
* preparePackage

##### doc

* pre-doc
* doc

### Ant-Integration

Scripting ant-targets works much the same as in an ant-xml file but instead
of using xml we use two yeti funcitons from
the `ybuilder.core.build` module:

    antTask taskName [hash of attributes] [list of subelements] project; 
    
    el elementName [hash<string,string> of attributes] [list of subelements];
    
- taskName and elementName are the xml-names of the task. Before the name
  you can (seperated by a space) have an url for custom namespaced tasks
- hash of attribtes is a hash<string,string> representing the xml-attributes
- list of subelements is a list of child-tags, creted with the `el` function

The following xml-fragement

    <mkdir dir="some-dir"/>
    <copy todir="some-dir">
        <fileset dir="src/test">
            <include name="**/*.yeti"/>
        </fileset>
    </copy>

is written:

    load ybuilder.core.build;
    
    project = createProject ();
    
    antTask "mkdir" ["dir":"some-dir"] [] project;
    antTask "copy" ["todir":"some-dir"]
            [el "fileset" ["dir":"src/test"]
                [el "include" ["name":"**/*.yeti"] []]]
            project;

Or if you put it in a target you should not create the project but use the
one provided:

    load ybuilder.core.build;
    
    foo = target "copyTest" [] do project:
        antTask "mkdir" ["dir":"some-dir"] [] project;
        antTask "copy" ["todir":"some-dir"]
                [el "fileset" ["dir":"src/test"]
                    [el "include" ["name":"**/*.yeti"] []]]
                project;
    done;
    
`ybuilder` comes with the ant.jar and the maven-ant.jar on the classpath if
you need other tasks then put their jars in the `lib/ybuilder/extlib` directory.
`ybuilder` will pick them up from there.

##Building ybuilder from source

The main sourcode is in `ybuilder/core` dir.

To build it use:

    java -Xmx512M -cp "lib/unmanaged/*" yeti.lang.compiler.yeti project_ybuilder.yeti clean, jary
 
 
    
 



    
