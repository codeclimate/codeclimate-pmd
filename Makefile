.PHONY: image test

IMAGE_NAME ?= codeclimate/codeclimate-pmd

image:
	docker build --rm -t $(IMAGE_NAME) .

test: image
	docker run --rm -v $(PWD):/code $(IMAGE_NAME) /code/test.groovy

upgrade:
	docker run --rm \
		--workdir /usr/src/app \
		--volume $(PWD):/usr/src/app \
		$(IMAGE_NAME) ./bin/upgrade.sh
