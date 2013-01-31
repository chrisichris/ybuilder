Ybuilder - a simple build tool for Yeti the funtional language for JVM
==========================================================

*Important Note: Currently Jan 31st 2013 ybuilder is in reworking and 
has some serious bugs which make this version unusable. It is here just for
testing. This should be fixed in a few days

Please use in the mean-time olderversions*

Ybuilder is a yeti [yeti](http://mth.github.com/yeti/) wrapper around ant 
combined with the dependency management of maven. 

Custom buildscripts can be defined from ground up like with ant but written
in yeti instead of ant-xml.There are
built-in commands for common tasks like downloading dependencies, 
compiling the project, lauching the REPL with the right classpath etc.

On top of the ant-wrapper there is standard and cutomizable build-script 
(`module ybuilder.core.base`) with predefined 
livecycle-targets (clean, compile, test etc), directories, 
and maven based dependency- and classpath-management. 

## Installation and Updates / Download

Ybuilder's functinonality is contained in a single all-in one jar. You can
either download that directly and copy it in each project or use the launcher.

### Pre-requisites

For both alterantives Java7 is needed, preferable on the path.

### Direct Usage of the jar

Everything needed to run ybuilder is in the executable jar 
`ybuilder-lib-0.6.jar`.

Just download it from:

<http://chrisichris.github.com/chrisis-maven-repo/ybuilder/lib/ybuilder-lib-0.6.jar>

copy it to your project directory and rename it to `ybuilder.jar`

Now you can launch it with:

	>java -jar ybuilder.jar

The advantage of this approache is that you will always use the same version
of ybuilder for this single project, which makes the build more stable.

The disadvantage is that the `ybuilder-lib-0.6.jar` has a size of about 4MB and
storing it with each project maybe too space consuming in your repo.

### Installation using the launcher

With the second alternative a launcher is used. The launcher is also an 
executable jar called `ybuilder.jar` but it is only about 4KB big.

The first time it is executed, the launcher downloads the 
above `ybuilder-lib-0.6.jar` to the `userhome/.ybuilder/lib` directory and 
than delegates every call to this jar.

For each project of the same user the launcher uses the same 
`ybuilder-lib-0.6.jar` and version.

You can download the launcher from this link:

<http://chrisichris.github.com/chrisis-maven-repo/ybuilder/ybuilder.jar>

Copy the launcher jar again to the root-directory of your project. 

It can be run exactly like the lib jar with
 
    >java -jar ybuilder.jar

The advantage of the launcher is that it is much smaller than the lib if
it is stored with your project and that one update updates all of the launchers.

### Updating the ybuilder lib

Because ybuilder is still in development you should quite regularly update.

For the direct usage just replace the latest version of the jar with a new one.
The latest version is always found at the download link above.

To update the lib used with the launcher run the launcher with

	>java -jar ybuilder.jar -update

Or if you want to update from a custom-url use

	>java -jar ybuilder.jar -update http://custom_url_with_ybuilder/lib
	
This will redownload the `ybuilder-lib-0.6.jar` to the `~/.ybuilder/lib`
directory.

## Support

<http://groups.google.com/group/yeti-lang>
 
## Ybuilder basics

### Targets

Every ybuilder project is - like an ant-project - made up of one or more 
targets which can depend on each other. Each target represent a unit of work ie.
init, compile etc. which is executed only once during a build. 

### Hello World

You run a ybuilder project using the `ybuilder.jar`, which executes 
the `project.yeti` (the build-script). The build-script insantiates and 
configures the targets of the project.

To try that out create the following `project.yeti` file:

	load ybuilder.core.build;

	config = createBuildConfig();
	_ = target config "hello" "world" [] do ap:
		println "Hello World";
	done;

	run config;

In a command-line shell execute the build script:

	>java -jar ybuilder.jar hello:world
	Hello World!

What's going on here? 

When the `ybuilder.jar` is executed it the yeti.jar, ant.jar and ant-maven.jar 
to the classpath and executes build-script `project.yeti`.

The build-script first creates a `config` structure. This
config structure is used to register the target using the `target` function.

The target function is than used create the `hello:world` target and register
it in the config structure. The `hello:world` target prints "Hello World!" 
when it is excuted.

The `config` structure now contains the target. As a last step the 
the `config` structure is given to the `run` function.

The `run` function reads from the command-line-args the name of the target
to execute (`hello:world`), looks up the target by name in the `config`
and finally executes it.

That's it.

### Buildscripts are yeti code
All `project.yeti` files are normal yeti programms. You can use all the
language constructs to construct them.

### Targets
As seen the target function is used to create and register targets with config.

The target function takes as arguments:
- config: this is the config structure it registers the target with
- groupname: the groupname of the target 
- name: the name of the target in the group
- a list of options:
	- Depends target: A target which gets executed before this target
	- Description string: A description of the target
	- DependencyOf otherTarget: add this target to the depedency of the 
				other target
	- ShortName(): register this target also under its name only. So it can
	               be executed whithout the group prefix (ie `compile` 
				   instead of `livecyle:compile`)
- the target's function: This is executed when the target is run.
 	The target function in turn gets a `project` structure which contains
	commandline args, properties the targets already executed and an 
	AntProject used for executing ant-tasks.

The target function retuns a target struct which can be used to reference
the target.

To try it out:

To try that out create the following `project.yeti` file:

	load ybuilder.core.build;

	config = createBuildConfig();
	
	taskOne = target config "hello" "one" 
		[Description "Prints hello"] do ap:
		println "Hello World!";
	done;

	taskTwo = target config "hello" "two" 
			[Depends one,
			Description "greets target",
			ShortName()] do p:
			println "Hello Task";
	done;

	run config;

In a command-line shell execute the build script:

	>java -jar ybuilder.jar two
	Hello World!
	Hello Task!

### Default Target

A target which is run when no target is named on the command line can be set
on config

	config.defaultTarget = config.livecycleTargets.compile;

If no default target is set. `Help` is executed.

### Using Ant Tasks

Ybuilder provides integration for Ant tasks.

The module `yubilder.core.build` contains the function `antTask` which is used
to define and execute an ant task very similar to what you would do in
xml.

Ybuilder tries to be as close to the xml-definitions of ant-tasks as 
possible, so that the noumerous ant-examples can be entered more or less 
one to one, with the same names and value of elements, attributes etc.

Elements are represented by the `el elementname attributes sublements` 
function. Where elementname is a string, attributes is as hash<string,string> and
subelements is a list of other elements.

To be as close as possible to ant-xml Ybuilder does not 
instantiate/call the various task java-classes directly, 
but rather drives the Ant-SAXHandler to generate the tasks.

The antTask function is of the form: 
`antTask taskname attributes subelements project` where

- name: the xml-element name of the task
- attributes: a hash<string,string> for the xml-attributes for the task (names
	and values are exactly like in xml)
- a list of <xmlelement> which is represent the xml-content of the task.
	- an xmlelement is either a Text string | Element {name is string,
	attributes is map<string,string>, subelements is list<xmlelement> 
	structure.
	You use the el function to construct elements
- the project-structure: which is given to the target-function

For example the following ant-xml task definition:

	<javac srcdir="src" 
		   destdir="target/classes" 
		   includes="**/*.jar" 
		   fork="true">
		<classpath>
		   		<pathelement location="classes"/>
				<fileset dir="lib">
					<include name="**/*.jar"/>
				</fileset>
		</classpath>
	</javac>

is expressed in yeti-code like this:	

	antTask "javac" 
		["srcdir" : "src", 
		 "destdir": "target/classes",
		 "includes":"**/*.java",
		 "fork": "true"]
		 [el "classpath" [:]
			[el "pathelement" ["location":"classes"],
			 el "fileset" ["dir":"lib"] 
				[el "include" ["name":"**/*.jar"][]]]]
		project;

The task is immidiately executed when the function is called.

The `project` struct (last argument) is either gotten from 
the argument to the target function:
	
	target config "group" "name" [] do project:
		antTask "mkdir" ["dir":"temp"] [] project;
	done;

or it can be created directly in the config-script, in which case the
ant-task is executed immidiately when the config-script is evaluated.

	antTask "mkdir" ["dir":"temp"] [] (createProject());


## Using the ybuilder.jar

With the ybuilder.jar you basically execute targets, where targets are
sperated by `,` 


    java -jar ybuilder.jar target-name [, target-name]*
    
i. e. to clean and compile your project type on the command line 

    java -jar ybuilder.jar clean, compile
 
You can also specify arguments for a target. The arguments come right
after the targetname.

	java -jar ybuilder.jar new myProjectName chrisichris/basic

Here `new` is the target name and `myProjectName` and `chrisichris/basic` are
the arguments.

You can exclude certain targets form the exectuion with `-x`

	java -jar ybuilder.jar compile -x "livecycle:init"

To get a list of targets use help

	java -jar ybuilder.jar help

## Template support

Ybuilder can generate files and directories from templates published on github
or another git-repository.

### Usage

Template repsitories should reside on git-hub and end with `.ybtr`.

To download such a template execute the `new` target from the parent-directory
of the new project.

	>java -jar ybuilder.jar new new_project_name chrisichris/basic

Where`new_project_name` is the name of the subdirectory of the new project and
and `chrisichris/basic` points to the github repository 
`chrisichris/basic.ybtr`. 

You will than be prompted for different properties. Enter them
or leave the default values given in square brackets.

After that ybuilder will create a new directory  
and just copy the content of the repository to the new 
directory.

The properties entered are applied to replace tokens in the text-files of
the template-repository when copying the text. This is done using
the ant replace tokens filter 
<http://ant.apache.org/manual/Types/filterchain.html#replacetokens>.

### Creating your own Template repository

Creating your own template-repository is easy. Just create a github
project with a name ending with `.ybtr` and add all the neccessary
direcotries and files

If you want to apply properties as tokens add a `ybtemplate.properties` file.

For example:

	group_id = @name@
	version = 0.1-alpha
	description = some description

The user is asked to enter for each property a value. The default value ist
the value given in the `ybtemplate.properties` file. If you want to refer to 
another a property before as the default value
use `@propertyName@` in the value field.

The property `name` is always present. It is the name given as directory
to the `new` target.

Each `@propertyName@` token in any of the text files of template is than 
replaced with the value the user entered ie.

If the template's project.yeti file is:

	config = baseConfig();
	confg.name := "@projectname@";

and the user enters for projectname foo. Than the project.yeti created will be

	config = baseConfig();
	confg.name := "foo";

## Yeti projects

So far everything shown was a general purpose ant-wrapper, which does not build
anything out of the box.

However because most yeti/java projects are quite similar Ybuilder has a 
yeti-module (`ybuilder.core.base`) which configures a standard yeti project.
It creates a standard directory-layout, registers targets for 
maven-dependency-management, for compiling both java and yeti classes, for
testing, running and packaging.

It is important to note that the `ybuilder.core.base` module does nothing 
special it just uses the `ybuilder.core.build` module exactly like 
described in the previous section and creates this way a standard-project.
    
### Creating a yeti project using ybuilder

>1. Load a project template from github.
>
>   >java -jar ybuilder.jar new projectNaem chrisichris/basic
	
	This will create a new directory "projectName" based on the
	github repository chrisichris/basic.ybtr
>
>2. Enter the values you are promted for and the project will be created
>
>3. edit `project.yeti` file to set the projects name, artifact id etc and add 
>   dependencies 
>	
>	 config = baseConfig ();
>    config.name := "first-test";
>    config.groupId :="org.foo";
>    config.artifactId := "first-test";
>    config.version := "0.1-alpha";
>    config.description := "First ybuilder based project";
>    
>	 config = createBaseConfig config
>    [ 
>    dependency "org.yeti" "yeti" "0.9.3" [],
>    dependency "org.apache.commons" "commons-lang3" "3.0.1" [],
>    dependency "junit" "junit" "3.8.2" [TestScope()],
>    dependency "javax.servlet" "servlet-api" "2.4" [ProvidedScope()],
>	 ];
>
>4. run `java -jar ybuilder.jar targets` to get help and see that 
>   project.yeti compiles
>
>5. add a source file to the src/ directory 
>    
>    file: src/foo.yeti:
>    application foo.yeti;
>    
>    println "Hello world!";
>    0;
>
>6. run it from source
>
>	>java -jar ybuilder.jar runyeti foo
>
>7. compile it
>
>   >java -jar ybuilder.jar compile
>   
>8. run it the class
>
>	>java -jar ybuilder.jar run foo
>
>9. jar it
>
>    >java -jar ybuilder.jar clean, jar
>    
>10. find the result in target/first-test.jar

### Directory Layout

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

### Defining the name, groudId, artifactId, version, description

`project.yeti` is a normal yeti programm. In this yeti program first a `config`
struct is created and customized, than targets are registered based on this 
config and finally the targets are run from the config.

Therefore to define the name etc first create a baseConfig and assign name etc
to the vars:

	config = baseConfig ();

    config.name := "yebspec";
    config.groupId :="org.yeb";
    config.artifactId := "yebspec";
    config.version := "0.1-alpha";
    config.description := "Specification tests for yeti";    

### Dependencies management

`ybuilder` uses [maven ant-tasks](http://maven.apache.org/ant-tasks/index.html)
for dependency resolution. It also uses the same build scopes and 
classpathes as maven (compile, provided, test, runtime).

Dependencies together with the classpathes are also added to the config struct. 
To do that convienently there is a special helper function :

	config = config with createBaseConfig config [
		dependency "org.yeti" "yeti" "0.9.3" [],
		dependency "org.apache.commons" "commons-lang3" "3.0.1" [],
		dependency "junit" "junit" "3.8.2" [TestScope()],
		dependency "javax.servlet" "servlet-api" "2.4" [ProvidedScope()]];
		
The scopes of the dependency is defined in the list of optional arguments. The 
scopes are the same as in maven (CompileScope, TestScope, RuntimeScope, 
SystemScope, ProvidedScope). The default scope is `CompileScope`.

To retrieve the dependencies you *must execute* the retrieveDependencies 
target. Just adding the dependencies has no effect.

	>java -jar ybuilder.jar retrieveDependencies

Ybuilder does not retrieve dependencies on each compile because this would take
too long.

#### Remote Repositories 

To add additional remote repositories add to the list of dependencies 

    remoteRepository id repositoryURL

ie

	config = config with createBaseConfig config [
		remoteRepository "springsource-release" 
				 "http://repository.springsource.com/maven/bundles/release",
		
		dependency "org.yeti" "yeti" "0.9.3" [],
		dependency "org.apache.commons" "commons-lang3" "3.0.1" [],
		dependency "junit" "junit" "3.8.2" [TestScope()],
		dependency "javax.servlet" "servlet-api" "2.4" [ProvidedScope()]];
    
The chrisichris github repository is already included by default

#### Using unmanaged dependencies

If you do not want to use the maven dependency mechanism or you need artifacts
which are not in a maven-repo than copy the jars to `lib/unmanaged`. `ybuilder`
will put all jars in this directory to all build-pathes. 
   
### Livecycle targets

Similar to maven the `ybuilder.core.base` module defines different predefined 
targets which form a linear dependency build-order.

Livecycles are just normal predefined targets.

These are thought to make extension of the core build-process easier. Ie
if yout want to add some target which depends on the result of the compile
process you just make it depend on the `compile` livecycle. 

### The predefined livecycle targets:    
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


## Building ybuilder from source

The main sourcode is in `ybuilder/core` dir.

To build it use:

    java -Xmx512M -cp "lib/unmanaged/*" yeti.lang.compiler.yeti project_ybuilder.yeti clean, jary

or on Windows use the ybuilder.bat file

    ybuilder clean, jary

if you want the resulting `ybulder-lib-0.6.jar`copied to user-home .ybuilder
repostiory for the launcher use the `installjar` target instead of `jary`

	ybuilder clean, installjar
 
 
    
 



    
