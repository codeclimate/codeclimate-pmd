.PHONY: image test docs

IMAGE_NAME ?= codeclimate/codeclimate-pmd

image:
	docker build --rm -t $(IMAGE_NAME) .

test: image
	docker run --rm $(IMAGE_NAME) sh -c "cd /usr/src/app && bundle exec rake"

