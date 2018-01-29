import groovy.util.*

class Main {
  static def execute(command) {
    OutputParser parser = new OutputParser(System.out, System.err)
    ProcessBuilder builder = new ProcessBuilder(command.split(' '))
    def env = builder.environment()
    env.put("JAVA_OPTS", "-XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=30")
    Process process = builder.start()
    process.consumeProcessOutput(parser.out, parser.err)
    process.waitFor()
    System.exit(process.exitValue())
  }

  static def appContext(args) {
    def cli = new CliBuilder(usage:"${this.class.name}")
    cli._(longOpt: "configFile", required: true, args: 1, "Path to config.json file")
    cli._(longOpt: "codeFolder", required: true, args: 1, "Path to code folder")
    cli.parse(args)
  }

  public static void main(args) {
    def config = new Config(appContext(args))
    if (config.noFiles()) {
      System.exit(0)
    }

    execute("/usr/src/app/lib/pmd/bin/run.sh pmd -cache /tmp/pmd-cache -filelist ${config.filesListPath()} -f codeclimate -R ${config.ruleSet()} -failOnViolation false")
  }
}
