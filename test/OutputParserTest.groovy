import static org.junit.Assert.*
import org.junit.*

class OutputParserTest {
  def out
  def err
  def parser

  @Before
  public void setup() {
    out = new ByteArrayOutputStream()
    err = new ByteArrayOutputStream()

    def outStream = new PrintStream(out)
    def errStream = new PrintStream(err)

    parser = new OutputParser(outStream, errStream)
  }

  @Test
  public void redirectNonJsonInput() {
    parser.out.print("This is a warning")
    assert out.toString("UTF-8").isEmpty()
    assertEquals "This is a warning", err.toString("UTF-8")
  }

  @Test
  public void printJsonLines() {
    parser.out.print("{}")
    assert err.toString("UTF-8").isEmpty()
    assertEquals "{}", out.toString("UTF-8")
  }

  @Test
  public void removeDeveloperPropertiesFromReadup() {
    def issue = '''
    {
      "type": "issue",
        "check_name": "AvoidMultipleUnaryOperators",
        "description": "Using multiple unary operators may be a bug, and/or is confusing.",
        "content": {
          "body": "## AvoidMultipleUnaryOperators\n\nSince: PMD 4.2\n\nPriority: Medium High\n\n[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\n\n[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\n\nThe use of multiple unary operators may be problematic, and/or confusing. Ensure that the intended usage is not a bug, or consider simplifying the expression.\n\n### Example:\n\n```java\n\n\n// These are typo bugs, or at best needlessly complex and confusing:\nint i = - -1;\nint j = + - +1;\nint z = ~~2;\nboolean b = !!true;\nboolean c = !!!true;\n\n// These are better:\nint i = 1;\nint j = -1;\nint z = 2;\nboolean b = true;\nboolean c = false;\n\n// And these just make your brain hurt:\nint i = ~-2;\nint j = -~7;\n\n \n``` \n\n### [PMD properties](http://pmd.github.io/pmd-6.0.1/customizing/pmd-developer.html)\n\nName | Value | Description\n--- | --- | ---\nviolationSuppressRegex | | Suppress violations with messages matching a regular expression\nviolationSuppressXPath | | Suppress violations on nodes which match a given relative XPath expression.\n"
        },
        "categories": [
          "Style"
        ],
        "location": {
          "path": "src/main/java/com/google/common/collect/Lists.java",
          "lines": {
            "begin": 1007,
            "end": 1007
          }
        },
        "severity": "normal",
        "remediation_points": 50000
    }
    '''
    def expectedIssue = '''
    {
      "type": "issue",
        "check_name": "AvoidMultipleUnaryOperators",
        "description": "Using multiple unary operators may be a bug, and/or is confusing.",
        "content": {
          "body": "## AvoidMultipleUnaryOperators\n\nSince: PMD 4.2\n\nPriority: Medium High\n\n[Categories](https://github.com/codeclimate/spec/blob/master/SPEC.md#categories): Style\n\n[Remediation Points](https://github.com/codeclimate/spec/blob/master/SPEC.md#remediation-points): 50000\n\nThe use of multiple unary operators may be problematic, and/or confusing. Ensure that the intended usage is not a bug, or consider simplifying the expression.\n\n### Example:\n\n```java\n\n\n// These are typo bugs, or at best needlessly complex and confusing:\nint i = - -1;\nint j = + - +1;\nint z = ~~2;\nboolean b = !!true;\nboolean c = !!!true;\n\n// These are better:\nint i = 1;\nint j = -1;\nint z = 2;\nboolean b = true;\nboolean c = false;\n\n// And these just make your brain hurt:\nint i = ~-2;\nint j = -~7;\n\n \n``` \n\n"
        },
        "categories": [
          "Style"
        ],
        "location": {
          "path": "src/main/java/com/google/common/collect/Lists.java",
          "lines": {
            "begin": 1007,
            "end": 1007
          }
        },
        "severity": "normal",
        "remediation_points": 50000
    }
    '''

    parser.out.print(issue)

    assert err.toString("UTF-8").isEmpty()
    assertEquals expectedIssue, out.toString("UTF-8")
  }
}
