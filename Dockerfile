FROM groovy:jre8-alpine

MAINTAINER "Code Climate <hello@codeclimate.com>"

USER root

RUN apk update && \
    apk add ca-certificates wget curl jq && \
    update-ca-certificates

COPY ./bin /usr/src/app/bin
RUN /usr/src/app/bin/install-pmd.sh

RUN adduser -u 9000 -D app

VOLUME /code
WORKDIR /code
COPY . /usr/src/app

USER app

CMD ["/usr/src/app/pmd.groovy", "--codeFolder=/code","--configFile=/config.json"]
