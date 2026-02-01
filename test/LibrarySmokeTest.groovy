import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test

class DefaultsSmokeTest extends BasePipelineTest {

  def defaultsScript

  @Before
  void setUp() {
    super.setUp()

    // stub steps if any vars call them (safe to keep)
    helper.registerAllowedMethod('sh', [Map], { Map m -> "" })
    helper.registerAllowedMethod('sh', [String], { String s -> "" })

    binding.setVariable('env', [:])
    binding.setVariable('currentBuild', [currentResult: 'SUCCESS'])
    binding.setVariable('scm', null)

    defaultsScript = loadScript('vars/defaults.groovy')
  }

  @Test
  void 'override sets keepReleases in script-backed defaults'() {
    def builder = defaultsScript.override()
    builder.withKeepReleases(7)

    // Now load Defaults.of(script) using the same pipeline script binding
    def cfg = helpers.defaults.Defaults.of(this)
    assert cfg.getKeepReleases() == 7
  }
}