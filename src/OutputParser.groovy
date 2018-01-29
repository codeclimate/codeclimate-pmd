
class OutputParser {
  public def out
  public def err

  OutputParser(out, err) {
    this.out = new OutStreamProcessor(out);
    this.err = err
  }

  class OutStreamProcessor extends PrintStream {
    public OutStreamProcessor(out) {
      super(out);
    }

    public void print(String line) {
      if(isJson(line)) {
        super.print(sanitize(line))
      } else {
        this.err.print(line)
      }
    }

    boolean isJson(txt) {
      return txt.trim().startsWith("{")
    }

    String sanitize(line) {
      return line.replaceFirst('(?s)### \\[PMD properties\\][^"]*', '')
    }
  }
}
