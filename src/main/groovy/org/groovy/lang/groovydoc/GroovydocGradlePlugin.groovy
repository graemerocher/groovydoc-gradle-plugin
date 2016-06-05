package org.groovy.lang.groovydoc

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.groovy.lang.groovydoc.tasks.GroovydocTask

/**
 * @author Graeme Rocher
 * @since 1.0
 */
@CompileStatic
class GroovydocGradlePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

        JavaPluginConvention plugin = project.getConvention().getPlugin(JavaPluginConvention)
        def sourceSets = plugin?.sourceSets
        SourceSet mainSourceSet = sourceSets.find { SourceSet ss -> ss.name == SourceSet.MAIN_SOURCE_SET_NAME }

        if(mainSourceSet != null) {
            GroovydocTask groovydocTask = (GroovydocTask)project.tasks.create(type:GroovydocTask, name:"groovydoc", overwrite:true)

            groovydocTask.source = project.files(mainSourceSet.allSource.srcDirs).singleFile
            groovydocTask.destinationDir = new File(project.buildDir, "docs/groovydoc")
            groovydocTask.classpath = project.configurations.getByName("compile")
        }


    }
}
