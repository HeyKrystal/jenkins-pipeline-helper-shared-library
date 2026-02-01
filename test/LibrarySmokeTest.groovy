import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test

class LibrarySmokeTest extends BasePipelineTest {

  def script

  @Before
  void setUp() {
    super.setUp()

    // Stub pipeline steps you call from library code
    helper.registerAllowedMethod('sh', [Map], { Map m -> return "" })
    helper.registerAllowedMethod('sh', [String], { String s -> return "" })
    helper.registerAllowedMethod('withCredentials', [List, Closure], { List l, Closure c -> c.call() })
    helper.registerAllowedMethod('archiveArtifacts', [Map], { Map m -> null })
    helper.registerAllowedMethod('checkout', [Object], { Object o -> null })

    // Provide common pipeline globals used by your code
    binding.setVariable('env', [:])
    binding.setVariable('currentBuild', [currentResult: 'SUCCESS', startTimeInMillis: System.currentTimeMillis()])
    binding.setVariable('scm', null)

    // Load a vars/ script to ensure it parses and can be invoked
    script = loadScript('vars/defaults.groovy')
  }

  @Test
  void 'defaults.override returns builder'() {
    def b = script.override()
    assert b != null
  }
}