FROM tiangolo/uwsgi-nginx-flask:python3.8
# RUN apk --update add bash nano
ENV STATIC_URL /static
ENV STATIC_PATH /var/www/app/static

RUN apt update
RUN apt install -y docker-compose

COPY ./src/requirements.txt /var/www/requirements.txt
RUN pip install -r /var/www/requirements.txt