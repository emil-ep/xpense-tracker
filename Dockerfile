FROM ubuntu:latest
LABEL authors="emil"

ENTRYPOINT ["top", "-b"]