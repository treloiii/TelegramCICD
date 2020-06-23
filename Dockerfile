FROM docker

#ARG MYSQL_HOST
#ARG MYSQL_USER
#ARG MYSQL_PASSWORD
ARG BOT_TOKEN
ARG PORT

#ENV MYSQL_HOST=$MYSQL_HOST
#ENV MYSQL_USER=$MYSQL_USER
#ENV MYSQL_PASSWORD=$MYSQL_PASSWORD
ENV BOT_TOKEN=$BOT_TOKEN
ENV PORT=$PORT
#25b8397643ea8fbdd22282f15f1e4086b344ce49
RUN apk add openjdk8 \
    && apk add git \
    && apk add maven \
    && apk add nodejs \
    && apk add npm \
    && mkdir -p ci-bot \
    && mkdir -p ci-bot/data/logs

WORKDIR /ci-bot

#VOLUME /ci-bot

COPY ./target/ci-bot-0.0.1.jar /ci-bot/bot.jar

EXPOSE $PORT

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Xmx256m -Xss512k -XX:MetaspaceSize=100m -jar bot.jar