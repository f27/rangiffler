FROM node:19.4.0-alpine as build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . ./
RUN sed -i 's/\r$//' ./.env
RUN sed -i 's/\r$//' ./.env.docker
RUN npm run build:docker

# release step
FROM nginx:1.23.3-alpine as release
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]