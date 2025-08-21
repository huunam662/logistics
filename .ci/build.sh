#!/bin/bash
sudo docker logout registry.gitlab.com
echo "$CI_REGISTRY_PW" | sudo docker login registry.gitlab.com -u "$CI_REGISTRY_USER" --password-stdin
# sudo docker buildx build --push --platform=linux/amd64 -t $image_name:${CI_PIPELINE_ID} --cache-to type=registry,ref=$image_name/cache-image,mode=max --cache-from type=registry,ref=$image_name/cache-image -f $docker_path/Dockerfile $root
sudo docker buildx build --platform=linux/amd64 -t $image_name:${CI_PIPELINE_ID} -f ./Dockerfile $root
sudo docker images ls
sudo docker push $image_name:${CI_PIPELINE_ID}
