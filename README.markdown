Ybuilder - a simple build tool for Yeti the funtional language for JVM
==========================================================

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

## Download and Installation

There is no real installation. Just download the `ybuilder.jar` from the
downloads section at github/ybuilder
 
<https://github.com/chrisichris/ybuilder/downloads> 

and copy it to the root-directory of your project. 

You can than run ybuilder with
 
    java -jar ybuilder

## Support

Join the discussion mailing list at:

<http://groups.google.com/group/yeti-lang>
 
    

## Ybuilder basics

### Targets
Every ybuilder project is - like an ant-project - made up of one or more 
targets which can depend on each other. Each target represent a unit of work ie.
init, compile etc. which is executed only once during a build. 

### Hello World

You run a ybuilder project using the `ybuilder.jar`, which in turn executes 
the `project.yeti` (the build script) which insantiates and configures 
the targets of the project.

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

When from the command-line the `ybuilder.jar` is executed it looks for the
`project.yeti` file (the build-script), adds the yeti.jar, ant.jar 
to the classpath and executes build-script.

The build-script first creates a `config` structure. This
config structure is than used to create the target with the `target` function.

The target function is than used to register in the config-structure 
a new target `hello:world` which prints "Hello World!" when it is excuted.
. takes the config structure, a group name, the target's 
name within the group, a list of options (ie dependencies, description etc), 
and than a function which gets executed when the target is executed.

The `config` now contains the target. At the end of the build-script 
the `config`, which contains now the `hello:world` is given to the 
`run` function.

The `run` function reads from the command-line-args the name of the target
to execute (`hello:world`) and than looks up the target by name in the `config`
and than executes it.

That's it.

### Buildscripts are yeti code
All `project.yeti` files are normal yeti programms. You can use all the
language constructs to construct them.

### Targets
As seen the target function is used to register targets with config.

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

### Using Ant Tasks

Ybuilder provides excellent integration for Ant tasks.

The module `yubilder.core.build`contains the function antTask which is used
to define and execute an ant task, where similar to what you would do in
xml.

It takes following arguments:
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

The `project` struct is either gotten from the argument to the target function:
	
	target config "group" "name" [] do project:
		antTask "mkdir" ["dir":"temp"] [] project;
	done;

or it can be created directly in the config-script, in which case the
ant-task is executed immidiately when the config-script is evaluated.

	antTask "mkdir" ["dir":"temp"] [] (newProject());

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


##Building ybuilder from source

The main sourcode is in `ybuilder/core` dir.

To build it use:

    java -Xmx512M -cp "lib/unmanaged/*" yeti.lang.compiler.yeti project_ybuilder.yeti clean, jary

or on Windows use the ybuilder.bat file

    ybuilder clean, jary
 
 
    
 



    
