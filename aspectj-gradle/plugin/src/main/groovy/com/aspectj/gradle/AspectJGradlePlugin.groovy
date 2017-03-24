import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task
import org.gradle.api.GradleException

class AspectJGradlePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('compile-aspects') {
            println "called form your own dear plugin"
            if (!project.hasProperty('sourceCompatibility')) {
                throw new GradleException("You must set the property 'sourceCompatibility' before applying the aspectj plugin")
            }
            if (!project.hasProperty('targetCompatibility')) {
                throw new GradleException("You must set the property 'targetCompatibility' before applying the aspectj plugin")
            }

            if (project.configurations.findByName('aspects') == null) {
                project.configurations.create('aspects')
                project.configurations.create('ajc')
                project.dependencies {
                    aspects "io.astefanutti.metrics.aspectj:metrics-aspectj:1.1.1"
                    compile "io.astefanutti.metrics.aspectj:metrics-aspectj-deps:1.1.1"
                    ajc "org.aspectj:aspectjtools:1.8.10"
                }
            }
            doLast {
                ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties",
                        classpath: project.configurations.ajc.asPath)

                ant.iajc(
                        fork: "false",
                        showWeaveInfo: "true",
                        verbose: "true",
                        source: project.sourceCompatibility,
                        target: project.targetCompatibility,
                        failonerror: "true",
                        destDir: project.sourceSets.main.output.classesDir.absolutePath,
                        aspectPath: project.configurations.aspects.asPath,
                        inpath: project.sourceSets.main.output.classesDir.absolutePath,
                        classpath: project.sourceSets.main.runtimeClasspath.asPath,
                )
            }
        }
        project.tasks["compileJava"].deleteAllActions();
        project.tasks["compileJava"].dependsOn(project.tasks['compile-aspects'])
    }
}

