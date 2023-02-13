.PHONY: image test release

IMAGE_NAME ?= codeclimate/codeclimate-pmd
RELEASE_REGISTRY ?= codeclimate

ifndef RELEASE_TAG
override RELEASE_TAG = latest
endif

image:
	docker build --rm -t $(IMAGE_NAME) .

test: image
	docker run --rm --workdir /usr/src/app $(IMAGE_NAME) ./test.sh

upgrade:
	docker run --rm \
		--workdir /usr/src/app \
		--volume $(PWD):/usr/src/app \
		$(IMAGE_NAME) ./bin/upgrade.sh

release:
	docker tag $(IMAGE_NAME) $(RELEASE_REGISTRY)/codeclimate-phpmd:$(RELEASE_TAG)
	docker push $(RELEASE_REGISTRY)/codeclimate-phpmd:$(RELEASE_TAG)
