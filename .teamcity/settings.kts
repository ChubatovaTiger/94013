import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.03"

project {

    vcsRoot(Parametrized_1)

    buildType(Build1)

    params {
        param("roadrunner.default.branch", "refs/heads/master")
        param("roadrunner.branch.spec", "+:refs/heads/(reviews/*)")
    }
}

object Build1 : BuildType({
    name = "build1"

    params {
        param("a", "1")
    }

    vcs {
        root(Parametrized_1)

        checkoutMode = CheckoutMode.MANUAL
    }

    steps {
        script {
            id = "simpleRunner"
            scriptContent = """
                if [ %a% -gt 0 ]; then
                echo "##teamcity[buildStop comment='Stop trigger. Not latest patchset' readdToQueue='false']"
                echo "##teamcity[buildStatus text='Not latest patchset']"
                fi
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = ""
            enableQueueOptimization = false
        }
    }
})

object Parametrized_1 : GitVcsRoot({
    id("Parametrized")
    name = "parametrized"
    url = "https://github.com/ChubatovaTiger/94013"
    branch = "%roadrunner.default.branch%"
    branchSpec = "%roadrunner.branch.spec%"
    authMethod = password {
        userName = "ChubatovaTiger"
        password = "credentialsJSON:8a89a428-4e80-4101-a4ae-396083bc3fc6"
    }
})
