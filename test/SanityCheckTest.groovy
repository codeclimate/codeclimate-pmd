import groovy.json.JsonSlurper
import groovy.util.FileNameFinder

import static org.junit.Assert.*
import org.junit.*

class CustomIO {
  def byteStream
  def printStream

  CustomIO() {
    byteStream = new ByteArrayOutputStream()
    printStream = new PrintStream(byteStream)
  }

  public String toString() {
    return byteStream.toString("UTF-8")
  }
}

class SanityCheckTest {
  def execute(command) {
    def proc = command.execute()
    def outIO = new CustomIO()
    def errIO = new CustomIO()

    proc.waitForProcessOutput(outIO.printStream, errIO.printStream)

    return [proc, outIO, errIO]
  }

  @Test
  void sanityCheck() {
    def (proc, out, err) = execute("/usr/src/app/pmd --codeFolder=/usr/src/app/fixtures/default --configFile=/usr/src/app/fixtures/default/config.json")

    assert !out.toString().isEmpty()
    assert proc.exitValue() == 0
  }

  @Test
  void checkConfigBackwardCompatibility() {
    def (proc, out, _err) = execute("/usr/src/app/pmd --codeFolder=/usr/src/app/fixtures/specified_file --configFile=/usr/src/app/fixtures/specified_file/config.new.json")
    def (procOld, outOld, _errOld) = execute("/usr/src/app/pmd --codeFolder=/usr/src/app/fixtures/specified_file --configFile=/usr/src/app/fixtures/specified_file/config.old.json")

    def expectedIssue = "Avoid modifying an outer loop incrementer in an inner loop for update expression"

    assert proc.exitValue() == 0
    assert proc.exitValue() == procOld.exitValue()
    assert out.toString().contains(expectedIssue)
    assert outOld.toString().contains(expectedIssue)
  }

  @Test
  void abortOnBadConfig() {
    def (proc, out, err) = execute("/usr/src/app/pmd --codeFolder=/usr/src/app/fixtures/bad_config --configFile=/usr/src/app/fixtures/bad_config/config.json")

    assert !err.toString().isEmpty()
    assert proc.exitValue() != 0
  }

  @Test
  void engineCheckList() {
    def engine = new JsonSlurper().parse(new File("engine.json"), "UTF-8")
    assert engine.name
    assert engine.description
    assert engine.description.size() <= 140
    assert engine.maintainer && engine.maintainer.name && engine.maintainer.email
    assert engine.languages.size() > 0
    assert engine.version
  }

  @Test
  void dockerfileCheckList() {
    def dockerfile = new File("Dockerfile").text

    assert dockerfile.contains("MAINTAINER")
    assert dockerfile.contains("VOLUME")
    assert dockerfile.contains("WORKDIR")
    assert dockerfile.contains("USER")
    assert dockerfile.contains("CMD")

    assert !dockerfile.toUpperCase().contains("EXPOSE")
    assert !dockerfile.toUpperCase().contains("ONBUILD")
    assert !dockerfile.toUpperCase().contains("ARG")

    assert dockerfile.split("\n")[-1].startsWith("CMD")
  }
}
