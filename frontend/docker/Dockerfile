# build stage
FROM node:lts-alpine as build-stage
ARG VUE_APP_ROOT_API
ARG BASE_URL


WORKDIR /app
COPY package.json ./
ENV VUE_APP_ROOT_API=$VUE_APP_ROOT_API
ENV BASE_URL=$BASE_URL



RUN npm install
COPY . .
RUN npm run build

# production stage
FROM nginx:stable-alpine as production-stage
COPY docker/nginx/nginx.conf /etc/nginx/nginx.conf
COPY docker/nginx/default.conf /etc/nginx/conf.d
COPY --from=build-stage /app/dist /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]