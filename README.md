Groovydoc Gradle Plugin
=======================

This is an alternative Groovydoc plugin for Gradle that forks the JVM.
 
Instead of generating the docs in the same JVM as Gradle, which is prone to cause conflicts and place strain on the memory consumption of the Gradle process, this plugin will fork a separate JVM for Groovydoc generation.

Generally this plugin is a drop in replacement for the regular Groovydoc generator, with the only limitation being that Groovydoc links are not curently supported. 

To use the plugin add the following to your buildscript classpath:

```groovy
repositories {
    maven { url "https://repo.grails.org/grails/core" }
}
dependencies {
    classpath 'io.github.groovylang.groovydoc:groovydoc-gradle-plugin:1.0.1'
}
```

Then apply the plugin:

```groovy
apply plugin:"io.github.groovylang.groovydoc"
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

If you wish to alter the memory requirements of the task you can do so using the `maxHeapSize`:

```groovy
groovydoc.maxHeapSize = "512m"
```

Or by passing whatever JVM arguments you want using `jvmArgs`:

```groovy
groovydoc.jvmArgs "-Xmx512m"
```

The `GroovydocTask` class extends Gradle's [JavaExec](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.JavaExec.html) class so only properties of that class including JVM arguments, classpath confifuration etc. can be configured as necessary.

Note that in order for the `GroovydocTask` to function Groovy itself needs to be on the classpath. If `groovy-all` is found on the classpath then the `GroovydocTool` from the `groovy-all` JAR is used, otherwise the `GroovydocTool` that this plugin depends on is used. If you only have the `groovy` on your classpath and want to use a different version of the `GroovydocTool` then you can configure an alternative version as follows:

```groovy
configurations { documentation }
dependencies {
   documentation "org.codehaus.groovy:groovy-groovydoc:2.4.6"
}

groovydoc.classpath += configurations.documentation
```
