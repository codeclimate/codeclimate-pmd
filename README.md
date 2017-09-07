# Code Climate PMD Engine

`codeclimate-pmd` is a Code Climate engine that wraps the [PMD](https://pmd.github.io) static analysis tool. You can run it on your command line using the Code Climate CLI, or on our hosted analysis platform.

### Installation

1. If you haven't already, [install the Code Climate CLI](https://github.com/codeclimate/codeclimate).
2. Run `codeclimate engines:enable pmd`. This command both installs the engine and enables it in your `.codeclimate.yml` file.
3. You're ready to analyze! Browse into your project's folder and run `codeclimate analyze`.

### Need help?

For help with PMD, [check out their documentation](https://pmd.github.io/).

If you're running into a Code Climate issue, first check out [our PMD engine docs][cc-docs-pmd] and look over this project's [GitHub Issues](https://github.com/codeclimate/codeclimate-rubocop/issues),
as your question may have already been covered. If not, [go ahead and open a support ticket with us](https://codeclimate.com/help).

[cc-docs-pmd]: https://docs.codeclimate.com/docs/pmd
