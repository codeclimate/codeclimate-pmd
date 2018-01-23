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
    assert "This is a warning", err.toString("UTF-8")
  }

  @Test
  public void printJsonLines() {
    parser.out.print("{}")
    assert err.toString("UTF-8").isEmpty()
    assert "{}", out.toString("UTF-8")
  }
}

