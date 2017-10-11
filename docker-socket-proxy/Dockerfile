FROM alpine:latest

RUN apk --update add socat && \
    rm -rf /var/cache/apk/* && \
    rm -rf /root/.cache

ENTRYPOINT ["socat", "-d", "-d", "TCP-L:2375,fork", "UNIX:/var/run/docker.sock"]
