package org.groovy.lang.groovydoc.tasks

import groovy.transform.CompileStatic
import org.apache.commons.cli.ParseException
import org.codehaus.groovy.tools.groovydoc.Main
import org.gradle.api.Action
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths

/**
 * A Groovydoc task that forks the JVM instead of running inprocess like the default Gradle impl
 *
 * Note, this implementation doesn't currently support links due to limitations with {@link Main}
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@CompileStatic
class GroovydocTask extends JavaExec {

    @OutputDirectory
    File destinationDir

    @Input
    @Optional
    String windowTitle

    @Input
    @Optional
    String docTitle

    @Input
    @Optional
    String header

    @Input
    @Optional
    String footer

    @Input
    @Optional
    String overview

    @Input
    @Optional
    Boolean notimestamp

    @Input
    @Optional
    Boolean noversionstamp

    @Input
    @Optional
    Boolean nomainforscripts


    @Input
    @Optional
    Boolean noscripts

    @Input
    @Optional
    Boolean verbose

    @Input
    @Optional
    Boolean debugOutput

    @Input
    @Optional
    Boolean quiet

    @Input
    @Optional
    File stylesheetFile

    @Input
    @Optional
    FileCollection groovyClasspath

    @Input
    @Optional
    String charset

    protected final List<Object> allSource = new ArrayList();

    GroovydocTask(){
        setMain(Main.name)
    }

    @InputFiles
    @SkipWhenEmpty
    FileTree getSource() {
        List copy = new ArrayList(this.allSource);
        FileTree src = this.getProject().files(copy).getAsFileTree();
        return src == null?this.getProject().files(new Object[0]).getAsFileTree():src
    }

    void setSource(Object source) {
        allSource.clear()
        allSource.add(source)
    }

    public GroovydocTask source(Object... sources) {
        allSource.addAll(Arrays.asList(sources))
        return this
    }

    @Override
    @TaskAction
    void exec() {
        classpath(findJarFile(ParseException))
        if(groovyClasspath != null) {
            classpath(groovyClasspath)
        }

        if( !classpath.files.find { File f -> f.name.contains('groovy-all')} ) {
            classpath(findJarFile(Main))
        }
        args(
            "-d", destinationDir.canonicalPath)

        if(windowTitle != null) {
            args("-windowtitle", windowTitle)
        }
        if(docTitle != null) {
            args("-doctitle", docTitle)
        }
        if(header != null) {
            args("-header", header)
        }
        if(footer != null) {
            args("-footer", footer)
        }
        if(overview != null) {
            args("-overview", overview)
        }
        if(quiet) {
            args("-quiet")
        }
        if(notimestamp) {
            args("-notimestamp")
        }
        if(noversionstamp) {
            args("-noversionstamp")
        }
        if(nomainforscripts) {
            args("-nomainforscripts")
        }
        if(noscripts) {
            args("-noscripts")
        }
        if(debugOutput) {
            args("-debug")
        }
        if(verbose) {
            args("-verbose")
        }
        if(charset) {
            args("-charset", charset)
        }

        if(stylesheetFile != null) {
            args("-stylesheetfile", stylesheetFile.canonicalPath)
        }

        final File tmpDir = new File(project.getBuildDir(), "tmp/groovydoc")

        project.copy(new Action<CopySpec>() {
            @Override
            void execute(CopySpec copySpec) {
                copySpec.from(source)
                        .into(tmpDir)
            }
        })
        args('-sourcepath', tmpDir.canonicalPath)
        args(calculatePackages(tmpDir))
        try {
            super.exec()
        } finally {
            tmpDir.deleteOnExit()
        }
    }

    Object[] calculatePackages(File dir) {
        List packages = []
        String path = ""
        calculatePackages(dir, packages, path)

        return packages as Object[]
    }

    public void calculatePackages(File dir, List packages, String path) {
        dir.eachDir { File subDir ->
            def packagePath = "${path}${subDir.name}".toString()
            if (!subDir.isHidden() && !subDir.name.startsWith('.')) {
                packages.add(packagePath)
            }
            calculatePackages(subDir, packages, "${packagePath}.")
        }
    }

    /**
     * Finds a JAR file for the given class
     * @param targetClass The target class
     * @return The JAR file
     */
    static File findJarFile(Class targetClass) {
        def resource = findClassResource(targetClass)
        findJarFile(resource)
    }

    /**
     * Returns the URL resource for the location on disk of the given class or null if it cannot be found
     *
     * @param targetClass The target class
     * @return The URL to class file or null
     */
    static URL findClassResource(Class targetClass) {
        targetClass.getResource('/' + targetClass.name.replace(".", "/") + ".class")
    }

    /**
     * Finds a JAR for the given resource
     *
     * @param resource The resource
     * @return The JAR file or null if it can't be found
     */
    static File findJarFile(URL resource) {
        def absolutePath = resource?.path
        if (absolutePath) {
            try {
                return Paths.get(new URL(absolutePath.substring(0, absolutePath.lastIndexOf("!"))).toURI()).toFile()
            } catch (MalformedURLException e) {
                return null
            }
        }
    }
}
