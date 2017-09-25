import static org.junit.Assert.*
import org.junit.*

class ConfigTest {
  @Test
  public void defaultRuleSet() {
    def config = new Config([configFile: "/usr/src/app/fixtures/default/config.json", codeFolder: "/usr/src/app/fixtures/default"])
    assertEquals config.ruleSet(), "/usr/src/app/ruleset.xml"
  }

  @Test
  public void specifiedRuleSetFile() {
    def config = new Config([configFile: "/usr/src/app/fixtures/specified_file/config.new.json", codeFolder: "/usr/src/app/fixtures/specified_file"])
    assertEquals config.ruleSet(), "/usr/src/app/fixtures/specified_file/rules.xml"
  }

  @Test
  public void honorPresentRules() {
    def config = new Config([configFile: "/usr/src/app/fixtures/ruleset_default_file/config.json", codeFolder: "/usr/src/app/fixtures/ruleset_default_file"])
    assertEquals config.ruleSet(), "/usr/src/app/fixtures/ruleset_default_file/ruleset.xml"
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAllowToMixRulesAndFile() {
    def config = new Config([configFile: "/usr/src/app/fixtures/rules/config.mix.json", codeFolder: "/usr/src/app/fixtures/rules"])
    config.ruleSet()
  }

  @Test
  public void acceptRulesSimpleNames() {
    def config = new Config([configFile: "/usr/src/app/fixtures/rules/config.json", codeFolder: "/usr/src/app/fixtures/rules"])
    assertEquals config.ruleSet(), "java-basic,design"
  }
}
