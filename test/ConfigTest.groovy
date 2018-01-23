import static org.junit.Assert.*
import org.junit.*

class ConfigTest {
  @Test
  public void defaultRuleSet() {
    def config = new Config([configFile: "/usr/src/app/fixtures/default/config.json", codeFolder: "/usr/src/app/fixtures/default"])
    assertEquals "/usr/src/app/java-basic-default-ruleset.xml", config.ruleSet()
  }

  @Test
  public void specifiedRuleSetFile() {
    def config = new Config([configFile: "/usr/src/app/fixtures/specified_file/config.new.json", codeFolder: "/usr/src/app/fixtures/specified_file"])
    assertEquals "/usr/src/app/fixtures/specified_file/rules.xml", config.ruleSet()
  }

  @Test
  public void honorPresentRules() {
    def config = new Config([configFile: "/usr/src/app/fixtures/ruleset_default_file/config.json", codeFolder: "/usr/src/app/fixtures/ruleset_default_file"])
    assertEquals "/usr/src/app/fixtures/ruleset_default_file/ruleset.xml", config.ruleSet()
  }

  @Test(expected = IllegalArgumentException.class)
  public void doesNotAllowToMixRulesAndFile() {
    def config = new Config([configFile: "/usr/src/app/fixtures/rules/config.mix.json", codeFolder: "/usr/src/app/fixtures/rules"])
    config.ruleSet()
  }

  @Test
  public void acceptRulesSimpleNames() {
    def config = new Config([configFile: "/usr/src/app/fixtures/rules/config.json", codeFolder: "/usr/src/app/fixtures/rules"])
    assertEquals "java-basic,java-design", config.ruleSet()
  }
}
