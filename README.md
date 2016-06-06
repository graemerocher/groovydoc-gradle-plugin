Groovydoc Gradle Plugin
=======================

This is an alternative Groovydoc plugin for Gradle that forks the JVM.
 
Instead of generating the docs in the same JVM as Gradle, which is prone to cause conflicts and place strain on the memory consumption of the Gradle process, this plugin will fork a separate JVM for Groovydoc generation.

Generally this plugin is a drop in replacement for the regular Groovydoc generator, with the only limitation being that Groovydoc links are not curently supported. 

To use the plugin add the following to your buildscript classpath:

```groovy
classpath 'io.github.groovylang.groovydoc:groovydoc-gradle-plugin:1.0.0-SNAPSHOT'
```

Then apply the plugin:

```groovy
apply plugin:"org.groovy.lang.groovydoc"
```

You can also manually configure Groovydoc tasks using the `org.groovy.lang.groovydoc.tasks.GroovydocTask` class:

```groovy
task groovydoc(type:GroovydocTask) {
    docTitle = "My Title"
    source = "src/main/groovy"
    destinationDir = "build/api"
    classpath = configurations.compile
}
```

If you wish to alter the memory requirements of the task you can do so using the `jvmArgs`:

```groovy
groovydoc.jvmArgs "-Xmx512m"
```