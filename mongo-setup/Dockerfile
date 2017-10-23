FROM mongo:3.4

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl iputils-ping dnsutils \
	&& rm -rf /var/lib/apt/lists/*

COPY setup.sh /setup.sh
RUN chmod +x /setup.sh

CMD /setup.sh
