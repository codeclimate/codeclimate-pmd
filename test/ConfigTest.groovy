import static org.junit.Assert.*
import org.junit.*

class ConfigTest {
  @Test
  public void defaultRuleSet() {
    def config = new Config([configFile: "/usr/src/app/fixtures/config.json", codeFolder: "/usr/src/app/fixtures"])
    assertEquals config.ruleSet(), "/usr/src/app/ruleset.xml"
  }

  @Test
  public void specifiedRuleSetFile() {
    def config = new Config([configFile: "/usr/src/app/fixtures/config.rule.json", codeFolder: "/usr/src/app/fixtures"])
    assertEquals config.ruleSet(), "/usr/src/app/fixtures/specified_rules.xml"
  }

  @Test
  public void honorPresentRules() {
    def config = new Config([configFile: "/usr/src/app/fixtures/config.json", codeFolder: "/usr/src/app/fixtures/honor"])
    assertEquals config.ruleSet(), "/usr/src/app/fixtures/honor/ruleset.xml"
  }
}
